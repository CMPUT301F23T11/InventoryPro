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

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ItemArrayAdapter extends ArrayAdapter<Item> {
    private Context context;
    ItemList itemList;
    private boolean multiSelectMode = false;

    public ItemArrayAdapter(Context context, ItemList itemList, ArrayList<Item> items) {
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

        // Get TextView objects
        Item item = getItem(position);
        CheckBox checkbox = view.findViewById(R.id.checkbox);
        TextView name = view.findViewById(R.id.itemNameText);
        TextView value = view.findViewById(R.id.itemCostText);
        TextView date = view.findViewById(R.id.itemDateText);
        TextView make = view.findViewById(R.id.itemMakeText);
        TextView model = view.findViewById(R.id.itemModelText);
        TextView serialNumber = view.findViewById(R.id.serialNumberText);

        // Set TextView objects text
        name.setText(item.getName());
        checkbox.setChecked(item.isSelected());
        value.setText(String.format("$%.0f", item.getValue()));
        date.setText(item.getLocalDate().toString());
        make.setText(item.getMake());
        model.setText(item.getModel());
        serialNumber.setText(item.getSerialNumber());

        view.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                multiSelectMode = !multiSelectMode;
                if (multiSelectMode) {
                    checkbox.setVisibility(View.VISIBLE);
                    checkbox.setChecked(true);
                }
                else {
                    checkbox.setVisibility(View.GONE);
                }
                return true;
            }
        });
        checkbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item.setSelected(checkbox.isChecked());
            }
        });

        // Hide checkboxes if multi-select mode is disabled and no items are selected
        if (!areAnyItemsSelected()) {
            multiSelectMode = false;
            checkbox.setVisibility(View.GONE);
        }

        return view;
    }
    // Method to check if any items are selected
    private boolean areAnyItemsSelected() {
        for (Item item : itemList.getItemList()) {
            if (item.isSelected()) {
                return true;
            }
        }
        return false;
    }
//    private void updateCheckboxesVisibility() {
//        if (multiSelectMode) {
//            for (int i = 0; i < getCount(); i++) {
//                View view = getView(i, null, null);
//                CheckBox checkbox = view.findViewById(R.id.checkbox);
//                checkbox.setVisibility(View.VISIBLE);
//            }
//        } else {
//            for (int i = 0; i < getCount(); i++) {
//                View view = getView(i, null, null);
//                CheckBox checkbox = view.findViewById(R.id.checkbox);
//                checkbox.setVisibility(View.GONE);
//            }
//        }
//    }
}
