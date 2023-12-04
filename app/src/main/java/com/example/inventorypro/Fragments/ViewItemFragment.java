package com.example.inventorypro.Fragments;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.CompositePageTransformer;
import androidx.viewpager2.widget.MarginPageTransformer;
import androidx.viewpager2.widget.ViewPager2;

import com.example.inventorypro.Activities.AddItemActivity;
import com.example.inventorypro.Activities.MainActivity;
import com.example.inventorypro.DatabaseManager;
import com.example.inventorypro.Item;
import com.example.inventorypro.ItemList;
import com.example.inventorypro.R;
import com.example.inventorypro.SliderAdapter;
import com.example.inventorypro.SliderItem;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;

import java.util.ArrayList;
import java.util.List;

/**
 * Displays a read-only view for the item from which the user can edit the item if they choose.
 */
public class ViewItemFragment extends DialogFragment {

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
    private Button deleteButton;


    List<Uri> imageUris = new ArrayList<>();

    List<SliderItem> sliderItems = new ArrayList<>();

    private ViewPager2 viewPager2;



    /**
     * The key to access the item object this fragment is viewing.
     */
    public static final String ARG_ITEM = "item";
    /**
     * The key to access the position in the list of this item that is being viewed.
     */
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
        ImageButton negativeButton = view.findViewById(R.id.cancel_button);
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
        viewPager2 = view.findViewById(R.id.itemImage);
        deleteButton = view.findViewById(R.id.delete_item_button);

        if (selectedItem != null) {
            //Setting Item values to the EditText to view
            name.getEditText().setText(selectedItem.getName());
            value.getEditText().setText(String.valueOf(selectedItem.getValue()));
            date.getEditText().setText(selectedItem.getLocalDate().toString());
            make.getEditText().setText(selectedItem.getMake());
            model.getEditText().setText(selectedItem.getModel());
            serialNumber.getEditText().setText(selectedItem.getSerialNumber());
            description.getEditText().setText(selectedItem.getDescription());
            comments.getEditText().setText(selectedItem.getComment());

            if (selectedItem.getImageUris().size() > 0) {
                DatabaseManager.getImageUris(selectedItem.getImageUris(), new OnSuccessListener<List<Uri>>() {
                    @Override
                    public void onSuccess(List<Uri> uris) {
                        int count = selectedItem.getImageUris().size();
                        for (int i = 0; i < count; i++) {
                            sliderItems.add(new SliderItem(uris.get(i)));
                        }
                        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, viewPager2,false);

                        viewPager2.setAdapter(sliderAdapter);
                        // Save the URI to the global variable if needed for later use
                        // this.uri = uri;
                        sliderAdapter.notifyDataSetChanged();
                        viewPager2.setBackground(null);

                        viewPager2.setClipToPadding(false);
                        viewPager2.setClipChildren(false);
                        viewPager2.setOffscreenPageLimit(3);
                        viewPager2.getChildAt(0).setOverScrollMode(RecyclerView.OVER_SCROLL_NEVER);


                        CompositePageTransformer compositePageTransformer = new CompositePageTransformer();
                        compositePageTransformer.addTransformer(new MarginPageTransformer(5));
                        compositePageTransformer.addTransformer(new ViewPager2.PageTransformer() {
                            @Override
                            public void transformPage(@NonNull View page, float position) {
                                float r = 1 - Math.abs(position);
                                page.setScaleY(0.85f + r * 0.15f);
                            }
                        });

                        viewPager2.setPageTransformer(compositePageTransformer);


                    }
                });
            }

            deleteButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    DialogFragment deleteItemConfirmationDialog = new DeleteItemConfirmationDialog(ViewItemFragment.this, selectedItem);
                    deleteItemConfirmationDialog.show(getParentFragmentManager(), "deleteItemConfirmationDialog");
                }
            });
        }
        return dialog;
    }

    /**
     * Called when the user requests to edit this item that is being viewed.
     * Starts AddItem Activity prepopulated with the item parameters in edit mode.
     */
    private void editItem(){
        Intent sendItemIntent = new Intent(getContext(), AddItemActivity.class);
        //sends the item back to main activity
        sendItemIntent.putExtra(getString(R.string.edit_item_intent), selectedItem);
        sendItemIntent.putExtra(getString(R.string.edit_item_position_intent), selectedPosition);
        startActivity(sendItemIntent);
    }
    public ViewItemFragment() {
        // Required empty public constructor
    }

    /**
     * Creates a new ViewItem_Fragment with the item and position input.
     * @param item The item to view.
     * @param position The position of the item to view.
     * @return new instance of ViewItem_Fragment with the provided item
     */
    public static ViewItemFragment newInstance(Item item, int position) {
        // Create a new instance of the ViewItem_Fragment.
        ViewItemFragment fragment = new ViewItemFragment();
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

    @Override
    public void onResume() {
        super.onResume();
        // add rounded corners to dialog fragment
        Window window = getDialog().getWindow();
        if (window != null) {
            window.setBackgroundDrawableResource(R.drawable.dialog_fragment_rounded);
        }
    }
}
