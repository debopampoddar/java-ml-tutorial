package com.debopam.example.ai.basics;

import org.nd4j.linalg.api.ndarray.INDArray;
import org.nd4j.linalg.factory.Nd4j;
import smile.data.vector.ValueVector;
import smile.data.DataFrame;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.util.Arrays;

/**
 * LEVEL 1: Converting Between Data Formats
 *
 * Learning Objectives:
 * - Convert Tablesaw → Smile DataFrame
 * - Convert Tablesaw → ND4j INDArray
 * - Convert ND4j → Smile DataFrame
 * - Understand when to use each format
 */
public class DataConversion {

    public static void main(String[] args) {
        System.out.println("=== Data Conversion Tutorial ===\n");

        example1_TablesawToSmile();
        example2_TablesawToND4j();
        example3_ND4jToSmile();
        example4_RoundTrip();
    }

    /**
     * Example 1: Tablesaw → Smile DataFrame
     *
     * When: After data loading/cleaning (Tablesaw), before ML (Smile)
     * Why: Smile ML algorithms require Smile DataFrames
     */
    private static void example1_TablesawToSmile() {
        System.out.println("--- Example 1: Tablesaw → Smile ---");

        // Create Tablesaw table
        Table tsTable = Table.create("data")
                .addColumns(
                        DoubleColumn.create("feature1", new double[]{1.0, 2.0, 3.0}),
                        DoubleColumn.create("feature2", new double[]{4.0, 5.0, 6.0}),
                        IntColumn.create("target", new int[]{0, 1, 0})
                );

        System.out.println("Original Tablesaw Table:");
        System.out.println(tsTable);

        // Convert to Smile DataFrame
        DataFrame smileDf = tablesawToSmile(tsTable);

        System.out.println("\nConverted to Smile DataFrame:");
        System.out.println(smileDf);
        System.out.println("Schema: " + smileDf.schema());
        System.out.println();
    }

    /**
     * Helper method: Tablesaw → Smile
     */
    private static DataFrame tablesawToSmile(Table table) {
        int rows = table.rowCount();
        int cols = table.columnCount();

        ValueVector[] vectors = new ValueVector[cols];

        for (int j = 0; j < cols; j++) {
            Column<?> col = table.column(j);
            String colName = col.name();

            if (col instanceof DoubleColumn) {
                double[] data = ((DoubleColumn) col).asDoubleArray();
                vectors[j] = ValueVector.of(colName, data);

            } else if (col instanceof IntColumn) {
                int[] data = ((IntColumn) col).asIntArray();
                vectors[j] = ValueVector.of(colName, data);

            } else if (col instanceof StringColumn) {
                String[] data = ((StringColumn) col).asObjectArray();
                vectors[j] = ValueVector.nominal(colName, data);
            }
        }

        return new DataFrame(vectors);
    }

    /**
     * Example 2: Tablesaw → ND4j INDArray
     *
     * When: Before numerical transformations (normalization, PCA, etc.)
     * Why: ND4j offers optimized matrix operations
     */
    private static void example2_TablesawToND4j() {
        System.out.println("--- Example 2: Tablesaw → ND4j ---");

        // Create numeric Tablesaw table
        Table tsTable = Table.create("matrix")
                .addColumns(
                        DoubleColumn.create("col1", new double[]{1, 2, 3}),
                        DoubleColumn.create("col2", new double[]{4, 5, 6}),
                        DoubleColumn.create("col3", new double[]{7, 8, 9})
                );

        System.out.println("Tablesaw Table:");
        System.out.println(tsTable);

        // Convert to ND4j
        INDArray ndArray = tablesawToND4j(tsTable);

        System.out.println("\nND4j INDArray:");
        System.out.println(ndArray);
        System.out.println("Shape: " + Arrays.toString(ndArray.shape()));
        System.out.println();
    }

    /**
     * Helper method: Tablesaw → ND4j
     */
    private static INDArray tablesawToND4j(Table table) {
        int rows = table.rowCount();
        int cols = table.columnCount();

        double[][] data = new double[rows][cols];

        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                // Extract numeric value from column
                Column<?> col = table.column(j);
                if (col instanceof DoubleColumn) {
                    data[i][j] = ((DoubleColumn) col).get(i);
                } else if (col instanceof IntColumn) {
                    data[i][j] = ((IntColumn) col).get(i);
                }
            }
        }

        return Nd4j.create(data);
    }

    /**
     * Example 3: ND4j → Smile DataFrame
     *
     * When: After ND4j transformations, before Smile ML
     * Why: Smile algorithms need labeled DataFrames
     */
    private static void example3_ND4jToSmile() {
        System.out.println("--- Example 3: ND4j → Smile ---");

        // Create ND4j array
        INDArray ndArray = Nd4j.create(new double[][]{
                {1.5, 2.5, 3.5},
                {4.5, 5.5, 6.5}
        });

        System.out.println("ND4j INDArray:");
        System.out.println(ndArray);

        // Convert to Smile with column names
        String[] colNames = {"x1", "x2", "x3"};
        DataFrame smileDf = nd4jToSmile(ndArray, colNames);

        System.out.println("\nSmile DataFrame:");
        System.out.println(smileDf);
        System.out.println();
    }

    /**
     * Helper method: ND4j → Smile
     */
    private static DataFrame nd4jToSmile(INDArray array, String[] colNames) {
        int rows = (int) array.rows();
        int cols = (int) array.columns();

        ValueVector[] vectors = new ValueVector[cols];

        for (int j = 0; j < cols; j++) {
            double[] column = new double[rows];
            for (int i = 0; i < rows; i++) {
                column[i] = array.getDouble(i, j);
            }
            vectors[j] = ValueVector.of(colNames[j], column);
        }

        return new DataFrame(vectors);
    }

    /**
     * Example 4: Complete round-trip conversion
     *
     * Demonstrates: Real workflow using all three libraries
     * 1. Load with Tablesaw
     * 2. Transform with ND4j
     * 3. Model with Smile
     */
    private static void example4_RoundTrip() {
        System.out.println("--- Example 4: Complete Round-Trip ---");

        // Step 1: Start with Tablesaw (simulating CSV load)
        System.out.println("Step 1: Load data with Tablesaw");
        Table original = Table.create("raw_data")
                .addColumns(
                        DoubleColumn.create("height", new double[]{170, 165, 180, 175}),
                        DoubleColumn.create("weight", new double[]{70, 60, 80, 75})
                );
        System.out.println(original);

        // Step 2: Convert to ND4j for normalization
        System.out.println("\nStep 2: Convert to ND4j for transformation");
        INDArray ndArray = tablesawToND4j(original);

        // Normalize: (x - mean) / std
        INDArray normalized = ndArray.subRowVector(ndArray.mean(0))
                .divRowVector(ndArray.std(0));
        System.out.println("Normalized:");
        System.out.println(normalized);

        // Step 3: Convert to Smile for ML
        System.out.println("\nStep 3: Convert to Smile for machine learning");
        DataFrame smileDf = nd4jToSmile(normalized,
                new String[]{"height_norm", "weight_norm"});
        System.out.println(smileDf);

        System.out.println("\n✓ Ready for Smile ML algorithms!");
        System.out.println();
    }
}
