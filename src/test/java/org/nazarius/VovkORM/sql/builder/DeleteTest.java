package org.nazarius.VovkORM.sql.builder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.nazarius.VovkORM.sql.builder.Delete.delete;

import org.junit.jupiter.api.Test;
import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.sql.dialect.Dialect;
import org.nazarius.VovkORM.sql.dialect.H2Dialect;

class DeleteTest {
    private final Dialect dialect = new H2Dialect();

    @Test
    void testBuild_SimpleDelete() {
        Delete delete = delete().from("users");

        String expected = "DELETE FROM users";
        assertEquals(expected, delete.build(dialect));
    }

    @Test
    void testBuild_WithWhereClause() {
        Where where = Where.column("id").eq(10);
        Delete delete = delete().from("users").where(where);

        String expected = "DELETE FROM users WHERE id = 10";
        assertEquals(expected, delete.build(dialect));
    }

    @Test
    void testBuild_WithComplexWhereClause() {
        Where where = Where.column("age").gt(18).and("active").eq(true);
        Delete delete = delete().from("customers").where(where);

        String expected = "DELETE FROM customers WHERE age > 18 AND active = true";
        assertEquals(expected, delete.build(dialect));
    }

    @Test
    void testBuild_ThrowsWhenNoTable() {
        Delete delete = delete();
        delete.where(Where.column("id").eq(1));

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> delete.build(dialect));
        assertEquals("Table name must be specified for DELETE", ex.getMessage());
    }

    @Test
    void testBuild_WithEmptyTable_ThrowsException() {
        Delete delete = delete().from("   ");

        IllegalStateException ex = assertThrows(IllegalStateException.class, () -> delete.build(dialect));
        assertEquals("Table name must be specified for DELETE", ex.getMessage());
    }

    @Test
    void testFluentInterface_Chaining() {
        Delete delete = delete().from("products").where(Where.column("quantity").lt(1));

        String expected = "DELETE FROM products WHERE quantity < 1";
        assertEquals(expected, delete.build(dialect));
    }

    @Test
    void testBuild_MultipleCalls_Idempotent() {
        Delete delete = delete().from("sessions").where(Where.column("expired").eq(true));

        String first = delete.build(dialect);
        String second = delete.build(dialect);

        assertEquals(first, second, "Calling build() multiple times should yield same result");
    }
}
