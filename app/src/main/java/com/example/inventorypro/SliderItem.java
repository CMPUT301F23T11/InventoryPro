package com.example.inventorypro;

import android.net.Uri;

public class SliderItem {
    private Uri uri;

    public SliderItem(Uri uri){
        this.uri = uri;

    }

    public Uri getImage() {
        return uri;
    }
}
