package org.nazarius.VovkORM.utils;

public class SortField {
    private final String field;
    private final SortDirection direction;

    public SortField(String field, SortDirection direction) {
        this.field = field;
        this.direction = direction;
    }

    public String getField() {
        return field;
    }

    public SortDirection getDirection() {
        return direction;
    }

    @Override
    public String toString() {
        return field + " " + direction;
    }
}
