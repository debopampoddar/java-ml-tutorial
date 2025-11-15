package com.debopam.example.ai.basics;

import tech.tablesaw.api.BooleanColumn;
import tech.tablesaw.api.DateColumn;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.columns.Column;

import java.time.LocalDate;

/**
 * LEVEL 1: Creating DataFrames with Tablesaw
 * <p>
 * Learning Objectives:
 * - Understand DataFrame structure
 * - Create DataFrames from different sources
 * - Work with different column types
 */
public class DataFrameCreation {

    public static void main(String[] args) {
        System.out.println("=== DataFrame Creation Tutorial ===\n");

        example1_SimpleCreation();
        example2_TypedColumns();
        example3_FromArrays();
        example4_RealWorldExample();
    }

    /**
     * Example 1: Creating an empty DataFrame and adding columns
     *
     * Concept: Start with an empty table and progressively add columns
     * Use case: Building data structures programmatically
     */
    private static void example1_SimpleCreation() {
        System.out.println("--- Example 1: Simple Creation ---");

        // Create empty table with name
        Table table = Table.create("students");

        // Add columns one by one
        StringColumn names = StringColumn.create("name");
        names.append("Alice");
        names.append("Bob");
        names.append("Charlie");

        IntColumn ages = IntColumn.create("age");
        ages.append(20);
        ages.append(22);
        ages.append(21);

        table.addColumns(names, ages);

        System.out.println(table);
        System.out.println("Shape: " + table.rowCount() + " rows × " +
                table.columnCount() + " columns\n");
    }

    /**
     * Example 2: Creating DataFrame with typed columns
     *
     * Concept: Different column types for different data
     * Column Types:
     * - StringColumn: Text data
     * - IntColumn: Integer numbers
     * - DoubleColumn: Decimal numbers
     * - BooleanColumn: True/False values
     * - DateColumn: Dates
     */
    private static void example2_TypedColumns() {
        System.out.println("--- Example 2: Typed Columns ---");

        Table employees = Table.create("employees")
                .addColumns(
                        StringColumn.create("name",
                                new String[]{"Alice", "Bob", "Charlie", "Diana"}),
                        IntColumn.create("age",
                                new int[]{25, 30, 35, 28}),
                        DoubleColumn.create("salary",
                                new double[]{50000.0, 65000.0, 75000.0, 60000.0}),
                        BooleanColumn.create("remote",
                                new boolean[]{true, false, true, false}),
                        DateColumn.create("hire_date",
                                new LocalDate[]{
                                        LocalDate.of(2020, 1, 15),
                                        LocalDate.of(2019, 6, 1),
                                        LocalDate.of(2021, 3, 22),
                                        LocalDate.of(2020, 11, 30)
                                })
                );

        System.out.println(employees);
        System.out.println("\nColumn Types:");
        for (Column<?> col : employees.columns()) {
            System.out.println("  " + col.name() + ": " + col.type());
        }
        System.out.println();
    }

    /**
     * Example 3: Creating from 2D arrays
     *
     * Concept: Convert existing numeric data to DataFrame
     * Use case: When you have computation results as arrays
     */
    private static void example3_FromArrays() {
        System.out.println("--- Example 3: From Arrays ---");

        // Simulate data from computation
        double[][] features = {
                {1.5, 2.3, 3.1},
                {4.2, 5.1, 6.0},
                {7.3, 8.2, 9.1}
        };

        // Create column names
        String[] columnNames = {"feature_1", "feature_2", "feature_3"};

        // Build table
        Table data = Table.create("features");
        for (int colIdx = 0; colIdx < features[0].length; colIdx++) {
            double[] column = new double[features.length];
            for (int rowIdx = 0; rowIdx < features.length; rowIdx++) {
                column[rowIdx] = features[rowIdx][colIdx];
            }
            data.addColumns(DoubleColumn.create(columnNames[colIdx], column));
        }

        System.out.println(data);
        System.out.println();
    }

    /**
     * Example 4: Real-world e-commerce example
     *
     * Concept: Realistic data structure combining multiple types
     * Demonstrates: Mixed data types, realistic values, business context
     */
    private static void example4_RealWorldExample() {
        System.out.println("--- Example 4: Real-World E-commerce Data ---");

        Table orders = Table.create("orders")
                .addColumns(
                        IntColumn.create("order_id",
                                new int[]{1001, 1002, 1003, 1004, 1005}),
                        StringColumn.create("customer",
                                new String[]{"Alice", "Bob", "Charlie", "Alice", "Diana"}),
                        StringColumn.create("product",
                                new String[]{"Laptop", "Mouse", "Keyboard", "Monitor", "Laptop"}),
                        DoubleColumn.create("price",
                                new double[]{999.99, 25.50, 89.99, 299.99, 1099.99}),
                        IntColumn.create("quantity",
                                new int[]{1, 2, 1, 1, 1}),
                        DateColumn.create("order_date",
                                new LocalDate[]{
                                        LocalDate.of(2025, 11, 1),
                                        LocalDate.of(2025, 11, 1),
                                        LocalDate.of(2025, 11, 2),
                                        LocalDate.of(2025, 11, 3),
                                        LocalDate.of(2025, 11, 3)
                                })
                );

        // Add calculated column (total = price * quantity)
        DoubleColumn total = orders.doubleColumn("price")
                .multiply(orders.intColumn("quantity"))
                .setName("total");
        orders.addColumns(total);

        System.out.println(orders);

        // Quick statistics
        System.out.println("\nQuick Statistics:");
        System.out.println("  Total Revenue: $" +
                orders.doubleColumn("total").sum());
        System.out.println("  Average Order: $" +
                String.format("%.2f", orders.doubleColumn("total").mean()));
        System.out.println("  Number of Orders: " + orders.rowCount());
        System.out.println();
    }
}
