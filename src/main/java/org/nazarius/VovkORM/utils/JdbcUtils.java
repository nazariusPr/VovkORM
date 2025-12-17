package org.nazarius.VovkORM.utils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.util.function.Function;
import org.nazarius.VovkORM.sql.builder.SQLBuilder;
import org.nazarius.VovkORM.sql.dialect.Dialect;

public class JdbcUtils {
    private JdbcUtils() {}

    public static <T> T withStatement(
            Connection connection, SQLBuilder sql, Dialect dialect, Function<PreparedStatement, T> action) {
        try (PreparedStatement stmt = connection.prepareStatement(sql.build(dialect))) {
            return action.apply(stmt);
        } catch (Exception e) {
            throw new RuntimeException("Failed to execute statement", e);
        }
    }
}
