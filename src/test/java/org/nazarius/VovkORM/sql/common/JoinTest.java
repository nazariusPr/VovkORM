package org.nazarius.VovkORM.sql.common;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;

import org.junit.jupiter.api.Test;

class JoinTest {

    @Test
    void testInnerJoin_Basic() {
        Join join = Join.inner("users");
        assertEquals("JOIN users", join.build());
    }

    @Test
    void testLeftJoin_Basic() {
        Join join = Join.left("orders");
        assertEquals("LEFT JOIN orders", join.build());
    }

    @Test
    void testRightJoin_Basic() {
        Join join = Join.right("payments");
        assertEquals("RIGHT JOIN payments", join.build());
    }

    @Test
    void testInnerJoin_WithAlias() {
        Join join = Join.inner("users").as("u");
        assertEquals("JOIN users AS u", join.build());
    }

    @Test
    void testLeftJoin_WithAliasAndOnCondition() {
        Join join = Join.left("orders").as("o").on("u.id = o.user_id");
        assertEquals("LEFT JOIN orders AS o ON u.id = o.user_id", join.build());
    }

    @Test
    void testRightJoin_WithOnConditionOnly() {
        Join join = Join.right("payments").on("o.id = p.order_id");
        assertEquals("RIGHT JOIN payments ON o.id = p.order_id", join.build());
    }

    @Test
    void testBlankAlias_Ignored() {
        Join join = Join.inner("products").as("   ");
        assertEquals("JOIN products", join.build());
    }

    @Test
    void testBlankOnCondition_Ignored() {
        Join join = Join.left("categories").on("   ");
        assertEquals("LEFT JOIN categories", join.build());
    }

    @Test
    void testToString_EqualsBuild() {
        Join join = Join.inner("users").as("u").on("u.id = o.user_id");
        assertEquals(join.build(), join.toString());
    }

    @Test
    void testFluentChaining() {
        Join join = Join.left("orders");
        assertSame(join, join.as("o"));
        assertSame(join, join.on("u.id = o.user_id"));
    }

    @Test
    void testMultipleCalls_OverrideAliasAndOn() {
        Join join = Join.inner("users")
                .as("u1")
                .on("x = y")
                .as("u2") // override alias
                .on("u2.id = o.user_id"); // override on condition

        assertEquals("JOIN users AS u2 ON u2.id = o.user_id", join.build());
    }
}
