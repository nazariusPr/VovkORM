package org.nazarius.VovkORM.sql.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.sql.dialect.Dialect;
import org.nazarius.VovkORM.sql.dialect.H2Dialect;

class UpdateTest {
    private final Dialect dialect = new H2Dialect();

    @Test
    void testBuild_SingleColumn_NoWhere() {
        Update update = Update.update("users").set("name", "John");

        String expected = "UPDATE users SET name = 'John'";
        assertEquals(expected, update.build(dialect));
    }

    @Test
    void testBuild_MultipleColumns_OrderPreserved() {
        Update update =
                Update.update("users").set("name", "Alice").set("age", 30).set("active", true);

        String expected = "UPDATE users SET name = 'Alice', age = 30, active = true";
        assertEquals(expected, update.build(dialect), "LinkedHashMap should preserve insertion order of columns");
    }

    @Test
    void testBuild_WithWhereClause() {
        Where where = Where.column("id").eq(5);

        Update update = Update.update("users").set("name", "Bob").where(where);

        String expected = "UPDATE users SET name = 'Bob' WHERE id = 5";
        assertEquals(expected, update.build(dialect));
    }

    @Test
    void testBuild_WithNullValue() {
        Update update = Update.update("products").set("description", null);

        String expected = "UPDATE products SET description = NULL";
        assertEquals(expected, update.build(dialect));
    }

    @Test
    void testBuild_WithStringContainingQuotes() {
        Update update = Update.update("users").set("nickname", "O'Reilly");

        String expected = "UPDATE users SET nickname = 'O''Reilly'";
        assertEquals(expected, update.build(dialect));
    }

    @Test
    void testBuild_WithNumberAndBoolean() {
        Update update = Update.update("config").set("version", 2).set("enabled", false);

        String expected = "UPDATE config SET version = 2, enabled = false";
        assertEquals(expected, update.build(dialect));
    }

    @Test
    void testBuild_ThrowsWhenNoColumns() {
        Update update = Update.update("users");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> update.build(dialect));
        assertEquals("At least one column must be set for UPDATE", ex.getMessage());
    }

    @Test
    void testChainedFluentInterface() {
        Update update = Update.update("users")
                .set("name", "Charlie")
                .set("age", 25)
                .where(Where.column("id").eq(1));

        String expected = "UPDATE users SET name = 'Charlie', age = 25 WHERE id = 1";
        assertEquals(expected, update.build(dialect));
    }

    @Test
    void testBuild_WithSpecialCharactersInString() {
        Update update = Update.update("users").set("note", "Hello, world!");

        String expected = "UPDATE users SET note = 'Hello, world!'";
        assertEquals(expected, update.build(dialect));
    }
}
