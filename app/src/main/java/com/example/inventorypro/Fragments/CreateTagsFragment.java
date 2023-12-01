package com.example.inventorypro.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.text.InputType;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
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

import com.example.inventorypro.Activities.MainActivity;
import com.example.inventorypro.R;
import com.example.inventorypro.TagList;

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
        // get the tag list
        TagList tagList = TagList.getInstance();
        // initialize list
        ListView listView = view.findViewById(R.id.tags_list);

        // Need to re-hook instance variables from here (since this window can be destroyed).
        TagList.getInstance().hook(getContext(),listView);

        // close button functionality
        Button closeButton = view.findViewById(R.id.close_button);
        closeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        // add button functionality
        Button addButton = view.findViewById(R.id.add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // create edit text input
                EditText input = new EditText(v.getContext());
                // make it so the keyboard has a done button instead of enter
                input.setImeOptions(EditorInfo.IME_ACTION_DONE);
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                // create alert dialog for user input
                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext())
                        .setMessage("Enter New Tag:")
                        .setView(input)
                        .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                tagList.add(input.getText().toString());
                            }
                        })
                        .setNeutralButton("Cancel", null);
                AlertDialog alertDialog = builder.create();
                // get focus of the user input when the dialog is shown
                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                    @Override
                    public void onShow(DialogInterface dialog) {
                        // set focus to EditText
                        input.requestFocus();
                        // open up the keyboard for the user, but there need to be a delay while
                        // the AlertDialog is being opened
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                // show keyboard
                                InputMethodManager inputMethodManager = (InputMethodManager) v.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
                                if (inputMethodManager != null) {
                                    inputMethodManager.showSoftInput(input, InputMethodManager.SHOW_IMPLICIT);
                                }
                            }
                        }, 100);
                    }
                });

                // make clicking done on the keyboard trigger the ok of the alert dialog
                input.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).performClick();
                            return true;
                        }
                        return false;
                    }
                });

                alertDialog.show();
            }
        });
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
