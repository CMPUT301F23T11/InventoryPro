package com.example.inventorypro;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;

public class ItemList {

    // Singleton pattern for item list. Don't need a reference to main activity anymore.
    private static ItemList instance = null;
    public static ItemList getInstance(){
        return instance;
    }
    public static void setInstance(ItemList itemList){
        instance = itemList;
    }

    Context context;
    ListView itemListView;
    private ArrayList<Item> itemList = new ArrayList<Item>();
    private ArrayAdapter<Item> itemArrayAdapter;
    private DatabaseManager database;

    private SortSettings sortSettings;
    private FilterSettings filterSettings;

    // TODO: Find by barcode
    // TODO: Find by tag
    // TODO: Apply tags to items
    // TODO: Call database management

    public ItemList(Context context, ListView itemListView, DatabaseManager database,
                    @NonNull SortSettings sortSettings,@NonNull FilterSettings filterSettings) {
        // save context and itemListView for later use
        this.context = context;
        this.itemListView = itemListView;

        // setup list view
        if (context != null && itemListView != null) {
            itemArrayAdapter = new ItemArrayAdapter(context, this, itemList);
            itemListView.setAdapter(itemArrayAdapter);
        }

        this.database = database;

        this.sortSettings = sortSettings;
        this.filterSettings = filterSettings;
    }

    /**
     * Called when the database wants to synchronize with the real-time update (from the database).
     * @param items The most up-to-date items.
     */
    public void onSynchronize(ArrayList<Item> items){
        // NOTE: this is called almost immediately after any add/remove (could be a performance problem later).
        itemList.clear();
        itemList.addAll(items);

        refresh();
    }

    // Resorts and filters items then notifies the UI to update.
    public void refresh(){
        if(itemArrayAdapter == null) return;

        filter();
        sort();
        itemArrayAdapter.notifyDataSetChanged();

        if(context != null){
            ((MainActivity)context).refreshTotalText();
        }
    }

    /**
     * Adds an item to the list of items and calls the database manager.
     * @param item The item to add.
     */
    public void add(Item item) {
        itemList.add(item);
        if (itemArrayAdapter != null) {
            itemArrayAdapter.notifyDataSetChanged();
        }

        if (database != null){
            database.addItem(item);
        }

        refresh(); //inefficient
    }

    /**
     * Removes an item from the list of items and calls the database manager.
     * @param item The item to remove.
     */
    public void remove(Item item) {
        itemList.remove(item);
        if (itemArrayAdapter != null) {
            itemArrayAdapter.notifyDataSetChanged();
        }

        if (database != null){
            database.removeItem(item);
        }

        refresh(); //inefficient
    }
    /**
     * Gets the item list of an ItemList object
     * @return
     * Returns the item list for a specific ItemList object
     */
    public ArrayList<Item> getItemList() {
        return itemList;
    }

    /**
     * Sorts the list of items according to the sorting settings (does not update the UI). Use ItemList.refresh() instead.
     */
    private void sort(){
        // Sorts according to sorting settings.

        SortFragment.SortType sortType = sortSettings.getSortType();
        SortFragment.SortOrder sortOrder = sortSettings.getSortOrder();

        switch (sortType) {
            case NONE:
                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> item1.getName().compareTo(item2.getName()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> item1.getName().compareTo(item2.getName()));
                }
                break;
            case DATE:
                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> item1.getLocalDate().toString().compareTo(item2.getLocalDate().toString()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> item1.getLocalDate().toString().compareTo(item2.getLocalDate().toString()));
                }
                break;
            case MAKE:
                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> item1.getMake().compareTo(item2.getMake()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> item1.getMake().compareTo(item2.getMake()));
                }
                break;
            case VALUE:
                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> (int) (item1.getValue() - item2.getValue()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> (int) (item1.getValue() - item2.getValue()));
                }
                break;
            case DESCRIPTION:
                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> item1.getDescription().compareTo(item2.getDescription()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> item1.getDescription().compareTo(item2.getDescription()));
                }
                break;
            case TAG:
                break;
        }
    }
    private void filter(){
        ArrayList<Item> filteredItems = new ArrayList<>();
        for (Item i : itemList){
            if(filterSettings.itemSatisfiesFilter(i)){
                filteredItems.add(i);
            }
        }
        itemList.clear();
        itemList.addAll(filteredItems);
    }

    /**
     * Gets an item at a position.
     * @param position The position to get the item.
     * @return The item at the position.
     */
    public Item get(int position) {
        return itemList.get(position);
    }

    public SortSettings getSortSettings(){return sortSettings;}
    public void setSortSettings(SortSettings sortSettings) {
        this.sortSettings = sortSettings;
    }

    public FilterSettings getFilterSettings() {
        return filterSettings;
    }
    public void setFilterSettings(FilterSettings filterSettings) {
        this.filterSettings = filterSettings;
    }

    /**
     * Gets the total value of all items in the list.
     * @return the total value of all items in the list.
     */
    public double getTotalValue() {
        double total = 0d;
        for (Item item : itemList) {
            total+=item.getValue();
        }
        return total;
    }
}