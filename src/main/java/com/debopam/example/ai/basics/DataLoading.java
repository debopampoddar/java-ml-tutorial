package com.debopam.example.ai.basics;

import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.io.csv.CsvReadOptions;

/**
 * LEVEL 1: Loading Data from Files
 * <p>
 * Learning Objectives:
 * - Load CSV files with proper configuration
 * - Handle missing values
 * - Inspect data after loading
 * - Understand common loading issues
 */
public class DataLoading {

    public static void main(String[] args) {
        System.out.println("=== Data Loading Tutorial ===\n");

        // Create sample CSV file first
        createSampleCSV();

        example1_BasicCSVLoading();
        example2_ConfiguredLoading();
        example3_DataInspection();
    }

    /**
     * Helper: Create a sample CSV file for demonstration
     */
    private static void createSampleCSV() {
        Table sample = Table.create("iris_sample")
                .addColumns(
                        DoubleColumn.create("sepal_length",
                                new double[]{5.1, 4.9, 4.7, 4.6, 5.0, 5.4}),
                        DoubleColumn.create("sepal_width",
                                new double[]{3.5, 3.0, 3.2, 3.1, 3.6, 3.9}),
                        DoubleColumn.create("petal_length",
                                new double[]{1.4, 1.4, 1.3, 1.5, 1.4, 1.7}),
                        DoubleColumn.create("petal_width",
                                new double[]{0.2, 0.2, 0.2, 0.2, 0.2, 0.4}),
                        StringColumn.create("species",
                                new String[]{"setosa", "setosa", "setosa",
                                        "setosa", "setosa", "setosa"})
                );

        try {
            sample.write().csv("iris_sample.csv");
            System.out.println("✓ Created iris_sample.csv\n");
        } catch (Exception e) {
            System.err.println("Could not create sample file: " + e.getMessage());
        }
    }

    /**
     * Example 1: Basic CSV loading
     *
     * Concept: Simplest way to load a CSV file
     * Tablesaw auto-detects column types and handles headers
     */
    private static void example1_BasicCSVLoading() {
        System.out.println("--- Example 1: Basic CSV Loading ---");

        try {
            // Simple one-liner to load CSV
            Table data = Table.read().csv("iris_sample.csv");

            System.out.println("Loaded successfully!");
            System.out.println("Rows: " + data.rowCount());
            System.out.println("Columns: " + data.columnCount());
            System.out.println("\nFirst 3 rows:");
            System.out.println(data.first(3));
            System.out.println();

        } catch (Exception e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
    }

    /**
     * Example 2: Configured loading with options
     *
     * Concept: Handle real-world CSV issues
     * - Custom separators (comma, tab, semicolon)
     * - Missing value indicators
     * - Header presence/absence
     * - Date formats
     */
    private static void example2_ConfiguredLoading() {
        System.out.println("--- Example 2: Configured Loading ---");

        try {
            CsvReadOptions options = CsvReadOptions.builder("iris_sample.csv")
                    .separator(',')                    // Field separator
                    .header(true)                      // First row is header
                    .missingValueIndicator("NA", "?", "null", "") // Missing value markers
                    .sample(false)                     // Read all rows (not just sample)
                    .build();

            Table data = Table.read().usingOptions(options);

            System.out.println("Loaded with custom options");
            System.out.println("Configuration used:");
            System.out.println("  - Separator: comma");
            System.out.println("  - Header: yes");
            System.out.println("  - Missing values: NA, ?, null, (empty)");
            System.out.println("\nData preview:");
            System.out.println(data.first(3));
            System.out.println();

        } catch (Exception e) {
            System.err.println("Error loading file: " + e.getMessage());
        }
    }

    /**
     * Example 3: Data inspection after loading
     *
     * Concept: Always inspect your data before processing
     * Check for: shape, types, missing values, statistics
     */
    private static void example3_DataInspection() {
        System.out.println("--- Example 3: Data Inspection ---");

        try {
            Table data = Table.read().csv("iris_sample.csv");

            // 1. Basic info
            System.out.println("1. Basic Information:");
            System.out.println("   Shape: " + data.rowCount() + " rows × " +
                    data.columnCount() + " columns");

            // 2. Column types
            System.out.println("\n2. Column Types:");
            data.structure().forEach(col ->
                    System.out.println("   " + col));

            // 3. First and last rows
            System.out.println("\n3. First 2 rows:");
            System.out.println(data.first(2));

            System.out.println("Last 2 rows:");
            System.out.println(data.last(2));

            // 4. Summary statistics for numeric columns
            System.out.println("4. Summary Statistics:");
            System.out.println(data.summary());

            // 5. Check for missing values
            System.out.println("\n5. Missing Values:");
            for (String colName : data.columnNames()) {
                long missing = data.column(colName).countMissing();
                if (missing > 0) {
                    System.out.println("   " + colName + ": " + missing + " missing");
                }
            }
            if (data.missingValueCounts().stream().findAny().isEmpty()) {
                System.out.println("   No missing values found");
            }

            // 6. Unique values for categorical column
            System.out.println("\n6. Unique Species:");
            StringColumn species = data.stringColumn("species");
            System.out.println("   " + species.unique().asList());

        } catch (Exception e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}

