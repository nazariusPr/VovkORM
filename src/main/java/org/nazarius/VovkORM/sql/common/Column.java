package org.nazarius.VovkORM.sql.common;

public class Column {
    private StringBuilder expression;
    private String alias;

    private Column() {}

    public static Column name(String column) {
        Column sqlColumn = new Column();
        if (column == null) {
            return sqlColumn;
        }

        sqlColumn.expression = new StringBuilder(column);
        return sqlColumn;
    }

    public Column alias(String alias) {
        this.alias = alias;
        return this;
    }

    public Column max() {
        expression = new StringBuilder("MAX(").append(expression).append(")");
        return this;
    }

    public Column min() {
        expression = new StringBuilder("MIN(").append(expression).append(")");
        return this;
    }

    public Column sum() {
        expression = new StringBuilder("SUM(").append(expression).append(")");
        return this;
    }

    public Column avg() {
        expression = new StringBuilder("AVG(").append(expression).append(")");
        return this;
    }

    public Column count() {
        expression = new StringBuilder("COUNT(").append(expression).append(")");
        return this;
    }

    public Column countDistinct() {
        expression = new StringBuilder("COUNT(DISTINCT ").append(expression).append(")");
        return this;
    }

    public String build() {
        if (alias == null || alias.isBlank()) {
            return expression.toString();
        }
        return expression + " AS " + alias;
    }

    @Override
    public String toString() {
        return build();
    }
}
