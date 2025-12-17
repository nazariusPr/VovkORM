package org.nazarius.VovkORM.utils;

import java.util.List;

public class Pageable {
    private int page;
    private int size;
    private List<SortField> sort;

    public Pageable(int page, int size) {
        this(page, size, null);
    }

    public Pageable(int page, int size, List<SortField> sort) {
        this.page = page;
        this.size = size;
        this.sort = sort;
    }

    public int getPage() {
        return page;
    }

    public void setPage(int page) {
        this.page = page;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public List<SortField> getSort() {
        return sort;
    }

    public void setSort(List<SortField> sort) {
        this.sort = sort;
    }

    @Override
    public String toString() {
        return "PageRequest{" + "page=" + page + ", size=" + size + ", sort=" + sort + '}';
    }
}
