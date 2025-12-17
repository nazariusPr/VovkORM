package org.nazarius.VovkORM.sql.dialect;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.SQLException;

public class DialectFactory {
    private DialectFactory() {}

    public static Dialect from(Connection connection) {
        try {
            DatabaseMetaData meta = connection.getMetaData();
            String url = meta.getURL().toLowerCase();

            if (url.contains("postgresql")) {
                return new PostgresDialect();
            }
            if (url.contains("h2")) {
                return new H2Dialect();
            }
            if (url.contains("oracle")) {
                return new OracleDialect();
            }

            throw new UnsupportedOperationException("Unknown database dialect: " + url);

        } catch (SQLException e) {
            throw new RuntimeException("Could not determine dialect from Connection", e);
        }
    }
}
