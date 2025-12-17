package org.nazarius.VovkORM.mapping;

import org.nazarius.VovkORM.metadata.TableMetadata;

public interface EntityMapper {
    <T> T map(Row row, TableMetadata<T> metadata);
}
