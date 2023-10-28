package com.example.inventorypro;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ItemList {
    private ArrayList<Item> itemList = new ArrayList<Item>();
    private ArrayAdapter<Item> itemArrayAdapter;
    private DatabaseManager database;

    // TODO: Sorting
    // TODO: Filtering
    // TODO: Find by barcode
    // TODO: Find by tag
    // TODO: Apply tags to items
    // TODO: Call database management

    public ItemList(Context context, ListView itemListView, DatabaseManager database) {
        // setup list view
        if (context != null && itemListView != null) {
            itemArrayAdapter = new ItemArrayAdapter(context, this, itemList);
            itemListView.setAdapter(itemArrayAdapter);
        }

        this.database = database;
    }

    public void onSynchronize(Item item){
        itemList.clear();
        itemList.add(item);
        Log.d("D",item.toString());
        itemArrayAdapter.notifyDataSetChanged();
    }

    /**
     * Adds an item to the list of items and calls the database manager.
     * @param item The item to add.
     */
    public void add(Item item) {
        /*itemList.add(item);
        if (itemArrayAdapter != null) {
            itemArrayAdapter.notifyDataSetChanged();
        }*/
        // TODO: Call database
        database.addItem(item);
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
        // TODO: Call database
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