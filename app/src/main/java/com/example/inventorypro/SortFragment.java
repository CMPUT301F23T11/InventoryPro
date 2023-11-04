package com.example.inventorypro;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import java.util.ArrayList;
import java.util.Collections;

public class SortFragment extends Fragment {
    private ItemList itemList;

    // Store settings for ascending or descending
    private enum SortOrder {
        ASCENDING,
        DESCENDING
    }
    private SortOrder sortOrder = SortOrder.ASCENDING;
    // Store settings for sort type
    private enum SortType {
        NONE,
        DATE,
        MAKE,
        VALUE,
        DESCRIPTION,
        TAG
    }
    private SortType sortType = SortType.NONE;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return layoutInflater.inflate(R.layout.sort_fragment, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        // Get itemList from main activity
        MainActivity mainActivity = (MainActivity) getActivity();
        if (mainActivity != null) {
            itemList = mainActivity.getItemList();
        }

        // Add functionality to radioGroup (sort type checkboxes)
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.sortType);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedID) {
                if (checkedID == R.id.sortNone) {
                    sortType = SortType.NONE;
                } else if (checkedID == R.id.sortDate) {
                    sortType = SortType.DATE;
                } else if (checkedID == R.id.sortMake) {
                    sortType = SortType.MAKE;
                } else if (checkedID == R.id.sortValue) {
                    sortType = SortType.VALUE;
                } else if (checkedID == R.id.sortDescription) {
                    sortType = SortType.DESCRIPTION;
                } else if (checkedID == R.id.sortTag) {
                    sortType = SortType.TAG;
                }
            }
        });
        // Set value of radioGroup to current setting
        switch (sortType) {
            case NONE:
                radioGroup.check(R.id.sortNone);
                break;
            case DATE:
                radioGroup.check(R.id.sortDate);
                break;
            case MAKE:
                radioGroup.check(R.id.sortMake);
                break;
            case VALUE:
                radioGroup.check(R.id.sortValue);
                break;
            case DESCRIPTION:
                radioGroup.check(R.id.sortDescription);
                break;
            case TAG:
                radioGroup.check(R.id.sortTag);
                break;
        }

        // Ascending and descending button functionality
        Button ascButton = view.findViewById(R.id.ascendingButton);
        Button desButton = view.findViewById(R.id.descendingButton);
        int primaryColor = ContextCompat.getColor(getActivity(), R.color.primary);
        ascButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ascButton.setBackgroundResource(R.drawable.button_filled);
                ascButton.setTextColor(Color.WHITE);
                desButton.setBackgroundResource(R.drawable.button_outline);
                desButton.setTextColor(primaryColor);
                sortOrder = SortOrder.ASCENDING;
            }
        });
        desButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desButton.setBackgroundResource(R.drawable.button_filled);
                desButton.setTextColor(Color.WHITE);
                ascButton.setBackgroundResource(R.drawable.button_outline);
                ascButton.setTextColor(primaryColor);
                sortOrder = SortOrder.DESCENDING;
            }
        });
        // set button based on settings
        switch (sortOrder) {
            case ASCENDING:
                ascButton.callOnClick();
                break;
            case DESCENDING:
                desButton.callOnClick();
                break;
        }
    }

    /**
     * Applies all the sorting settings to the items list view
     * @param itemListCopy a list of items that can be modified by this function. This list will
     *                     then be displayed (done in a different function).
     */
    public void apply(ArrayList<Item> itemListCopy) {
        // sort based on the sort type saved
        switch (sortType) {
            case NONE:
                // sort ascending or descending
                if (sortOrder == SortOrder.ASCENDING) {
                    Collections.sort(itemListCopy, (item1, item2) -> item1.getName().compareTo(item2.getName()));
                } else {
                    Collections.sort(itemListCopy, (item2, item1) -> item1.getName().compareTo(item2.getName()));
                }
                break;
            case DATE:
                // sort ascending or descending
                if (sortOrder == SortOrder.ASCENDING) {
                    Collections.sort(itemListCopy, (item1, item2) -> item1.getLocalDate().toString().compareTo(item2.getLocalDate().toString()));
                } else {
                    Collections.sort(itemListCopy, (item2, item1) -> item1.getLocalDate().toString().compareTo(item2.getLocalDate().toString()));
                }
                break;
            case MAKE:
                // sort ascending or descending
                if (sortOrder == SortOrder.ASCENDING) {
                    Collections.sort(itemListCopy, (item1, item2) -> item1.getMake().compareTo(item2.getMake()));
                } else {
                    Collections.sort(itemListCopy, (item2, item1) -> item1.getMake().compareTo(item2.getMake()));
                }
                break;
            case VALUE:
                // sort ascending or descending
                if (sortOrder == SortOrder.ASCENDING) {
                    Collections.sort(itemListCopy, (item1, item2) -> (int)(item1.getValue() - item2.getValue()));
                } else {
                    Collections.sort(itemListCopy, (item2, item1) -> (int)(item1.getValue() - item2.getValue()));
                }
                break;
            case DESCRIPTION:
                // sort ascending or descending
                if (sortOrder == SortOrder.ASCENDING) {
                    Collections.sort(itemListCopy, (item1, item2) -> item1.getDescription().compareTo(item2.getDescription()));
                } else {
                    Collections.sort(itemListCopy, (item2, item1) -> item1.getDescription().compareTo(item2.getDescription()));
                }
                break;
            case TAG:
                break;
        }
    }

    /**
     * Resets all the sorting settings to default
     */
    public void clear() {
        // TODO: Reset the settings
        // This method is already called from the SortFilterDialogFragment when clear is clicked
        // This should reset all settings to default
    }
}
