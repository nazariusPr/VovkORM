package org.nazarius.VovkORM.core.impl;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.Map;
import org.nazarius.VovkORM.annotations.Column;
import org.nazarius.VovkORM.annotations.PrimaryKey;
import org.nazarius.VovkORM.annotations.Table;
import org.nazarius.VovkORM.core.EntityScanner;
import org.nazarius.VovkORM.enums.GenerationType;
import org.nazarius.VovkORM.metadata.ColumnMetadata;
import org.nazarius.VovkORM.metadata.TableMetadata;

public class SimpleEntityScanner implements EntityScanner {

    private static final SimpleEntityScanner INSTANCE = new SimpleEntityScanner();

    private SimpleEntityScanner() {}

    public static SimpleEntityScanner getInstance() {
        return INSTANCE;
    }

    @Override
    public <T> TableMetadata<T> scan(Class<T> clazz) {
        return scanStatic(clazz);
    }

    public static <T> TableMetadata<T> scanStatic(Class<T> clazz) {
        String tableName = getTableName(clazz);
        Map<String, ColumnMetadata> columns = getColumns(clazz);
        return new TableMetadata<>(tableName, columns, clazz);
    }

    private static String getTableName(Class<?> clazz) {
        Table table = clazz.getAnnotation(Table.class);

        if (table != null && !table.name().isBlank()) {
            return table.name();
        }

        return clazz.getSimpleName();
    }

    private static Map<String, ColumnMetadata> getColumns(Class<?> clazz) {
        Map<String, ColumnMetadata> columns = new HashMap<>();

        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            boolean nullable = true;
            boolean unique = false;
            int length = 255;
            boolean isPrimaryKey = field.isAnnotationPresent(PrimaryKey.class);
            GenerationType generationType = GenerationType.NONE;
            String name = field.getName();

            if (field.isAnnotationPresent(Column.class)) {
                Column col = field.getAnnotation(Column.class);
                if (!col.name().isBlank()) {
                    name = col.name();
                }
                nullable = col.nullable();
                unique = col.unique();
                length = col.length();
            }

            if (isPrimaryKey) {
                PrimaryKey pk = field.getAnnotation(PrimaryKey.class);
                generationType = pk != null ? pk.strategy() : GenerationType.NONE;
            }

            ColumnMetadata columnMetadata =
                    new ColumnMetadata(name, nullable, unique, length, isPrimaryKey, generationType, field);
            columns.put(field.getName(), columnMetadata);
        }

        return columns;
    }
}
