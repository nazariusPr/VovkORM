package org.nazarius.VovkORM.sql.common;

public class Join {
    private String table;
    private String alias;
    private String on;
    private String type;

    private Join() {}

    public static Join inner(String table) {
        Join join = new Join();
        join.table = table;
        join.type = "JOIN";
        return join;
    }

    public static Join left(String table) {
        Join join = new Join();
        join.table = table;
        join.type = "LEFT JOIN";
        return join;
    }

    public static Join right(String table) {
        Join join = new Join();
        join.table = table;
        join.type = "RIGHT JOIN";
        return join;
    }

    public Join as(String alias) {
        this.alias = alias;
        return this;
    }

    public Join on(String condition) {
        this.on = condition;
        return this;
    }

    public String build() {
        StringBuilder sb = new StringBuilder(type).append(" ").append(table);

        if (alias != null && !alias.isBlank()) {
            sb.append(" AS ").append(alias);
        }

        if (on != null && !on.isBlank()) {
            sb.append(" ON ").append(on);
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        return build();
    }
}
