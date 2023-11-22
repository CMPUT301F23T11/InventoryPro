package com.example.inventorypro;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

/**
 * Array adapter to display Tags.
 * WORK IN PROGRESS.
 */
public class CreateTagsArrayAdapter extends ArrayAdapter<String> {
    private Context context;
    TagList tagList;

    public CreateTagsArrayAdapter(Context context, TagList tagList, ArrayList<String> tags) {
        super(context, 0, tags);
        this.context = context;
        this.tagList = tagList;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.create_tags_item, parent, false);
        }

        String tagName = tagList.getTag(position);
        EditText editText = view.findViewById(R.id.tag_text);
        editText.setText(tagName);

        return view;
    }
}
