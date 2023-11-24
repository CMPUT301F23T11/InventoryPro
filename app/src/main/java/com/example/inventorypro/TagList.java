package com.example.inventorypro;

import android.content.Context;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.Collections;

public class TagList {
    // Singleton pattern for tag list. Don't need a reference to main activity.
    private static TagList instance = null;

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

    private ArrayList<String> tagList = new ArrayList<String>();
    private CreateTagsArrayAdapter createTagsArrayAdapter;

    /**
     * Adds a tag String to the tag list.
     * @param tag
     */
    public void add(String tag) {
        // make sure that tag is not empty and doesn't already exist in list before adding
        if (tag != null && !tag.trim().isEmpty() && !tagList.contains(tag)) {
            tagList.add(tag);
            refreshList();
        }
    }

    /**
     * Removes a tag String from the tag list.
     * @param tag
     */
    public void remove(String tag) {
        // make sure that the tag exists before removing
        if (tagList.contains(tag)) {
            tagList.remove(tag);
            refreshList();
        }
    }

    /**
     * Gets the tag at an index.
     * @param index the index of the tag.
     * @return the String of the tag
     */
    public String getTag(int index) {
        return tagList.get(index);
    }

    /**
     * Change an existing tag
     * @param oldTag the String tag that is being replaced
     * @param newTag the String to replace the old tag
     */
    public void editTag(String oldTag, String newTag) {
        // make sure that tag being edited exists
        if (tagList.contains(oldTag)) {
            tagList.set(tagList.indexOf(oldTag), newTag);
            refreshList();
        }
    }

    /**
     * Check if a tag is contained within the tag list
     * @param tag the tag to check
     * @return true if the tag is in the list, false otherwise
     */
    public boolean contains(String tag) {
        return tagList.contains(tag);
    }

    /**
     * Create a new CreateTagsArrayAdapter.
     * @param context the context
     * @param listView the listView to set the adapter
     */
    public void createTagsArrayAdapter(Context context, ListView listView) {
        createTagsArrayAdapter = new CreateTagsArrayAdapter(context, this, tagList);
        listView.setAdapter(createTagsArrayAdapter);
    }

    /**
     * Resorts the list and notifies the array adapter
     */
    private void refreshList() {
        Collections.sort(tagList, String.CASE_INSENSITIVE_ORDER);
        if (createTagsArrayAdapter != null) {
            createTagsArrayAdapter.notifyDataSetChanged();
        }
    }
}
