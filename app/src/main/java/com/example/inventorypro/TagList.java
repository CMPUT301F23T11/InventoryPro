package com.example.inventorypro;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class TagList extends SynchronizedList<String> {
    // Singleton pattern for tag list. Don't need a reference to main activity.
    private static TagList instance = null;

    public TagList(DatabaseManager database) {
        super(database);
    }

    /**
     * Fetch the static instance of this tag list.
     * @return
     */
    public static TagList getInstance(){
        return instance;
    }

    /**
     * Set the static instance of this tag list (usually after user authentication).
     * @param tagList The initialized TagList.
     */
    public static void setInstance(TagList tagList) {
        instance = tagList;
    }

    @Override
    public void hook(Context context, ListView itemListView) {
        super.hook(context, itemListView);

        // setup list view
        if (context != null && itemListView != null) {
            createTagsArrayAdapter(context,itemListView);
        }
    }

    /**
     * Adds a tag String to the tag list.
     * @param tag
     */
    public void add(String tag) {
        // make sure that tag is not empty and doesn't already exist in list before adding
        if (tag != null && !tag.trim().isEmpty() && !itemList.contains(tag)) {
            /*tagList.add(tag);
            refreshList();*/
            super.add(tag);
        }
    }

    @Override
    protected void addToDatabase(String item) {
        database.addTag(item);
    }

    /**
     * Removes a tag String from the tag list.
     * @param tag
     */
    public void remove(String tag) {
        // make sure that the tag exists before removing
        if (itemList.contains(tag)) {
            /*tagList.remove(tag);
            refreshList();*/
            super.remove(tag);
        }
    }

    @Override
    protected void removeFromDatabase(String item) {
        database.removeTag(item);
    }


    /**
     * Change an existing tag
     * @param oldTag the String tag that is being replaced
     * @param newTag the String to replace the old tag
     */
    public void editTag(String oldTag, String newTag) {
        // make sure that tag being edited exists
        if (itemList.contains(oldTag)) {
            /*tagList.set(tagList.indexOf(oldTag), newTag);
            // refreshList();*/
            int i = itemList.indexOf(oldTag);
            super.replace(newTag,i);
        }
    }

    /**
     * Check if a tag is contained within the tag list
     * @param tag the tag to check
     * @return true if the tag is in the list, false otherwise
     */
    public boolean contains(String tag) {
        return itemList.contains(tag);
    }

    /**
     * Create a new CreateTagsArrayAdapter.
     * @param context the context
     * @param listView the listView to set the adapter
     */
    public void createTagsArrayAdapter(Context context, ListView listView) {
        itemArrayAdapter = new CreateTagsArrayAdapter(context, this, itemList);
        listView.setAdapter(itemArrayAdapter);
    }

    @Override
    public void postProcess() {
        super.postProcess();
        Collections.sort(itemList, String.CASE_INSENSITIVE_ORDER);
    }
}
