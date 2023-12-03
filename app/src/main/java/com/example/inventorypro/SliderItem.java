package com.example.inventorypro;

import android.net.Uri;

/**
 * Represents a single slider item to display an image.
 */
public class SliderItem {
    private Uri uri;

    public SliderItem(Uri uri){
        this.uri = uri;

    }

    /**
     * Gets the URI representing an image.
     * @return The URI pointing to the image.
     */
    public Uri getImage() {
        return uri;
    }
}
