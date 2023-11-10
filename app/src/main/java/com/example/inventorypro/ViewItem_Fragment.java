package com.example.inventorypro;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ScrollView;

import androidx.fragment.app.DialogFragment;

import com.google.android.material.textfield.TextInputLayout;

public class ViewItem_Fragment extends DialogFragment {

    private Item selectedItem;
    private int selectedPosition;

    private TextInputLayout name;
    private TextInputLayout date;
    private TextInputLayout make;
    private TextInputLayout model;
    private TextInputLayout serialNumber;
    private TextInputLayout description;
    private TextInputLayout comments;
    private TextInputLayout value;

    private Button confirmButton;
    private Button cancelButton;

    private OnFragmentInteractionListener listener;

    public static final String ARG_ITEM = "item";
    public static final String ARG_POSITION = "position";

    /**
     * Create Dialogue to view Item
     * @param savedInstanceState The last saved instance state of the Fragment,
     * or null if this is a freshly created Fragment.
     *
     * @return Dialogue
     */
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());


        // Customize the dialog width
        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        layoutParams.copyFrom(builder.create().getWindow().getAttributes());

        // Set the width to cover 90% of the screen width
        layoutParams.width = (int) (getResources().getDisplayMetrics().widthPixels * 0.9);

        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_view_item, null);

        // Wrap the view inside a ScrollView
        ScrollView scrollView = new ScrollView(getActivity());
        scrollView.addView(view);

        builder.setView(scrollView);




        // Edit button
        Button positiveButton = view.findViewById(R.id.edit_button);
        positiveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Sends Item to AddItem Activity for user to editing
                editItem();

            }
        });

        // Your custom negative button
        Button negativeButton = view.findViewById(R.id.cancel_button);
        negativeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Close the dialog
                dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        // Apply the customized layout parameters
        dialog.getWindow().setAttributes(layoutParams); // Apply the customized layout parameters


        name = view.findViewById(R.id.viewItemName);
        value = view.findViewById(R.id.viewValue);
        date = view.findViewById(R.id.viewDate);
        make = view.findViewById(R.id.viewMake);
        model = view.findViewById(R.id.viewModel);
        serialNumber = view.findViewById(R.id.viewSerialNumber);
        description = view.findViewById(R.id.viewDescription);
        comments = view.findViewById(R.id.viewComments);

        if (selectedItem != null) {
            //Setting Item values to the EditText to view
            name.getEditText().setText(selectedItem.getName());
            value.getEditText().setText(String.valueOf(selectedItem.getValue()));
            date.getEditText().setText(selectedItem.getLocalDate().toString());
            make.getEditText().setText(selectedItem.getMake());
            model.getEditText().setText(selectedItem.getModel());
            serialNumber.getEditText().setText(selectedItem.getSerialNumber());
            description.getEditText().setText(selectedItem.getDescription());
            comments.getEditText().setText(selectedItem.getComment());}


        return dialog;
    }

    /**
     *Interface to communicate between between fragment and main activity
     */
    public interface OnFragmentInteractionListener {
        void onOkPressed(Item item);
    }

    /**
     * Initiates the process of editing a selected item by launching the AddItem activity.
     * The selected item and its position are bundled into an intent and sent to the AddItem activity.
     * The AddItem activity is then responsible for handling the editing process.
     */
    private void editItem(){
        Intent sendItemIntent = new Intent(getContext(), AddItem.class);
        //sends the item back to main activity
        sendItemIntent.putExtra("edit", selectedItem);
        sendItemIntent.putExtra("editPositon", selectedPosition);
        startActivity(sendItemIntent);
    }
    public ViewItem_Fragment() {
        // Required empty public constructor
    }

    /**
     *create a new instance of the ViewItem_Fragment.
     * @param item
     * @param position
     * @return new instance of ViewItem_Fragment with the provided item
     */
    public static ViewItem_Fragment newInstance(Item item, int position) {
        // Create a new instance of the ViewItem_Fragment.
        ViewItem_Fragment fragment = new ViewItem_Fragment();
        // Bundle the item and position into the fragment's arguments.
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        args.putParcelable(ARG_ITEM, item);
        fragment.setArguments(args);

        // Return the newly created fragment.
        return fragment;
    }

    /**
     * Called to do initial creation of the fragment.
     * @param savedInstanceState If the fragment is being re-created from
     * a previous saved state, this is the state.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        // Perform the default creation process of the fragment.
        super.onCreate(savedInstanceState);
        // Check if the fragment has arguments.
        if (getArguments() != null) {
            // Retrieve the item and its position from the arguments.
            selectedItem = getArguments().getParcelable(ARG_ITEM);
            selectedPosition = getArguments().getInt(ARG_POSITION);
        }
    }

    }
