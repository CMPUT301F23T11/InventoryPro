package com.example.inventorypro.Fragments;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.example.inventorypro.Item;
import com.example.inventorypro.ItemList;
import com.example.inventorypro.R;
import com.example.inventorypro.TagList;

import java.util.ArrayList;
import java.util.List;

/**
 * Fragment used when the user wants to select multiple tags (assignment).
 */
public class SelectTagsFragment extends DialogFragment {
    private ArrayList<Item> items;
    private List<String> tags;

    /**
     * Constructor
     * @param items the ArrayList of items that tags are being selected for
     * @param tags the List of tags to select (this is for adding and editing items)
     */
    public SelectTagsFragment(@Nullable ArrayList<Item> items, @Nullable List<String> tags) {
        this.items = items;
        this.tags = tags;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        // set the layout to be used with this dialog fragment
        View view = layoutInflater.inflate(R.layout.select_tags_fragment, viewGroup, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        // get the tag list
        TagList tagList = TagList.getInstance();
        // clear the list of selected tags
        tagList.resetSelectedTags();
        // initialize list
        ListView listView = view.findViewById(R.id.tags_list);

        // Need to re-hook instance variables from here (since this window can be destroyed).
        TagList.getInstance().hookSelectItems(getContext(), listView, items, tags);

        // cancel button functionality
        Button cancelButton = view.findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // if tags are being applied to more than one item then change title to "apply tags" rather
        // than "select tags"
        if (items != null) {
            if (items.size() > 1) {
                TextView textView = view.findViewById(R.id.select_tags_title);
                textView.setText("Apply Tags");

                Button applyButton = view.findViewById(R.id.add_button);
                applyButton.setText("Apply");
                // add tags to all items when apply is pressed
                applyButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // add all tags to all items
                        for (Item item : items) {
                            for (String tag : tagList.getSelectedTags()) {
                                item.addTag(tag);
                                // update item in database
                                ItemList itemList = ItemList.getInstance();
                                itemList.updateItem(item);
                            }
                        }
                        // close
                        dismiss();
                    }
                });
            } else if (items.size() == 1) {
                Button selectButton = view.findViewById(R.id.add_button);
                selectButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // set all tags for item
                        items.get(0).setTags(tagList.getSelectedTags());
                        // update item in database
                        ItemList itemList = ItemList.getInstance();
                        itemList.updateItem(items.get(0));
                        // close
                        dismiss();
                    }
                });
            }
        } else if (tags != null) {
            Button selectButton = view.findViewById(R.id.add_button);
            selectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    // tags will be set if this is for adding / editing items. This will edit the list
                    // directly used in the adding / editing page
                    tags.clear();
                    tags.addAll(tagList.getSelectedTags());
                    // close
                    dismiss();
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();

        // get the 90% of the screen dimensions
        int width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);
        int height = (int) (getResources().getDisplayMetrics().heightPixels * 0.9);

        // set new screen dimensions
        Dialog dialog = getDialog();
        if (dialog != null) {
            dialog.getWindow().setLayout(width, height);
        }

        // add rounded corners to dialog fragment
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.dialog_fragment_rounded);
        }
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle bundle) {
        Dialog dialog = super.onCreateDialog(bundle);
        return dialog;
    }
}
