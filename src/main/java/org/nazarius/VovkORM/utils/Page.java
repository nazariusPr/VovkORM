package org.nazarius.VovkORM.utils;

import java.util.List;

public class Page<T> {
    private List<T> elems;
    private long currentPage;
    private long totalElems;
    private long totalPages;

    public Page(List<T> elems, long currentPage, long totalElems, long totalPages) {
        this.elems = elems;
        this.currentPage = currentPage;
        this.totalElems = totalElems;
        this.totalPages = totalPages;
    }

    public List<T> getElems() {
        return elems;
    }

    public void setElems(List<T> elems) {
        this.elems = elems;
    }

    public long getCurrentPage() {
        return currentPage;
    }

    public void setCurrentPage(long currentPage) {
        this.currentPage = currentPage;
    }

    public long getTotalElems() {
        return totalElems;
    }

    public void setTotalElems(long totalElems) {
        this.totalElems = totalElems;
    }

    public long getTotalPages() {
        return totalPages;
    }

    public void setTotalPages(long totalPages) {
        this.totalPages = totalPages;
    }
}
