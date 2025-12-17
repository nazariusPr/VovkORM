package org.nazarius.VovkORM.core.impl;

import static org.nazarius.VovkORM.sql.common.Where.column;
import static org.nazarius.VovkORM.utils.JdbcUtils.withStatement;

import java.sql.Connection;
import java.sql.SQLException;
import org.nazarius.VovkORM.core.EntityPersister;
import org.nazarius.VovkORM.metadata.ColumnMetadata;
import org.nazarius.VovkORM.metadata.TableMetadata;
import org.nazarius.VovkORM.sql.builder.Delete;
import org.nazarius.VovkORM.sql.builder.SQLBuilder;
import org.nazarius.VovkORM.sql.builder.Update;
import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.sql.dialect.Dialect;

public class SimpleEntityPersister implements EntityPersister {
    private final Dialect dialect;

    public SimpleEntityPersister(Dialect dialect) {
        this.dialect = dialect;
    }

    @Override
    public <T> int deleteById(Connection connection, TableMetadata<T> metadata, Object id) {
        ColumnMetadata primaryKey = metadata.getPrimaryKey();
        if (primaryKey == null) {
            throw new IllegalStateException(String.format(
                    "Entity %s has no primary key defined", metadata.getClazz().getSimpleName()));
        }

        if (id == null) {
            throw new IllegalStateException(String.format(
                    "Primary key value is null for entity %s",
                    metadata.getClazz().getSimpleName()));
        }

        final String TABLE = metadata.getTableName();
        final String PRIMARY_KEY = primaryKey.getColumnName();

        Delete sql = Delete.delete().from(TABLE).where(column(PRIMARY_KEY).eq(id));
        return executePersistAction(connection, metadata.getTableName(), sql);
    }

    @Override
    public <T> int delete(Connection connection, TableMetadata<T> metadata, T entity) {
        ColumnMetadata primaryKey = metadata.getPrimaryKey();
        if (primaryKey == null) {
            throw new IllegalStateException(String.format(
                    "Entity %s has no primary key defined", metadata.getClazz().getSimpleName()));
        }

        Object id = primaryKey.getValue(entity);
        return deleteById(connection, metadata, id);
    }

    @Override
    public int delete(Connection connection, Delete delete) {
        return executePersistAction(connection, delete.getTable(), delete);
    }

    @Override
    public <T> int delete(Connection connection, TableMetadata<T> metadata, Where where) {
        Delete sql = Delete.delete().from(metadata.getTableName()).where(where);
        return delete(connection, sql);
    }

    @Override
    public <T> int update(Connection connection, TableMetadata<T> metadata, T entity) {
        ColumnMetadata primaryKey = metadata.getPrimaryKey();
        if (primaryKey == null) {
            throw new IllegalStateException(
                    "Entity " + metadata.getClazz().getSimpleName() + " has no primary key defined");
        }

        Object id = primaryKey.getValue(entity);
        if (id == null) {
            throw new IllegalStateException("Primary key value is null for entity "
                    + metadata.getClazz().getSimpleName());
        }

        Where where = Where.column(primaryKey.getColumnName()).eq(id);
        Update update = buildUpdateStatement(entity, metadata, where);
        return update(connection, update);
    }

    @Override
    public int update(Connection connection, Update update) {
        return executePersistAction(connection, update.getTable(), update);
    }

    @Override
    public <T> int update(Connection connection, TableMetadata<T> metadata, T entity, Where where) {
        Update update = buildUpdateStatement(entity, metadata, where);
        return update(connection, update);
    }

    private <T> Update buildUpdateStatement(T entity, TableMetadata<T> metadata, Where where) {
        final String TABLE = metadata.getTableName();
        Update update = Update.update(TABLE);

        for (ColumnMetadata columnMetadata : metadata.getColumns().values()) {
            if (columnMetadata.isPrimaryKey()) {
                continue;
            }

            String column = columnMetadata.getColumnName();
            Object value = columnMetadata.getValue(entity);
            update.set(column, value);
        }

        if (where != null) {
            update.where(where);
        }
        return update;
    }

    private <T> Update buildUpdateStatement(T entity, TableMetadata<T> metadata) {
        return buildUpdateStatement(entity, metadata, null);
    }

    private int executePersistAction(Connection connection, String tableName, SQLBuilder sql) {
        return withStatement(connection, sql, dialect, stmt -> {
            try {
                return stmt.executeUpdate();
            } catch (SQLException e) {
                throw new RuntimeException("Failed to execute persist action on table: " + tableName, e);
            }
        });
    }
}
