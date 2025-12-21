package org.nazarius.VovkORM.manager;

import java.util.List;
import org.nazarius.VovkORM.sql.builder.Delete;
import org.nazarius.VovkORM.sql.builder.Select;
import org.nazarius.VovkORM.sql.builder.Update;
import org.nazarius.VovkORM.sql.common.Where;
import org.nazarius.VovkORM.utils.Page;
import org.nazarius.VovkORM.utils.Pageable;

public interface EntityManager {
    // Create operations
    <T> void save(T entity);

    // Read operations
    <T> List<T> readAll(Class<T> clazz);

    <T> Page<T> readPage(Pageable pageable, Class<T> clazz);

    <T> T readById(Object id, Class<T> clazz);

    <T> List<T> read(String select, Class<T> clazz);

    <T> List<T> read(Select select, Class<T> clazz);

    <T> List<T> read(Where where, Class<T> clazz);

    <T> Page<T> read(Where where, Pageable pageable, Class<T> clazz);

    <T> List<T> fetchValues(Select select);

    <T> T fetchValue(Select select);

    <T> List<T> fetchValues(String select);

    <T> T fetchValue(String select);

    List<List<?>> fetchRows(Select select);

    List<?> fetchRow(Select select);

    // Update operations
    <T> int update(T entity, Class<T> clazz);

    <T> int update(Update update);

    <T> int update(T entity, Where where, Class<T> clazz);

    // Delete operations
    <T> int deleteById(Object id, Class<T> clazz);

    <T> int delete(T entity, Class<T> clazz);

    int delete(String delete);

    int delete(Delete delete);

    <T> int delete(Where where, Class<T> clazz);
}
