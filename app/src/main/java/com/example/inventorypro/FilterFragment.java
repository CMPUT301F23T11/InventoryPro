package com.example.inventorypro;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

public class FilterFragment extends Fragment {
    // TODO: Add class variables to store settings

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        return layoutInflater.inflate(R.layout.filter_fragment, viewGroup, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        // TODO: Add logic for changing settings
    }

    /**
     * Applies all the filter settings to the items list view
     */
    public void apply() {
        // TODO: Change filtered items in list
        // This method is already called from the SortFilterDialogFragment when apply is clicked
        // Make sure that this method will actually change the viewed item list when this is called
    }

    /**
     * Resets all the filter settings to default
     */
    public void clear() {
        // TODO: Reset the settings
        // This method is already called from the SortFilterDialogFragment when clear is clicked
        // This should reset all settings to default
    }
}
