package com.debopam.example.ai.basics;

import tech.tablesaw.aggregate.AggregateFunctions;
import tech.tablesaw.api.DoubleColumn;
import tech.tablesaw.api.IntColumn;
import tech.tablesaw.api.StringColumn;
import tech.tablesaw.api.Table;
import tech.tablesaw.selection.Selection;

import static tech.tablesaw.aggregate.AggregateFunctions.mean;
import static tech.tablesaw.aggregate.AggregateFunctions.sum;

/**
 * LEVEL 1: Basic Data Operations
 *
 * Learning Objectives:
 * - Select columns and rows
 * - Filter data with conditions
 * - Sort data
 * - Transform columns
 * - Group and aggregate
 */
public class DataOperations {

    public static void main(String[] args) {
        System.out.println("=== Data Operations Tutorial ===\n");

        // Create sample dataset
        Table employees = createEmployeeData();

        example1_SelectingData(employees);
        example2_FilteringData(employees);
        example3_SortingData(employees);
        example4_TransformingData(employees);
        example5_GroupingAndAggregating(employees);
    }

    /**
     * Create sample employee dataset
     */
    private static Table createEmployeeData() {
        return Table.create("employees")
                .addColumns(
                        IntColumn.create("id",
                                new int[]{101, 102, 103, 104, 105, 106, 107, 108}),
                        StringColumn.create("name",
                                new String[]{"Alice", "Bob", "Charlie", "Diana",
                                        "Eve", "Frank", "Grace", "Henry"}),
                        StringColumn.create("department",
                                new String[]{"Engineering", "Sales", "Engineering", "Sales",
                                        "Engineering", "HR", "HR", "Sales"}),
                        DoubleColumn.create("salary",
                                new double[]{75000, 55000, 82000, 60000,
                                        78000, 50000, 52000, 58000}),
                        IntColumn.create("years_experience",
                                new int[]{5, 3, 7, 4, 6, 2, 3, 4})
                );
    }

    /**
     * Example 1: Selecting columns and rows
     *
     * Concept: Extract subsets of data
     * - Select specific columns
     * - Select row ranges
     * - Combine both
     */
    private static void example1_SelectingData(Table data) {
        System.out.println("--- Example 1: Selecting Data ---");
        System.out.println("Original data:");
        System.out.println(data);

        // Select specific columns
        System.out.println("\n1a. Select name and salary columns:");
        Table nameAndSalary = data.select("name", "salary");
        System.out.println(nameAndSalary);

        // Select first N rows
        System.out.println("1b. First 3 employees:");
        System.out.println(data.first(3));

        // Select row range
        System.out.println("1c. Rows 2-4 (0-indexed):");
        Table subset = data.rows(2, 3, 4); // Individual row indices
        System.out.println(subset);

        // Combine: select columns and rows
        System.out.println("1d. Name and department for first 3:");
        System.out.println(data.select("name", "department").first(3));
        System.out.println();
    }

    /**
     * Example 2: Filtering data with conditions
     *
     * Concept: Boolean indexing - keep rows matching criteria
     * Operators: ==, !=, >, <, >=, <=
     * Logic: and(), or(), not()
     */
    private static void example2_FilteringData(Table data) {
        System.out.println("--- Example 2: Filtering Data ---");

        // Simple filter: salary > 60000
        System.out.println("2a. Employees with salary > $60,000:");
        Table highEarners = data.where(
                data.doubleColumn("salary").isGreaterThan(60000)
        );
        System.out.println(highEarners);

        // Filter by string match
        System.out.println("\n2b. Engineering department only:");
        Table engineers = data.where(
                data.stringColumn("department").isEqualTo("Engineering")
        );
        System.out.println(engineers);

        // Combined conditions with AND
        System.out.println("\n2c. Engineering employees with >5 years experience:");
        Table seniorEngineers = data.where(
                data.stringColumn("department").isEqualTo("Engineering")
                        .and(data.intColumn("years_experience").isGreaterThan(5))
        );
        System.out.println(seniorEngineers);

        // Combined conditions with OR
        System.out.println("\n2d. Salary > $75k OR experience > 6 years:");
        Table qualified = data.where(
                data.doubleColumn("salary").isGreaterThan(75000)
                        .or(data.intColumn("years_experience").isGreaterThan(6))
        );
        System.out.println(qualified);

        // NOT condition
        System.out.println("\n2e. Everyone except Sales:");
        Table nonSales = data.where(
                data.stringColumn("department").isNotEqualTo("Sales")
        );
        System.out.println(nonSales);
        System.out.println();
    }

