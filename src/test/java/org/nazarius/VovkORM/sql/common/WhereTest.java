package org.nazarius.VovkORM.sql.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class WhereTest {

    @Test
    void testEq_WithStringValue() {
        Where where = Where.column("name").eq("John");
        assertEquals("WHERE name = 'John'", where.build());
    }

    @Test
    void testEq_WithNumberValue() {
        Where where = Where.column("age").eq(25);
        assertEquals("WHERE age = 25", where.build());
    }

    @Test
    void testGtCondition() {
        Where where = Where.column("salary").gt(5000);
        assertEquals("WHERE salary > 5000", where.build());
    }

    @Test
    void testLtCondition() {
        Where where = Where.column("price").lt(100);
        assertEquals("WHERE price < 100", where.build());
    }

    @Test
    void testLikeCondition() {
        Where where = Where.column("name").like("%John%");
        assertEquals("WHERE name LIKE '%John%'", where.build());
    }

    @Test
    void testAndCondition() {
        Where where = Where.column("age").gt(18).and("country").eq("USA");
        assertEquals("WHERE age > 18 AND country = 'USA'", where.build());
    }

    @Test
    void testOrCondition() {
        Where where = Where.column("status").eq("active").or("status").eq("pending");
        assertEquals("WHERE status = 'active' OR status = 'pending'", where.build());
    }

    @Test
    void testMultipleConditions() {
        Where where =
                Where.column("age").gt(18).and("country").eq("USA").or("vip").eq(true);
        assertEquals("WHERE age > 18 AND country = 'USA' OR vip = true", where.build());
    }

    @Test
    void testToString_EqualsBuild() {
        Where where = Where.column("id").eq(10);
        assertEquals(where.build(), where.toString());
    }

    @Test
    void testChainedFluency() {
        Where where = Where.column("id").eq(1);
        assertSame(where, where.gt(2));
    }

    @Test
    void testBuildEmptyClause() {
        Where where = Where.column("id");
        where.eq(1);
        assertEquals("WHERE id = 1", where.build());
    }

    @Test
    void testStringValueEscapingSingleQuote() {
        Where where = Where.column("name").eq("O'Connor");
        assertEquals("WHERE name = 'O''Connor'", where.build());
    }
}
