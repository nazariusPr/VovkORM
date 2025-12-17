package org.nazarius.VovkORM.sql.builder;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.stream.Collectors;
import org.nazarius.VovkORM.sql.common.Column;
import org.nazarius.VovkORM.sql.common.Join;
import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.sql.dialect.Dialect;
import org.nazarius.VovkORM.utils.SortField;

public class Select implements SQLBuilder {
    private final Set<Column> columns = new LinkedHashSet<>();
    private String table;
    private String alias;
    private final Set<Join> joins = new LinkedHashSet<>();
    private Where where;
    private long limit;
    private long offset;
    private final Set<String> orderBy = new LinkedHashSet<>();

    private Select() {}

    public static Select select() {
        return new Select();
    }

    public static Select select(String... cols) {
        if (cols == null || cols.length == 0) {
            return select();
        }

        Column[] columnObjects = Arrays.stream(cols)
                .filter(c -> c != null && !c.isBlank())
                .map(Column::name)
                .toArray(Column[]::new);

        return select(columnObjects);
    }

    public static Select select(Column... cols) {
        Select sql = new Select();
        sql.columns.addAll(Arrays.asList(cols));
        return sql;
    }

    public Select from(String table) {
        this.table = table;
        return this;
    }

    public Select as(String alias) {
        this.alias = alias;
        return this;
    }

    public Select join(Join join) {
        if (join != null) {
            joins.add(join);
        }
        return this;
    }

    public Select where(Where where) {
        this.where = where;
        return this;
    }

    public Select limit(long limit) {
        this.limit = limit;
        return this;
    }

    public Select offset(long offset) {
        this.offset = offset;
        return this;
    }

    public Select orderBy(String... cols) {
        orderBy.addAll(Arrays.asList(cols));
        return this;
    }

    public Select orderBy(SortField... sortFields) {
        String[] fields = Arrays.stream(sortFields).map(SortField::toString).toArray(String[]::new);
        return orderBy(fields);
    }

    @Override
    public String build(Dialect dialect) {
        if (table == null || table.isBlank()) {
            throw new IllegalStateException("Table name must be specified for SELECT");
        }

        StringBuilder query = new StringBuilder("SELECT ");

        if (columns.isEmpty()) {
            query.append("*");
        } else {
            query.append(columns.stream().map(Column::build).collect(Collectors.joining(", ")));
        }

        if (table != null) {
            query.append(" FROM ").append(table);
        }

        if (alias != null && !alias.isBlank()) {
            query.append(" AS ").append(alias);
        }

        for (Join j : joins) {
            query.append(" ").append(j.build());
        }

        if (where != null) {
            query.append(" ").append(where.build());
        }

        if (!orderBy.isEmpty()) {
            query.append(" ORDER BY ").append(String.join(", ", orderBy));
        }

        if (limit > 0 || offset > 0) {
            String limitOffset = dialect.getLimitOffsetClause(limit, offset);
            query.append(" ").append(limitOffset);
        }

        return query.toString().trim();
    }

    public String getTable() {
        return table;
    }
}
