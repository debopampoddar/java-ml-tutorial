package com.debopam.example.ai;

import smile.data.DataFrame;
import smile.math.matrix.Matrix;
import tech.tablesaw.api.Table;
import tech.tablesaw.api.DoubleColumn;
import org.nd4j.linalg.factory.Nd4j;
import org.nd4j.linalg.api.ndarray.INDArray;

/**
 * Comprehensive environment verification for Intel Mac
 * Tests all three core libraries and native dependencies
 */
public class EnvironmentTest {

    public static void main(String[] args) {
        System.out.println("=".repeat(60));
        System.out.println("Java Data Science Environment Test (Intel Mac)");
        System.out.println("=".repeat(60));

        boolean allPassed = true;

        // Test 1: System Information
        allPassed &= testSystemInfo();

        // Test 2: Smile
        allPassed &= testSmile();

        // Test 3: Tablesaw
        allPassed &= testTablesaw();

        // Test 4: ND4j with Native Backend
        allPassed &= testND4j();

        System.out.println("=".repeat(60));
        if (allPassed) {
            System.out.println("✓ ALL TESTS PASSED - Environment Ready!");
        } else {
            System.out.println("✗ SOME TESTS FAILED - Check configuration");
        }
        System.out.println("=".repeat(60));
    }

    private static boolean testSystemInfo() {
        System.out.println("\n[1] System Information:");
        try {
            System.out.println("    OS: " + System.getProperty("os.name"));
            System.out.println("    Architecture: " + System.getProperty("os.arch"));
            System.out.println("    Java Version: " + System.getProperty("java.version"));

            // Check if Intel Mac
            String arch = System.getProperty("os.arch");
            if (!arch.contains("x86_64")) {
                System.out.println("    ⚠ WARNING: Not Intel x86_64 architecture!");
                System.out.println("    This tutorial is optimized for Intel Mac");
            }

            System.out.println("    ✓ System info retrieved");
            return true;
        } catch (Exception e) {
            System.out.println("    ✗ Failed: " + e.getMessage());
            return false;
        }
    }

    private static boolean testSmile() {
        System.out.println("\n[2] Testing Smile:");
        try {
            // Test DataFrame creation
            double[][] data = {{1.0, 2.0, 3.0}, {4.0, 5.0, 6.0}};
            DataFrame df = DataFrame.of(data);
            System.out.println("    DataFrame shape: " + df.nrow() + "×" + df.ncol());

            // Test Matrix operations
            Matrix matrix = Matrix.of(data);
            Matrix transposed = matrix.transpose();
            System.out.println("    Matrix transposed: " + transposed.nrow() + "×" + transposed.ncol());

            // Get version info
            Package smilePkg = DataFrame.class.getPackage();
            String version = (smilePkg != null) ? smilePkg.getImplementationVersion() : "Available";
            System.out.println("    Version: " + version);

            System.out.println("    ✓ Smile working correctly");
            return true;
        } catch (Exception e) {
            System.out.println("    ✗ Failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testTablesaw() {
        System.out.println("\n[3] Testing Tablesaw:");
        try {
            // Create a sample table
            Table table = Table.create("test")
                    .addColumns(
                            DoubleColumn.create("feature1", new double[]{1.1, 2.2, 3.3}),
                            DoubleColumn.create("feature2", new double[]{4.4, 5.5, 6.6})
                    );

            System.out.println("    Table dimensions: " + table.rowCount() + "×" + table.columnCount());

            // Test aggregation
            double mean = table.doubleColumn("feature1").mean();
            System.out.println("    Mean of feature1: " + mean);

            System.out.println("    ✓ Tablesaw working correctly");
            return true;
        } catch (Exception e) {
            System.out.println("    ✗ Failed: " + e.getMessage());
            e.printStackTrace();
            return false;
        }
    }

    private static boolean testND4j() {
        System.out.println("\n[4] Testing ND4j (with native backend):");
        try {
            // Test array creation
            INDArray array = Nd4j.create(new double[][]{{1, 2, 3}, {4, 5, 6}});
            System.out.println("    Array shape: " + java.util.Arrays.toString(array.shape()));

            // Test matrix multiplication (uses OpenBLAS)
            INDArray result = array.mmul(array.transpose());
            System.out.println("    Matrix multiplication result shape: " +
                    java.util.Arrays.toString(result.shape()));

            // Verify native backend
            String backend = Nd4j.getBackend().getClass().getSimpleName();
            System.out.println("    Backend: " + backend);

            // Test element-wise operations
            INDArray normalized = array.div(array.maxNumber());
            System.out.println("    Normalization successful");

            System.out.println("    ✓ ND4j with native backend working correctly");
            return true;
        } catch (Exception e) {
            System.out.println("    ✗ Failed: " + e.getMessage());
            System.out.println("    Common cause: Missing or wrong platform-specific ND4j/OpenBLAS");
            e.printStackTrace();
            return false;
        }
    }
}

