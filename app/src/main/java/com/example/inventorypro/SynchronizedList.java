package com.example.inventorypro;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.example.inventorypro.Activities.MainActivity;
import com.example.inventorypro.Fragments.SortFragment;

import java.util.ArrayList;
import java.util.Collections;

/**
 * Synchronized list is a collection of objects that are synced with the database and support operations like adding/deleting.
 */
public abstract class SynchronizedList<T1> {

    protected Context context;
    protected ListView itemListView;
    protected ArrayList<T1> itemList = new ArrayList<T1>();
    protected ArrayAdapter<T1> itemArrayAdapter;
    protected DatabaseManager database;

    //TODO: make this approach cleaner. Currently, itemList is the list of items as filtered/sorted. Store a copy of original.
    protected ArrayList<T1> originalItemList = new ArrayList<T1>();


    public SynchronizedList(DatabaseManager database) {
        this.database = database;
    }

    /**
     * Used to update references when the MainActivity is created and destroyed.
     * @param context From MainActivity
     * @param itemListView The updated list view object.
     */
    public void hook(Context context, ListView itemListView){
        this.context = context;
        this.itemListView = itemListView;
    }

    /**
     * Called when the database wants to synchronize with the real-time update (from the database).
     * @param items The most up-to-date items.
     */
    public void onSynchronize(ArrayList<T1> items){
        // NOTE: this is called almost immediately after any add/remove (could be a performance problem later).
        Log.e("GAN", "Database sync occuring " + items.size() + " items.");
        itemList.clear();
        itemList.addAll(items);

        originalItemList.clear();
        originalItemList.addAll(items);

        refresh();
    }

    public void update(){
        itemArrayAdapter.notifyDataSetChanged();
    }
    /**
     * Resorts and filters items then notifies the UI to update.
     */
    public void refresh(){
        if(itemArrayAdapter == null) return;

        // Must re-add the original items (this should be cleaner later)
        itemList.clear();
        itemList.addAll(originalItemList);
        postProcess();
        itemArrayAdapter.notifyDataSetChanged();

    }
    // Modify the item list (sort/filter)
    public void postProcess(){

    }

    /**
     * Adds an item to the list of items and calls the database manager.
     * @param item The item to add.
     */
    public void add(T1 item) {
        itemList.add(item);
        if (itemArrayAdapter != null) {
            itemArrayAdapter.notifyDataSetChanged();
        }

        if (database != null){
            addToDatabase(item);
        }

        refresh(); //inefficient
    }
    protected abstract void addToDatabase(T1 item);


    /**
     * Removes an item from the list of items and calls the database manager.
     * @param item The item to remove.
     */
    public void remove(T1 item) {
        itemList.remove(item);
        if (itemArrayAdapter != null) {
            itemArrayAdapter.notifyDataSetChanged();
        }

        if (database != null){
            removeFromDatabase(item);
        }

        refresh(); //inefficient
    }
    protected abstract void removeFromDatabase(T1 item);

    public void replace(T1 item, T1 old){
        replace(item,originalItemList.indexOf(old));
    }
    /**
     * Replaces the old item at position with a new item.
     * @param item The new item.
     * @param position The position of the old item to replace.
     */
    public void replace(T1 item, int position){
        T1 oldItem = originalItemList.get(position);
        originalItemList.set(position,item);

        if(database != null){
            replaceInDatabase(oldItem,item);
        }

        refresh();
    }
    protected void replaceInDatabase(T1 oldItem, T1 item){
        removeFromDatabase(oldItem);
        addToDatabase(item);
    }

    /**
     * Gets the item list of an ItemList object
     * @return
     * Returns the item list for a specific ItemList object
     */
    public ArrayList<T1> getItemList() {
        return itemList;
    }
    public ArrayList<T1> getOriginalItemList() {
        return originalItemList;
    }

    /**
     * Gets an item at a position.
     * @param position The position to get the item.
     * @return The item at the position.
     */
    public T1 get(int position) {
        return itemList.get(position);
    }

}