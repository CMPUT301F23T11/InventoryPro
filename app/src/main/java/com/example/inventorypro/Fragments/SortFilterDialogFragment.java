package com.example.inventorypro.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.inventorypro.ItemList;
import com.example.inventorypro.R;
import com.example.inventorypro.User;

/**
 * Hosts the SortFragment and FilterFragment.
 * Is responsible for clearing and refreshing the ItemList as sort/filter options change.
 */
public class SortFilterDialogFragment extends DialogFragment {
    private SortFragment sortFragment = new SortFragment();
    private FilterFragment filterFragment = new FilterFragment();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        // set the layout to be used with this dialog fragment
        View view = layoutInflater.inflate(R.layout.sort_filter_dialog_fragment, viewGroup, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        // view sort fragment after view is created
        switchToSort();

        // add button functionality
        // sort and filter buttons
        Button sortButton = view.findViewById(R.id.sortButton);
        Button filterButton = view.findViewById(R.id.filterButton);
        int primaryColor = ContextCompat.getColor(getActivity(), R.color.primary);
        // sort tab functionality
        sortButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToSort();
                sortButton.setBackgroundResource(R.drawable.button_filled);
                sortButton.setTextColor(Color.WHITE);
                filterButton.setBackgroundResource(R.drawable.button_outline);
                filterButton.setTextColor(primaryColor);
            }
        });
        // filter tab functionality
        filterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switchToFilter();
                filterButton.setBackgroundResource(R.drawable.button_filled);
                filterButton.setTextColor(Color.WHITE);
                sortButton.setBackgroundResource(R.drawable.button_outline);
                sortButton.setTextColor(primaryColor);
            }
        });
        // close button
        Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        // clear button
        Button clearButton = view.findViewById(R.id.clearButton);
        clearButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                User.getInstance().getFilterSettings().clear();
                User.getInstance().getSortSettings().clear();
                ItemList.getInstance().refresh();
                dismiss();
            }
        });
        // Apply button
        Button applyButton = view.findViewById(R.id.applyButton);
        applyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ItemList.getInstance().refresh();

                // close sorting view
                dismiss();
            }
        });
    }

    /**
     * Switch the fragment currently being view to the sort fragment
     */
    private void switchToSort() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sort_filter_frame, sortFragment);
        fragmentTransaction.commit();
    }

    /**
     * Switch the fragment currently being view to the filter fragment
     */
    private void switchToFilter() {
        FragmentManager fragmentManager = getChildFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.sort_filter_frame, filterFragment);
        fragmentTransaction.commit();
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