    /**
     * Example 3: Sorting data
     *
     * Concept: Reorder rows by column values
     * - Ascending vs descending
     * - Multiple column sorting
     */
    private static void example3_SortingData(Table data) {
        System.out.println("--- Example 3: Sorting Data ---");

        // Sort by single column (ascending)
        System.out.println("3a. Sort by salary (ascending):");
        Table sortedAsc = data.sortAscendingOn("salary");
        System.out.println(sortedAsc);

        // Sort by single column (descending)
        System.out.println("\n3b. Sort by salary (descending):");
        Table sortedDesc = data.sortDescendingOn("salary");
        System.out.println(sortedDesc);

        // Sort by multiple columns
        System.out.println("\n3c. Sort by department, then salary (desc):");
        Table multiSort = data.sortOn("department", "salary");
        System.out.println(multiSort);
        System.out.println();
    }

    /**
     * Example 4: Transforming columns
     *
     * Concept: Create new columns from existing ones
     * - Mathematical operations
     * - String manipulations
     * - Conditional transformations
     */
    private static void example4_TransformingData(Table data) {
        System.out.println("--- Example 4: Transforming Data ---");

        Table transformed = data.copy();

        // Create salary in thousands
        DoubleColumn salaryK = transformed.doubleColumn("salary")
                .divide(1000)
                .setName("salary_k");
        transformed.addColumns(salaryK);

        transformed.intColumn("years_experience")
                .setName("years_experience");

        java.util.function.Function<Integer, String> experienceToLevelFunc = years -> {
            if (years < 3) return "Junior";
            else if (years < 6) return "Mid";
            else return "Senior";
        };

        // Create experience level based on years
        StringColumn level = transformed.intColumn("years_experience")
                .map(experienceToLevelFunc, StringColumn::create)
                .setName("level");

        transformed.addColumns(level);

        // Calculate annual bonus (10% of salary)
        DoubleColumn bonus = transformed.doubleColumn("salary")
                .multiply(0.10)
                .setName("bonus");
        transformed.addColumns(bonus);

        System.out.println("Transformed data with new columns:");
        System.out.println(transformed);
        System.out.println();
    }

    /**
     * Example 5: Grouping and aggregating
     *
     * Concept: Split-Apply-Combine pattern
     * 1. Split data into groups
     * 2. Apply aggregation function (sum, mean, count, etc.)
     * 3. Combine results
     */
    private static void example5_GroupingAndAggregating(Table data) {
        System.out.println("--- Example 5: Grouping and Aggregating ---");

        // Group by department and calculate statistics
        System.out.println("5a. Statistics by department:");
        Table byDept = data.summarize("salary", mean, sum, AggregateFunctions.count)
                .by("department");

        System.out.println(byDept);

        // Count employees per department
        System.out.println("\n5b. Employee count by department:");
        Table counts = data.countBy(data.categoricalColumn("department"));
        System.out.println(counts);

        // Multiple grouping columns
        System.out.println("\n5c. Average salary by department and experience:");
        // Create experience bands first
        Table withBands = data.copy();
        java.util.function.Function<Integer, String> experinceToYears = y -> y < 4 ? "0-3" : y < 7 ? "4-6" : "7+";

        StringColumn expBand = withBands.intColumn("years_experience")
                .map(experinceToYears, StringColumn::create)
                .setName("exp_band");
        withBands.addColumns(expBand);

        Table multiGroup = withBands.summarize("salary",
                mean
        ).by("department", "exp_band");

        System.out.println(multiGroup);
        System.out.println();
    }
}

