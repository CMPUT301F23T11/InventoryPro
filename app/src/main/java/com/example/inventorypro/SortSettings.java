package com.example.inventorypro;


// The objective of the sort fragment is to return a sort preferences.
public class SortSettings{

    private SortFragment.SortOrder sortOrder;
    private SortFragment.SortType sortType;

    public SortSettings(SortFragment.SortOrder sortOrder, SortFragment.SortType sortType) {
        this.sortOrder = sortOrder;
        this.sortType = sortType;
    }
    public SortSettings() {
        this(SortFragment.SortOrder.ASCENDING, SortFragment.SortType.NONE);
    }

    public SortFragment.SortOrder getSortOrder() {
        return sortOrder;
    }

    public void setSortOrder(SortFragment.SortOrder sortOrder) {
        this.sortOrder = sortOrder;
    }

    public SortFragment.SortType getSortType() {
        return sortType;
    }

    public void setSortType(SortFragment.SortType sortType) {
        this.sortType = sortType;
    }
}