package com.example.inventorypro;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.function.Consumer;

/**
 * The list of items for the user automatically synchronized using a DatabaseManager.
 * Uses Singleton pattern.
 */
public class ItemList {

    // Singleton pattern for item list. Don't need a reference to main activity anymore.
    private static ItemList instance = null;

    /**
     * Fetch the static instance of this item list.
     * @return
     */
    public static ItemList getInstance(){
        return instance;
    }

    /**
     * Set the static instance of this item list (usually after user authentication).
     * @param itemList The initialized ItemList.
     */
    public static void setInstance(ItemList itemList){
        instance = itemList;
    }

    Context context;
    ListView itemListView;
    private ArrayList<Item> itemList = new ArrayList<Item>();
    private ArrayAdapter<Item> itemArrayAdapter;
    private DatabaseManager database;

    //TODO: make this approach cleaner. Currently, itemList is the list of items as filtered/sorted. Store a copy of original.
    private ArrayList<Item> originalItemList = new ArrayList<Item>();

    // TODO: Find by barcode
    // TODO: Find by tag
    // TODO: Apply tags to items
    // TODO: Call database management

    public ItemList(Context context, ListView itemListView, DatabaseManager database) {
        // save context and itemListView for later use
        resetup(context, itemListView);

        this.database = database;
    }

    /**
     * Used to update references when the MainActivity is created and destroyed.
     * @param context From MainActivity
     * @param itemListView The updated list view object.
     */
    public void resetup(Context context, ListView itemListView){
        this.context = context;
        this.itemListView = itemListView;

        // setup list view
        if (context != null && itemListView != null) {

            itemArrayAdapter = new ItemArrayAdapter(context, this, itemList);
            itemListView.setAdapter(itemArrayAdapter);
        }
    }

    /**
     * Called when the database wants to synchronize with the real-time update (from the database).
     * @param items The most up-to-date items.
     */
    public void onSynchronize(ArrayList<Item> items){
        // NOTE: this is called almost immediately after any add/remove (could be a performance problem later).
        Log.e("GAN", "Database sync occuring " + items.size() + " items.");
        itemList.clear();
        itemList.addAll(items);

        originalItemList.clear();
        originalItemList.addAll(items);

        refresh();
    }

    /**
     * Resorts and filters items then notifies the UI to update.
     */
    public void refresh(){
        if(itemArrayAdapter == null) return;

        // Must re-add the original items (this should be cleaner later)
        itemList.clear();
        itemList.addAll(originalItemList);

        filter();
        sort();
        itemArrayAdapter.notifyDataSetChanged();

        if(context != null){
            ((MainActivity)context).refreshTotalText();
            ((MainActivity)context).showSortAndFilterChips();
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
     * Replaces the old item at position with a new item.
     * @param item The new item.
     * @param position The position of the old item to replace.
     */
    public void replace(Item item, int position){
        // Lookup the item, don't rely on position which may change.

        // Lookup the old item by comparing their unique IDs.
        Item oldItem = null;
        for(Item i : originalItemList){
            if (i.getUID().equals(item.getUID())){
                oldItem = i;
                break;
            }
        }
        if(oldItem == null) {
            // As a failsafe, this call will automatically update the database and eventually asynchronously
            // call onSynchronize.
            database.addItem(item);
            return;
        }

        // Perform operations to update the original item list.
        originalItemList.remove(oldItem);
        originalItemList.add(item);

        // Perform the same operations on the database (redundant to remove the item I think).
        if (database != null){
            database.removeItem(oldItem);
            database.addItem(item); // Automatically overwrites preexisting data.
        }

        // Tell ItemList to immediately update.
        refresh();
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
    protected void sort(){
        if(UserPreferences.getInstance().getSortSettings() == null){
            Log.e("ITEMLIST", "Sort settings is null.");
            return;
        }

        SortFragment.SortType sortType = UserPreferences.getInstance().getSortSettings().getSortType();
        SortFragment.SortOrder sortOrder = UserPreferences.getInstance().getSortSettings().getSortOrder();

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
                    Collections.sort(itemList, (item1, item2) -> (int) Math.signum(item1.getValue() - item2.getValue()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> (int) Math.signum(item1.getValue() - item2.getValue()));
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

    /**
     * Filters the list of items according to the sorting settings (does not update the UI). Use ItemList.refresh() instead.
     */
    protected void filter(){
        if(UserPreferences.getInstance().getFilterSettings() == null){
            Log.e("ITEMLIST", "Filter settings is null.");
            return;
        }

        ArrayList<Item> filteredItems = new ArrayList<>();
        for (Item i : itemList){
            if(UserPreferences.getInstance().getFilterSettings().itemSatisfiesFilter(i)){
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