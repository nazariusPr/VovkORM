package org.nazarius.VovkORM.sql.dialect;

import java.util.HashMap;
import java.util.Map;

public class H2Dialect implements Dialect {
    private static final Map<Class<?>, String> TYPE_MAPPING = new HashMap<>();

    static {
        TYPE_MAPPING.put(Integer.class, "INT");
        TYPE_MAPPING.put(int.class, "INT");
        TYPE_MAPPING.put(Long.class, "BIGINT");
        TYPE_MAPPING.put(long.class, "BIGINT");
        TYPE_MAPPING.put(Boolean.class, "BOOLEAN");
        TYPE_MAPPING.put(boolean.class, "BOOLEAN");
        TYPE_MAPPING.put(String.class, "VARCHAR");
    }

    @Override
    public String getIdentityColumnString() {
        return "IDENTITY";
    }

    @Override
    public String getLimitOffsetClause(long limit, long offset) {
        StringBuilder sql = new StringBuilder();

        if (limit > 0) {
            sql.append("LIMIT ").append(limit).append(" ");
        }

        if (offset > 0) {
            sql.append("OFFSET ").append(offset);
        }

        return sql.toString().trim();
    }

    @Override
    public String getColumnType(Class<?> javaType) {
        return TYPE_MAPPING.getOrDefault(javaType, "VARCHAR");
    }
}
