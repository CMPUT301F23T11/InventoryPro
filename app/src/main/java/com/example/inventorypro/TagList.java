package com.example.inventorypro;

import android.content.Context;

import java.util.ArrayList;

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

    /**
     * Adds a tag String to the tag list.
     * @param tag
     */
    public void add(String tag) {
        tagList.add(tag);
    }

    /**
     * Removes a tag String from the tag list.
     * @param tag
     */
    public void remove(String tag) {
        // make sure that the tag exists before removing
        if (tagList.contains(tag)) {
            tagList.remove(tag);
        }
    }

    public String getTag(int index) {
        return tagList.get(index);
    }

    /**
     * Gets a new CreateTagsArrayAdapter. Since there will be more than one type of array adapter
     * for tags, it is not stored in this object.
     * @param context the MainActivity context
     * @return a new CreateTagsArrayAdapter for this tag list
     */
    public CreateTagsArrayAdapter getCreateTagsArrayAdapter(Context context) {
        return new CreateTagsArrayAdapter(context, this, tagList);
    }
}
