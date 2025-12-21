package org.nazarius.VovkORM.core.impl;

import static org.nazarius.VovkORM.sql.builder.Select.select;
import static org.nazarius.VovkORM.sql.common.Where.column;
import static org.nazarius.VovkORM.utils.JdbcUtils.withStatement;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.nazarius.VovkORM.core.EntityReader;
import org.nazarius.VovkORM.mapping.EntityMapper;
import org.nazarius.VovkORM.mapping.Row;
import org.nazarius.VovkORM.mapping.impl.RowData;
import org.nazarius.VovkORM.metadata.ColumnMetadata;
import org.nazarius.VovkORM.metadata.TableMetadata;
import org.nazarius.VovkORM.sql.builder.Select;
import org.nazarius.VovkORM.sql.common.Column;
import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.sql.dialect.Dialect;
import org.nazarius.VovkORM.utils.Page;
import org.nazarius.VovkORM.utils.Pageable;
import org.nazarius.VovkORM.utils.SortField;

public class SimpleEntityReader implements EntityReader {
    private EntityMapper mapper;
    private final Dialect dialect;

    public SimpleEntityReader(EntityMapper mapper, Dialect dialect) {
        this.mapper = mapper;
        this.dialect = dialect;
    }

    public void setMapper(EntityMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public <T> List<T> readAll(Connection connection, TableMetadata<T> metadata) {
        final String TABLE = metadata.getTableName();
        final String[] COLUMNS = getColumnNames(metadata);

        Select sql = select(COLUMNS).from(TABLE);
        return executeSelect(connection, metadata, sql);
    }

    @Override
    public <T> Page<T> readPage(Connection connection, TableMetadata<T> metadata, Pageable pageable) {
        return read(connection, metadata, null, pageable);
    }

    @Override
    public <T> T readById(Connection connection, TableMetadata<T> metadata, Object id) {
        ColumnMetadata primaryKey = metadata.getPrimaryKey();
        if (primaryKey == null) {
            throw new IllegalStateException(String.format(
                    "Entity %s has no primary key defined", metadata.getClazz().getSimpleName()));
        }

        final String TABLE = metadata.getTableName();
        final String[] COLUMNS = getColumnNames(metadata);
        final String PRIMARY_KEY = primaryKey.getColumnName();

        Select sql = select(COLUMNS).from(TABLE).where(column(PRIMARY_KEY).eq("?"));

        return withStatement(connection, sql, dialect, stmt -> {
            try {
                stmt.setObject(1, id);

                try (ResultSet rs = stmt.executeQuery()) {
                    if (!rs.next()) {
                        return null;
                    }

                    Row row = toRow(rs);
                    T entity = mapper.map(row, metadata);

                    if (rs.next()) {
                        throw new IllegalStateException(String.format(
                                "Expected one result but found multiple for ID: %s in table: %s", id, TABLE));
                    }

                    return entity;
                }
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });
    }

    @Override
    public <T> List<T> read(Connection connection, TableMetadata<T> metadata, String sql) {
        return executeSelect(connection, metadata, sql);
    }

    @Override
    public <T> List<T> read(Connection connection, TableMetadata<T> metadata, Select select) {
        return executeSelect(connection, metadata, select);
    }

    @Override
    public <T> List<T> read(Connection connection, TableMetadata<T> metadata, Where where) {
        final String TABLE = metadata.getTableName();
        final String[] COLUMNS = getColumnNames(metadata);

        Select sql = select(COLUMNS).from(TABLE).where(where);

        return executeSelect(connection, metadata, sql);
    }

    @Override
    public <T> Page<T> read(Connection connection, TableMetadata<T> metadata, Where where, Pageable pageable) {
        if (pageable == null) {
            throw new IllegalArgumentException("Pageable must not be null");
        }

        final String TABLE = metadata.getTableName();
        final String[] COLUMNS = getColumnNames(metadata);

        long totalElems = countAll(connection, metadata, where);

        long page = pageable.getPage();
        long pageSize = pageable.getSize();
        long totalPages = (long) Math.ceil((double) totalElems / pageSize);

        Select sql = select(COLUMNS).from(TABLE);
        if (where != null) {
            sql.where(where);
        }

        sql = getPaginatedSelect(sql, pageable);

        List<T> elems = executeSelect(connection, metadata, sql);

        return new Page<>(elems, page, totalElems, totalPages);
    }

    @Override
    public <T> List<T> fetchValues(Connection connection, Select select) {
        String sql = select.build(dialect);
        return fetchValues(connection, sql);
    }

    @Override
    public <T> T fetchValue(Connection connection, Select select) {
        String sql = select.build(dialect);
        return fetchValue(connection, sql);
    }

    @Override
    public <T> List<T> fetchValues(Connection connection, String sql) {
        return withStatement(connection, sql, stmt -> {
            try (ResultSet rs = stmt.executeQuery()) {
                List<T> results = new ArrayList<>();

                while (rs.next()) {
                    T value = (T) rs.getObject(1);
                    results.add(value);
                }

                return results;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to read values from table", e);
            }
        });
    }

    @Override
    public <T> T fetchValue(Connection connection, String sql) {
        return withStatement(connection, sql, stmt -> {
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    return (T) rs.getObject(1);
                }

                return null;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to read value from table", e);
            }
        });
    }

