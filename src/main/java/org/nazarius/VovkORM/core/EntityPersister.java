package org.nazarius.VovkORM.core;

import java.sql.Connection;
import org.nazarius.VovkORM.metadata.TableMetadata;
import org.nazarius.VovkORM.sql.builder.Delete;
import org.nazarius.VovkORM.sql.builder.Update;
import org.nazarius.VovkORM.sql.common.Where;

public interface EntityPersister {
    <T> int deleteById(Connection connection, TableMetadata<T> metadata, Object id);

    <T> int delete(Connection connection, TableMetadata<T> metadata, T entity);

    int delete(Connection connection, Delete delete);

    <T> int delete(Connection connection, TableMetadata<T> metadata, Where where);

    <T> int update(Connection connection, TableMetadata<T> metadata, T entity);

    int update(Connection connection, Update update);

    <T> int update(Connection connection, TableMetadata<T> metadata, T entity, Where where);
}
