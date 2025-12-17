package org.nazarius.VovkORM.manager.impl;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;
import java.util.function.Function;
import javax.sql.DataSource;
import org.nazarius.VovkORM.core.EntityPersister;
import org.nazarius.VovkORM.core.EntityReader;
import org.nazarius.VovkORM.core.EntityScanner;
import org.nazarius.VovkORM.core.impl.SimpleEntityPersister;
import org.nazarius.VovkORM.core.impl.SimpleEntityReader;
import org.nazarius.VovkORM.core.impl.SimpleEntityScanner;
import org.nazarius.VovkORM.manager.EntityManager;
import org.nazarius.VovkORM.mapping.EntityMapper;
import org.nazarius.VovkORM.mapping.impl.SimpleEntityMapper;
import org.nazarius.VovkORM.metadata.TableMetadata;
import org.nazarius.VovkORM.sql.builder.Delete;
import org.nazarius.VovkORM.sql.builder.Select;
import org.nazarius.VovkORM.sql.builder.Update;
import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.sql.dialect.Dialect;
import org.nazarius.VovkORM.sql.dialect.DialectFactory;
import org.nazarius.VovkORM.utils.Page;
import org.nazarius.VovkORM.utils.Pageable;

public class SimpleEntityManager implements EntityManager {
    private final DataSource dataSource;
    private EntityScanner scanner;
    private EntityReader reader;
    private EntityPersister persister;

    public SimpleEntityManager(DataSource dataSource) {
        this.dataSource = dataSource;
        try (Connection conn = dataSource.getConnection()) {
            Dialect dialect = DialectFactory.from(conn);

            EntityMapper mapper = new SimpleEntityMapper();
            this.scanner = SimpleEntityScanner.getInstance();
            this.reader = new SimpleEntityReader(mapper, dialect);
            this.persister = new SimpleEntityPersister(dialect);

        } catch (SQLException e) {
            throw new RuntimeException("Failed to initialize Entity Manager", e);
        }
    }

    @Override
    public <T> void save(T entity) {}

    @Override
    public <T> T readById(Object id, Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return reader.readById(conn, metadata, id);
        });
    }

    @Override
    public <T> List<T> read(Select select, Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return reader.read(conn, metadata, select);
        });
    }

    @Override
    public <T> List<T> read(Where where, Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return reader.read(conn, metadata, where);
        });
    }

    @Override
    public <T> Page<T> read(Where where, Pageable pageable, Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return reader.read(conn, metadata, where, pageable);
        });
    }

    @Override
    public <T> List<T> fetchValues(Select select) {
        return withConnection(conn -> reader.fetchValues(conn, select));
    }

    @Override
    public <T> T fetchValue(Select select) {
        return withConnection(conn -> reader.fetchValue(conn, select));
    }

    @Override
    public List<List<?>> fetchRows(Select select) {
        return withConnection(conn -> reader.fetchRows(conn, select));
    }

    @Override
    public List<?> fetchRow(Select select) {
        return withConnection(conn -> reader.fetchRow(conn, select));
    }

    @Override
    public <T> int update(T entity, Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return persister.update(conn, metadata, entity);
        });
    }

    @Override
    public <T> int update(Update update) {
        return withConnection(conn -> persister.update(conn, update));
    }

    @Override
    public <T> int update(T entity, Where where, Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return persister.update(conn, metadata, entity, where);
        });
    }

    @Override
    public <T> List<T> readAll(Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return reader.readAll(conn, metadata);
        });
    }

    @Override
    public <T> Page<T> readPage(Pageable pageable, Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return reader.readPage(conn, metadata, pageable);
        });
    }

    @Override
    public <T> int deleteById(Object id, Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return persister.deleteById(conn, metadata, id);
        });
    }

    @Override
    public <T> int delete(T entity, Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return persister.delete(conn, metadata, entity);
        });
    }

    @Override
    public <T> int delete(Delete delete) {
        return withConnection(conn -> persister.delete(conn, delete));
    }

    @Override
    public <T> int delete(Where where, Class<T> clazz) {
        return withConnection(conn -> {
            TableMetadata<T> metadata = scanner.scan(clazz);
            return persister.delete(conn, metadata, where);
        });
    }

    public void setScanner(EntityScanner scanner) {
        this.scanner = scanner;
    }

    public void setReader(EntityReader reader) {
        this.reader = reader;
    }

    public void setPersister(EntityPersister persister) {
        this.persister = persister;
    }

    private <T> T withConnection(Function<Connection, T> action) {
        try (Connection conn = dataSource.getConnection()) {
            return action.apply(conn);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to execute action with connection", e);
        }
    }

    private <T> T withTransaction(Function<Connection, T> action) {
        try (Connection conn = dataSource.getConnection()) {
            conn.setAutoCommit(false);
            try {
                T result = action.apply(conn);
                conn.commit();
                return result;
            } catch (RuntimeException | SQLException e) {
                conn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new RuntimeException("Transaction failed", e);
        }
    }
}
