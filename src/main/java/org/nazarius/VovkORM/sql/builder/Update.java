package org.nazarius.VovkORM.sql.builder;

import java.util.LinkedHashMap;
import java.util.Map;
import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.sql.dialect.Dialect;

public class Update implements SQLBuilder {
    private String table;
    private final Map<String, Object> columns = new LinkedHashMap<>();
    private Where where;

    private Update() {}

    public String getTable() {
        return table;
    }

    public static Update update(String table) {
        Update update = new Update();
        update.table = table;

        return update;
    }

    public Update set(String column, Object value) {
        columns.put(column, value);
        return this;
    }

    public Update where(Where where) {
        this.where = where;
        return this;
    }

    @Override
    public String build(Dialect dialect) {
        if (table == null || table.isBlank()) {
            throw new IllegalStateException("Table name must be specified for UPDATE");
        }

        if (columns.isEmpty()) {
            throw new IllegalStateException("At least one column must be set for UPDATE");
        }

        StringBuilder sb = new StringBuilder("UPDATE ");
        sb.append(table).append(" SET ");

        // Build "column = value" pairs
        boolean first = true;
        for (Map.Entry<String, Object> entry : columns.entrySet()) {
            if (!first) {
                sb.append(", ");
            }
            first = false;

            sb.append(entry.getKey()).append(" = ");

            Object value = entry.getValue();
            if (value == null) {
                sb.append("NULL");
            } else if (value instanceof Number || value instanceof Boolean) {
                sb.append(value);
            } else {
                // wrap strings and other objects in quotes
                sb.append("'").append(value.toString().replace("'", "''")).append("'");
            }
        }

        if (where != null) {
            sb.append(" ").append(where.build());
        }

        return sb.toString();
    }
}
