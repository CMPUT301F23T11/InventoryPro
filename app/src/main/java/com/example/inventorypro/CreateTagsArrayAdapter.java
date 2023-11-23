package com.example.inventorypro;

import android.content.Context;
import android.graphics.Color;
import android.hardware.input.InputManager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

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

        // edit text
        String tagName = tagList.getTag(position);
        EditText editText = view.findViewById(R.id.tag_text);
        // set edit text
        editText.setText(tagName);
        // disable edit text to start
        setEditTextEnabled(editText, Boolean.FALSE);

        // edit button functionality
        final Boolean[] enabled = {Boolean.FALSE}; // array makes it editable in button
        ImageButton editButton = view.findViewById(R.id.edit_button);
        // spot to save the tag string before it is edited
        final String[] oldTag = {""}; // array makes it editable in button
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                enabled[0] = !enabled[0];
                setEditTextEnabled(editText, enabled[0]);
                // swap between edit and not edit mode
                if (enabled[0]) {
                    // change edit button to check button
                    editButton.setImageResource(R.drawable.baseline_check_24);
                    // save the original tag string
                    oldTag[0] = editText.getText().toString();
                } else {
                    // change check button to edit button
                    editButton.setImageResource(R.drawable.baseline_edit_24);

                    // update tag
                    String newTag = editText.getText().toString();
                    // make sure that the tag doesn't already exist
                    if (tagList.contains(newTag)) {
                        // change back to old tag
                        editText.setText(oldTag[0]);
                    } else {
                        // update to new tag
                        tagList.editTag(oldTag[0], newTag);
                    }
                }
            }
        });

        // when check on keyboard is pressed, pressed the edit button to finish editing
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    editButton.performClick();
                    return true;
                }
                return false;
            }
        });

        return view;
    }

    /**
     * Enable and disable an EditText object from being editable.
     * @param editText the EditText object to enable and disable.
     */
    private void setEditTextEnabled(EditText editText, Boolean enable) {
        editText.setEnabled(enable);
        editText.setFocusable(enable);
        editText.setFocusableInTouchMode(enable);
        editText.setCursorVisible(enable);
        editText.setTextColor(Color.BLACK);
        // if enabling then set focus to the EditText
        if (enable) {
            // set focus to EditText
            editText.requestFocus();
            // set cursor to end
            editText.setSelection(editText.getText().length());
            // show keyboard
            InputMethodManager inputMethodManager = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputMethodManager != null) {
                inputMethodManager.showSoftInput(editText, InputMethodManager.SHOW_IMPLICIT);
            }
        }
    }
}
