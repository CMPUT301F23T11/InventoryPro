package com.example.inventorypro.Fragments;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.inventorypro.Helpers;
import com.example.inventorypro.R;
import com.example.inventorypro.SortSettings;
import com.example.inventorypro.User;

/**
 * Allows the user to adjust their UserPreferences FilterSettings.
 */
public class SortFragment extends Fragment {

    /**
     * Store settings for ascending or descending.
     */
    public enum SortOrder {
        ASCENDING,
        DESCENDING
    }

    /**
     * Store settings for sort type.
     */
    public enum SortType {
        NONE("None"),
        DATE("Date"),
        MAKE("Make"),
        VALUE("Value"),
        DESCRIPTION("Description"),
        TAG("Tag");

        private String description;

        private SortType(String description) {
            this.description = description;
        }

        /**
         * Get the user friendly representation for this enum.
         * @return The user friendly description.
         */
        public String describe() {
            return description;
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return layoutInflater.inflate(R.layout.sort_fragment, viewGroup, false);
    }

    private SortSettings sortSettings(){
        return User.getInstance().getSortSettings();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        // Add functionality to radioGroup (sort type checkboxes)
        RadioGroup radioGroup = (RadioGroup) view.findViewById(R.id.sortType);
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedID) {
                if (checkedID == R.id.sortNone) {
                    sortSettings().setSortType(SortType.NONE);
                } else if (checkedID == R.id.sortDate) {
                    sortSettings().setSortType(SortType.DATE);
                } else if (checkedID == R.id.sortMake) {
                    sortSettings().setSortType(SortType.MAKE);
                } else if (checkedID == R.id.sortValue) {
                    sortSettings().setSortType(SortType.VALUE);
                } else if (checkedID == R.id.sortDescription) {
                    sortSettings().setSortType(SortType.DESCRIPTION);
                } else if (checkedID == R.id.sortTag) {
                    sortSettings().setSortType(SortType.TAG);
                }
            }
        });
        // Set value of radioGroup to current setting
        switch (sortSettings().getSortType()) {
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
                sortSettings().setSortOrder(SortOrder.ASCENDING);
            }
        });
        desButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                desButton.setBackgroundResource(R.drawable.button_filled);
                desButton.setTextColor(Color.WHITE);
                ascButton.setBackgroundResource(R.drawable.button_outline);
                ascButton.setTextColor(primaryColor);
                sortSettings().setSortOrder(SortOrder.DESCENDING);
            }
        });
        // set button based on settings
        switch (sortSettings().getSortOrder()) {
            case ASCENDING:
                ascButton.callOnClick();
                break;
            case DESCENDING:
                desButton.callOnClick();
                break;
        }
    }
}