    @Override
    public List<List<?>> fetchRows(Connection connection, Select select) {
        return withStatement(connection, select, dialect, stmt -> {
            try (ResultSet rs = stmt.executeQuery()) {
                List<List<?>> results = new ArrayList<>();
                int columnCount = rs.getMetaData().getColumnCount();

                while (rs.next()) {
                    List<Object> row = new ArrayList<>(columnCount);
                    for (int i = 1; i <= columnCount; i++) {
                        row.add(rs.getObject(i));
                    }
                    results.add(row);
                }

                return results;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to read rows from table: " + select.getTable(), e);
            }
        });
    }

    @Override
    public List<?> fetchRow(Connection connection, Select select) {
        return withStatement(connection, select, dialect, stmt -> {
            try (ResultSet rs = stmt.executeQuery()) {
                List<Object> result = new ArrayList<>();
                if (rs.next()) {
                    int columnCount = rs.getMetaData().getColumnCount();
                    for (int i = 1; i <= columnCount; i++) {
                        result.add(rs.getObject(i));
                    }
                }

                return result;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to read row from table: " + select.getTable(), e);
            }
        });
    }

    private Row toRow(ResultSet rs) {
        try {
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();
            Map<String, Object> values = new HashMap<>();

            for (int i = 1; i <= columnCount; i++) {
                String columnName = meta.getColumnLabel(i);
                Object value = rs.getObject(i);
                values.put(columnName, value);
            }

            return new RowData(values);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to read row from ResultSet", e);
        }
    }

    private String[] getColumnNames(TableMetadata<?> metadata) {
        return metadata.getColumns().values().stream()
                .map(ColumnMetadata::getColumnName)
                .toArray(String[]::new);
    }

    private Select getPaginatedSelect(Select select, Pageable pageable) {
        if (pageable == null) {
            return select;
        }

        final long LIMIT = pageable.getSize();
        final long OFFSET = pageable.getPage() * LIMIT;

        select = select.limit(LIMIT).offset(OFFSET);

        if (pageable.getSort() != null && !pageable.getSort().isEmpty()) {
            SortField[] ORDER_BY = pageable.getSort().toArray(new SortField[0]);
            select = select.orderBy(ORDER_BY);
        }

        return select;
    }

    private <T> long countAll(Connection connection, TableMetadata<T> metadata, Where where) {
        final String TABLE = metadata.getTableName();
        final Column COLUMN = Column.name("*").count();

        Select sql = select(COLUMN).from(TABLE);

        if (where != null) {
            sql.where(where);
        }

        return withStatement(connection, sql, dialect, stmt -> {
            try (ResultSet rs = stmt.executeQuery()) {
                rs.next();
                return rs.getLong(1);
            } catch (SQLException e) {
                throw new RuntimeException("Failed to count rows", e);
            }
        });
    }

    private <T> List<T> executeSelect(Connection connection, TableMetadata<T> metadata, String sql) {
        return withStatement(connection, sql, stmt -> {
            try (ResultSet rs = stmt.executeQuery()) {
                List<T> results = new ArrayList<>();
                while (rs.next()) {
                    Row row = toRow(rs);
                    T entity = mapper.map(row, metadata);
                    results.add(entity);
                }
                return results;
            } catch (SQLException e) {
                throw new RuntimeException("Failed to read entities from table: " + metadata.getTableName(), e);
            }
        });
    }

    private <T> List<T> executeSelect(Connection connection, TableMetadata<T> metadata, Select select) {
        String sql = select.build(dialect);
        return executeSelect(connection, metadata, sql);
    }
}
