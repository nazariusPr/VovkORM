package org.nazarius.VovkORM.sql.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.nazarius.VovkORM.sql.common.Column.name;

import org.junit.jupiter.api.Test;

class ColumnTest {

    @Test
    void testName_WithValidColumn() {
        Column column = name("username");
        assertEquals("username", column.build());
    }

    @Test
    void testName_WithNullColumn() {
        Column column = name(null);
        assertThrows(NullPointerException.class, column::build);
    }

    @Test
    void testAlias_WithValidAlias() {
        Column column = name("username").alias("u");
        assertEquals("username AS u", column.build());
    }

    @Test
    void testAlias_BlankAlias() {
        Column column = name("username").alias("   ");
        // blank alias should be ignored
        assertEquals("username", column.build());
    }

    @Test
    void testAlias_NullAlias() {
        Column column = name("username").alias(null);
        assertEquals("username", column.build());
    }

    @Test
    void testToString_EqualsBuild() {
        Column column = name("id").alias("user_id");
        assertEquals(column.build(), column.toString());
    }

    @Test
    void testBuild_WithoutAlias() {
        Column column = name("email");
        assertEquals("email", column.build());
    }

    @Test
    void testBuild_WithAlias() {
        Column column = name("email").alias("e");
        assertEquals("email AS e", column.build());
    }

    @Test
    void testChainedCalls() {
        Column column = name("address").alias("a");
        assertEquals("address AS a", column.build());
        assertSame(column, column.alias("a"));
    }

    @Test
    void testMaxAggregation() {
        assertEquals("MAX(age)", name("age").max().build());
        assertEquals("MAX(age) AS max_age", name("age").max().alias("max_age").build());
    }

    @Test
    void testMinAggregation() {
        assertEquals("MIN(salary)", name("salary").min().build());
        assertEquals(
                "MIN(salary) AS min_salary",
                name("salary").min().alias("min_salary").build());
    }

    @Test
    void testSumAggregation() {
        assertEquals("SUM(amount)", name("amount").sum().build());
        assertEquals(
                "SUM(amount) AS total_amount",
                name("amount").sum().alias("total_amount").build());
    }

    @Test
    void testAvgAggregation() {
        assertEquals("AVG(score)", name("score").avg().build());
        assertEquals(
                "AVG(score) AS avg_score",
                name("score").avg().alias("avg_score").build());
    }

    @Test
    void testCountAggregation() {
        assertEquals("COUNT(id)", name("id").count().build());
        assertEquals(
                "COUNT(id) AS count_id", name("id").count().alias("count_id").build());
    }

    @Test
    void testCountDistinctAggregation() {
        assertEquals("COUNT(DISTINCT user_id)", name("user_id").countDistinct().build());
        assertEquals(
                "COUNT(DISTINCT user_id) AS distinct_users",
                name("user_id").countDistinct().alias("distinct_users").build());
    }

    @Test
    void testMultipleAggregationCalls() {
        Column column = name("price").sum().max();
        assertEquals("MAX(SUM(price))", column.build());
    }
}
