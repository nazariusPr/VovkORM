package org.nazarius.VovkORM.sql.builder;

import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.sql.dialect.Dialect;

public class Delete implements SQLBuilder {
    private String table;
    private Where where;

    private Delete() {}

    public String getTable() {
        return table;
    }

    public static Delete delete() {
        return new Delete();
    }

    public Delete from(String table) {
        this.table = table;
        return this;
    }

    public Delete where(Where where) {
        this.where = where;
        return this;
    }

    @Override
    public String build(Dialect dialect) {
        if (table == null || table.isBlank()) {
            throw new IllegalStateException("Table name must be specified for DELETE");
        }

        StringBuilder sb = new StringBuilder("DELETE FROM ");
        sb.append(table);

        if (where != null) {
            sb.append(" ").append(where.build());
        }

        return sb.toString();
    }
}
