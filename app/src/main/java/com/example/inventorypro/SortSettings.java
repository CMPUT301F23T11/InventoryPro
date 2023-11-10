package com.example.inventorypro;


/**
 * The SortSettings contain all of the settings data required to perform sorting on the item list.
 */
public class SortSettings{

    private SortFragment.SortOrder sortOrder;
    private SortFragment.SortType sortType;


    public SortSettings() {
        this(SortFragment.SortOrder.ASCENDING, SortFragment.SortType.NONE);
    }

    public SortSettings(SortFragment.SortOrder sortOrder, SortFragment.SortType sortType) {
        this.sortOrder = sortOrder;
        this.sortType = sortType;
    }

    /**
     * Clear the settings back to default.
     */
    public void clear(){
        sortOrder = SortFragment.SortOrder.ASCENDING;
        sortType = SortFragment.SortType.NONE;
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