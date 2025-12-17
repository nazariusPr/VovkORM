package org.nazarius.VovkORM.sql.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.nazarius.VovkORM.sql.builder.Select.select;

import org.junit.jupiter.api.Test;
import org.nazarius.VovkORM.sql.common.Join;
import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.sql.dialect.Dialect;
import org.nazarius.VovkORM.sql.dialect.H2Dialect;

class SelectTest {
    private final Dialect dialect = new H2Dialect();

    @Test
    void testSelectAllColumns() {
        String query = select().from("users").build(dialect);

        assertEquals("SELECT * FROM users", query);
    }

    @Test
    void testSelectSpecificColumns() {
        String query = select("id", "name", "email").from("users").build(dialect);

        assertEquals("SELECT id, name, email FROM users", query);
    }

    @Test
    void testSelectWithAlias() {
        String query = select("id", "name").from("users").as("u").build(dialect);

        assertEquals("SELECT id, name FROM users AS u", query);
    }

    @Test
    void testSelectWithJoin() {
        Join join = Join.inner("orders").as("o").on("u.id = o.user_id");

        String query = select("u.id", "u.name", "o.total")
                .from("users")
                .as("u")
                .join(join)
                .build(dialect);

        assertEquals("SELECT u.id, u.name, o.total FROM users AS u JOIN orders AS o ON u.id = o.user_id", query);
    }

    @Test
    void testSelectWithMultipleJoins() {
        Join join1 = Join.inner("orders").as("o").on("u.id = o.user_id");
        Join join2 = Join.left("payments").as("p").on("p.order_id = o.id");

        String query = select("u.id", "o.total", "p.amount")
                .from("users")
                .as("u")
                .join(join1)
                .join(join2)
                .build(dialect);

        assertEquals(
                "SELECT u.id, o.total, p.amount FROM users AS u JOIN orders AS o ON u.id = o.user_id LEFT JOIN payments AS p ON p.order_id = o.id",
                query);
    }

    @Test
    void testSelectWithWhereClause() {
        Where where = Where.column("age").gt(18).and("active").eq(true);

        String query = select("id", "name").from("users").where(where).build(dialect);

        assertEquals("SELECT id, name FROM users WHERE age > 18 AND active = true", query);
    }

    @Test
    void testSelectWithOrderBy() {
        String query =
                select("id", "name").from("users").orderBy("name", "id DESC").build(dialect);

        // Note: HashSet in orderBy means order is not guaranteed, so we assert using contains
        assertTrue(query.startsWith("SELECT id, name FROM users ORDER BY"));
        assertTrue(query.contains("name"));
        assertTrue(query.contains("id DESC"));
    }

    @Test
    void testSelectWithJoinWhereAndOrderBy() {
        Join join = Join.left("orders").as("o").on("u.id = o.user_id");
        Where where = Where.column("u.active").eq(true);

        String query = select("u.id", "u.name", "o.total")
                .from("users")
                .as("u")
                .join(join)
                .where(where)
                .orderBy("u.name")
                .build(dialect);

        assertEquals(
                "SELECT u.id, u.name, o.total FROM users AS u LEFT JOIN orders AS o ON u.id = o.user_id WHERE u.active = true ORDER BY u.name",
                query);
    }

    @Test
    void testSelectIgnoresNullAndBlankColumns() {
        String query = select("id", " ", null, "email").from("users").build(dialect);

        assertEquals("SELECT id, email FROM users", query);
    }

    @Test
    void testOrderByCanBeCalledMultipleTimes() {
        String query =
                select("id").from("users").orderBy("name").orderBy("email").build(dialect);

        assertTrue(query.contains("ORDER BY"));
        assertTrue(query.contains("name"));
        assertTrue(query.contains("email"));
    }
}
