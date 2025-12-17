package org.nazarius.VovkORM.sql.common;

public class Where {
    private final StringBuilder clause = new StringBuilder();
    private String currentColumn;

    private Where() {}

    public static Where column(String column) {
        Where where = new Where();
        where.currentColumn = column;
        return where;
    }

    public Where eq(Object value) {
        appendCondition("=", value);
        return this;
    }

    public Where gt(Object value) {
        appendCondition(">", value);
        return this;
    }

    public Where lt(Object value) {
        appendCondition("<", value);
        return this;
    }

    public Where like(String value) {
        appendCondition("LIKE", value);
        return this;
    }

    public Where and(String column) {
        clause.append(" AND");
        currentColumn = column;
        return this;
    }

    public Where or(String column) {
        clause.append(" OR");
        currentColumn = column;
        return this;
    }

    public String build() {
        return "WHERE " + clause;
    }

    @Override
    public String toString() {
        return build();
    }

    private void appendCondition(String operator, Object value) {
        if (!clause.isEmpty()) {
            clause.append(" ");
        }

        clause.append(currentColumn).append(" ").append(operator).append(" ");

        if (value == null) {
            clause.append("NULL");
        } else if (value instanceof Number || value instanceof Boolean) {
            clause.append(value);
        } else {
            // wrap strings and other objects in quotes
            clause.append("'").append(value.toString().replace("'", "''")).append("'");
        }
    }
}
