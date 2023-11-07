package com.example.inventorypro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

// WIP
public class TagArrayAdapter extends ArrayAdapter<Item> {
    private Context context;
    ItemList itemList;

    public TagArrayAdapter(Context context, ItemList itemList, ArrayList<Item> items) {
        super(context, 0, items);
        this.context = context;
        this.itemList = itemList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.item_content, parent, false);
        }

        // WIP

        return view;
    }
}
