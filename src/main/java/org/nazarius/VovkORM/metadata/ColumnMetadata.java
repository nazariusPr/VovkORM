package org.nazarius.VovkORM.metadata;

import java.lang.reflect.Field;
import org.nazarius.VovkORM.enums.GenerationType;

public class ColumnMetadata {
    private final String columnName;
    private final boolean nullable;
    private final boolean unique;
    private final int length;
    private final boolean primaryKey;
    private final GenerationType generationType;
    private final Field field;

    public ColumnMetadata(
            String columnName,
            boolean nullable,
            boolean unique,
            int length,
            boolean primaryKey,
            GenerationType generationType,
            Field field) {
        this.columnName = columnName;
        this.primaryKey = primaryKey;
        this.nullable = nullable;
        this.unique = unique;
        this.length = length;
        this.generationType = generationType;
        this.field = field;
    }

    public String getColumnName() {
        return columnName;
    }

    public boolean isPrimaryKey() {
        return primaryKey;
    }

    public boolean isNullable() {
        return nullable;
    }

    public boolean isUnique() {
        return unique;
    }

    public int getLength() {
        return length;
    }

    public GenerationType getGenerationType() {
        return generationType;
    }

    public Field getField() {
        return field;
    }

    public Object getValue(Object entity) {
        try {
            return field.get(entity);
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Failed to access field " + field.getName(), e);
        }
    }

    @Override
    public String toString() {
        return "ColumnMetadata{"
                + "columnName='"
                + columnName
                + '\''
                + ", primaryKey="
                + primaryKey
                + ", nullable="
                + nullable
                + ", unique="
                + unique
                + ", length="
                + length
                + ", generationType="
                + generationType
                + ", field="
                + field.getName()
                + '}';
    }
}
