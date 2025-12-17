package org.nazarius.VovkORM.mapping.impl;

import java.util.Map;
import org.nazarius.VovkORM.mapping.Row;

public class RowData implements Row {
    private final Map<String, Object> values;

    public RowData(Map<String, Object> values) {
        this.values = values;
    }

    @Override
    public Object get(String columnName) {
        return values.get(columnName);
    }
}
