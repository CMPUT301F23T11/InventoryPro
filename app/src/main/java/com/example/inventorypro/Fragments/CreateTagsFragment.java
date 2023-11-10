package com.example.inventorypro.Fragments;

import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.ColorInt;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.inventorypro.R;

import java.util.ArrayList;

/**
 * This DialogFragment is the screen used to define/create/edit tags.
 * WORK IN PROGRESS.
 */
public class CreateTagsFragment extends DialogFragment {

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater layoutInflater, @Nullable ViewGroup viewGroup, @Nullable Bundle bundle) {
        // set the layout to be used with this dialog fragment
        View view = layoutInflater.inflate(R.layout.create_tags_fragment, viewGroup, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle bundle) {
        // close button
        /*Button closeButton = view.findViewById(R.id.closeButton);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });*/
        ListView listView=view.findViewById(R.id.tags_list);

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
