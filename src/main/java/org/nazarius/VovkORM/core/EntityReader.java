package org.nazarius.VovkORM.core;

import java.sql.Connection;
import java.util.List;
import org.nazarius.VovkORM.metadata.TableMetadata;
import org.nazarius.VovkORM.sql.builder.Select;
import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.utils.Page;
import org.nazarius.VovkORM.utils.Pageable;

public interface EntityReader {
    <T> List<T> readAll(Connection connection, TableMetadata<T> metadata);

    <T> Page<T> readPage(Connection connection, TableMetadata<T> metadata, Pageable pageable);

    <T> T readById(Connection connection, TableMetadata<T> metadata, Object id);

    <T> List<T> read(Connection connection, TableMetadata<T> metadata, String sql);

    <T> List<T> read(Connection connection, TableMetadata<T> metadata, Select select);

    <T> List<T> read(Connection connection, TableMetadata<T> metadata, Where where);

    <T> Page<T> read(Connection connection, TableMetadata<T> metadata, Where where, Pageable pageable);

    <T> List<T> fetchValues(Connection connection, Select select);

    <T> T fetchValue(Connection connection, Select select);

    <T> List<T> fetchValues(Connection connection, String sql);

    <T> T fetchValue(Connection connection, String sql);

    List<List<?>> fetchRows(Connection connection, Select select);

    List<?> fetchRow(Connection connection, Select select);
}
