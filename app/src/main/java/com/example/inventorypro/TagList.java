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
        // make sure that the tag doesn't exists before adding
        if (!tagList.contains(tag)) {
            tagList.add(tag);
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
     * Gets a new CreateTagsArrayAdapter. Since there will be more than one type of array adapter
     * for tags, it is not stored in this object.
     * @param context the MainActivity context
     * @return a new CreateTagsArrayAdapter for this tag list
     */
    public CreateTagsArrayAdapter getCreateTagsArrayAdapter(Context context) {
        return new CreateTagsArrayAdapter(context, this, tagList);
    }
}
