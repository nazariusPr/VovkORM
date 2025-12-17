package org.nazarius.VovkORM.sql.builder;

import org.nazarius.VovkORM.sql.dialect.Dialect;

public interface SQLBuilder {
    String build(Dialect dialect);
}
