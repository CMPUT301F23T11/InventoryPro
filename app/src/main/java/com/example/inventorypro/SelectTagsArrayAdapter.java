package com.example.inventorypro;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * The adapter to display the tag selection screen.
 */
public class SelectTagsArrayAdapter extends ArrayAdapter<String> {
    private Context context;
    TagList tagList;

    // This is the ArrayList of items that tags are being selected for
    private ArrayList<Item> items;
    // This is the List of tags that are currently being selected
    private List<String> currentTags;

    public SelectTagsArrayAdapter(Context context, TagList tagList, ArrayList<String> tags, @Nullable ArrayList<Item> items, @Nullable List<String> currentTags) {
        super(context, 0, tags);
        this.context = context;
        this.tagList = tagList;
        this.items = items;
        this.currentTags = currentTags;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(context).inflate(R.layout.select_tags_item, parent, false);
        }

        // edit text
        String tagName = tagList.get(position);
        TextView textView = view.findViewById(R.id.tag_text);
        // set edit text
        textView.setText(tagName);

        // checkbox functionality
        CheckBox checkBox = view.findViewById(R.id.checkbox);
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    tagList.addSelectedTag(tagName);
                } else {
                    tagList.removeSelectedTag(tagName);
                }
            }
        });

        // make the entire item clickable
        LinearLayout linearLayout = view.findViewById(R.id.tag_item);
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBox.toggle();
            }
        });

        // if only one item is selected then check all current tags
        if (items != null && items.size() == 1) {
            if (items.get(0).getTags().contains(tagName)) {
                checkBox.toggle();
            }
        } else if (currentTags != null) {
            // if current tags is not null (this is for add and edit item) then toggle all tags in
            // the list that are in the list of currently selected tags
            if (currentTags.contains(tagName)) {
                checkBox.toggle();
            }
        }

        return view;
    }
}
