package org.nazarius.VovkORM.metadata;

import java.util.Map;

public class TableMetadata<T> {

    private final String tableName;

    // The key in this map is the Java field name (not the SQL column name).
    // Use ColumnMetadata#getColumnName() to obtain the actual database column name.
    private final Map<String, ColumnMetadata> columns;
    private final Class<T> clazz;

    public TableMetadata(String tableName, Map<String, ColumnMetadata> columns, Class<T> clazz) {
        this.tableName = tableName;
        this.columns = columns;
        this.clazz = clazz;
    }

    public Class<T> getClazz() {
        return clazz;
    }

    public String getTableName() {
        return tableName;
    }

    public Map<String, ColumnMetadata> getColumns() {
        return columns;
    }

    public ColumnMetadata getPrimaryKey() {
        return columns.values().stream()
                .filter(ColumnMetadata::isPrimaryKey)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String toString() {
        return "TableMetadata{"
                + "clazz="
                + clazz.getSimpleName()
                + ", tableName='"
                + tableName
                + ", columns="
                + columns.keySet()
                + '}';
    }
}
