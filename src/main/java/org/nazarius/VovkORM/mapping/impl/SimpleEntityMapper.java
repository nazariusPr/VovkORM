package org.nazarius.VovkORM.mapping.impl;

import java.lang.reflect.Field;
import org.nazarius.VovkORM.mapping.EntityMapper;
import org.nazarius.VovkORM.mapping.Row;
import org.nazarius.VovkORM.metadata.ColumnMetadata;
import org.nazarius.VovkORM.metadata.TableMetadata;

public class SimpleEntityMapper implements EntityMapper {

    @Override
    public <T> T map(Row row, TableMetadata<T> metadata) {
        try {
            T instance = metadata.getClazz().getDeclaredConstructor().newInstance();

            for (ColumnMetadata column : metadata.getColumns().values()) {
                String columnName = column.getColumnName();
                Field field = column.getField();
                field.setAccessible(true);

                Object value = row.get(columnName);
                field.set(instance, value);
            }

            return instance;
        } catch (Exception e) {
            throw new RuntimeException(
                    "Failed to map entity: " + metadata.getClazz().getSimpleName(), e);
        }
    }
}
