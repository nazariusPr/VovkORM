package org.nazarius.VovkORM.sql.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import org.nazarius.VovkORM.sql.dialect.Dialect;

public class Insert implements SQLBuilder {
    private String table;
    private final Map<String, Object> columns = new LinkedHashMap<>();

    private Insert() {}

    public static Insert into(String table) {
        Insert insert = new Insert();
        insert.table = table;
        return insert;
    }

    public Insert value(String column, Object value) {
        columns.put(column, value);
        return this;
    }

    @Override
    public String build(Dialect dialect) {
        if (table == null || table.isBlank()) {
            throw new IllegalStateException("Table name must be specified for INSERT");
        }

        if (columns.isEmpty()) {
            throw new IllegalStateException("At least one column must be provided for INSERT");
        }

        StringBuilder sb = new StringBuilder("INSERT INTO ");
        sb.append(table).append(" (");

        boolean first = true;
        for (String column : columns.keySet()) {
            if (!first) sb.append(", ");
            first = false;
            sb.append(column);
        }

        sb.append(") VALUES (");

        first = true;
        for (Object value : columns.values()) {
            if (!first) sb.append(", ");
            first = false;

            if (value == null) {
                sb.append("NULL");
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else {
                sb.append("'").append(value.toString().replace("'", "''")).append("'");
            }
        }

        sb.append(")");
        return sb.toString();
    }
}
