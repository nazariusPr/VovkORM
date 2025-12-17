package org.nazarius.VovkORM.sql.dialect;

public interface Dialect {
    String getIdentityColumnString();

    String getLimitOffsetClause(long limit, long offset);

    String getColumnType(Class<?> javaType);
}
