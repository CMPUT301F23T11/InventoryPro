package com.example.inventorypro.Activities;

import static java.lang.Integer.parseInt;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.example.inventorypro.Helpers;
import com.example.inventorypro.Item;
import com.example.inventorypro.R;
import com.example.inventorypro.SliderAdapter;
import com.example.inventorypro.SliderItem;
import com.github.dhaval2404.imagepicker.ImagePicker;
import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AddItem Activity is responsible for gathering user input and re-creating the MainActivity with the parsed Item.
 */
public class AddItemActivity extends AppCompatActivity {
    Uri uri;
    private TextView header;
    private TextInputLayout name;
    private TextInputLayout date;
    private TextInputLayout make;
    private TextInputLayout model;
    private TextInputLayout serialNumber;
    private TextInputLayout description;
    private TextInputLayout comments;
    private TextInputLayout value;
    private ImageButton addTagButton, addImageButton, addCodeButton;
    private int selectedPosition;
    private boolean editMode = false;
    List<String> tags;

    private ViewPager2 viewPager2;
    List<SliderItem> sliderItems = new ArrayList<>();



    private Button confirmButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        // Gets values from the EditText
        name = findViewById(R.id.inputItemName);
        value = findViewById(R.id.inputValue);
        date = findViewById(R.id.inputDate);
        make = findViewById(R.id.inputMake);
        model= findViewById(R.id.inputModel);
        serialNumber = findViewById(R.id.inputSerialNumber);
        description = findViewById(R.id.inputDescription);
        comments = findViewById(R.id.comments);
        confirmButton = findViewById(R.id.confirm_button);
        cancelButton = findViewById(R.id.cancel_button);
        header = findViewById(R.id.add_header);
        viewPager2 = findViewById(R.id.viewPagerImageSlider);


        // Set the default date to the current date
        date.getEditText().setText(LocalDate.now().toString());

        addTagButton = findViewById(R.id.addTagButton);
        addImageButton = findViewById(R.id.addImageButton);
        addCodeButton = findViewById(R.id.addcode_button);

        addTagButton.setOnClickListener(Helpers.notImplementedClickListener);
        addImageButton.setOnClickListener(Helpers.notImplementedClickListener);
        addCodeButton.setOnClickListener(Helpers.notImplementedClickListener);

        addImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ImagePicker.with(AddItemActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start();
            }
        });
        //calls sendItem if all inputs are valid
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validateInput()) {
                    if (editMode) {
                        sendEditItem();
                    } else {
                        sendItem();
                    }
                }
            }
        });

        // Calls cancel if the user wants to return to the main activity
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        // Try to get a new item from the intent.
        Item potentialItem = parseItemFromAddItemActivity();
        if (potentialItem != null) {
            // Set EditText values to the values of the selected Item
            name.getEditText().setText(potentialItem.getName());
            name.setHelperText("");
            value.getEditText().setText(String.valueOf(potentialItem.getValue()));
            date.getEditText().setText(potentialItem.getLocalDate().toString());
            make.getEditText().setText(potentialItem.getMake());
            model.getEditText().setText(potentialItem.getModel());
            serialNumber.getEditText().setText(potentialItem.getSerialNumber());
            description.getEditText().setText(potentialItem.getDescription());
            comments.getEditText().setText(potentialItem.getComment());

            // Change the header to "Edit Item"
            header.setText("Edit Item");
            editMode = true;
        }
    }

    /**
     * Overrides the method to receive the result from other activities started for result.
     * This method is invoked when an image is selected using ImagePicker.
     * @param requestCode The integer request code originally supplied to
     *                    startActivityForResult(), allowing you to identify who this
     *                    result came from.
     * @param resultCode The integer result code returned by the child activity
     *                   through its setResult().
     * @param data An Intent, which can return result data to the caller
     *               (various data can be attached to Intent "extras").
     *
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            // Retrieves the selected image's URI from the result Intent
            Uri uri = data.getData();
            if (uri != null) {
                sliderItems.add(new SliderItem(uri));
                viewPager2.setAdapter(new SliderAdapter(sliderItems,viewPager2));
                // Save the URI to the global variable if needed for later use
                this.uri = uri;
                viewPager2.setBackground(null);
            }
        }
    }

    /**
     * Parses the item if this activity is in edit mode and starts MainActivity
     */
    private void sendEditItem() {
        // Intent to return to the main activity
        Intent sendEditIntent = new Intent(this, MainActivity.class);

        // Create a date in LocalDate format from the user input
        LocalDate itemDate = Helpers.parseDate(date.getEditText().getText().toString());

        // Create a new input
        Item editItem = new Item(
                name.getEditText().getText().toString(),
                Double.parseDouble(value.getEditText().getText().toString()),
                itemDate,
                make.getEditText().getText().toString(),
                model.getEditText().getText().toString(),
                serialNumber.getEditText().getText().toString(),
                description.getEditText().getText().toString(),
                comments.getEditText().getText().toString(), tags);

        // Send the edited item back to the main activity
        sendEditIntent.putExtra("edit Item", editItem);
        sendEditIntent.putExtra("edit Position", selectedPosition);
        startActivity(sendEditIntent);
    }

    /**
     * Creates a new item from the user inputs and sends it back to the main activity.
     */
    private void sendItem() {
        // Create a date in LocalDate format from the user input
        LocalDate itemDate = Helpers.parseDate(date.getEditText().getText().toString());

        // Create a new input
        Item newItem = new Item(
                name.getEditText().getText().toString(),
                Double.parseDouble(value.getEditText().getText().toString()),
                itemDate,
                make.getEditText().getText().toString(),
                model.getEditText().getText().toString(),
                serialNumber.getEditText().getText().toString(),
                description.getEditText().getText().toString(),
                comments.getEditText().getText().toString(),
                null);

        // Intent to return to the main activity
        Intent sendItemIntent = new Intent(this, MainActivity.class);

        // Send the new item back to the main activity
        sendItemIntent.putExtra("new Item", newItem);
        startActivity(sendItemIntent);
    }

    /**
     * Returns to the main activity.
     */
    private void cancel() {
        Intent cancelIntent = new Intent(this, MainActivity.class);
        startActivity(cancelIntent);
    }

    /**
     * Validates all the user inputs.
     *
     * @return true if all inputs pass validation, false otherwise.
     */
    private boolean validateInput() {
        // Validate name
        if (name.getEditText().length() == 0) {
            name.setError("This field is required!");
            return false;
        }

        // Validate date
        if (date.getEditText().length() == 0) {
            date.setError("This field is required!");
            return false;
        } else if (date.getEditText().length() != 10) {
            date.setError("Enter (YYYY-MM-DD)!");
            return false;
            // Check for month
        } else if (parseInt(date.getEditText().getText().toString().substring(5, 7)) > 12 ||
                parseInt(date.getEditText().getText().toString().substring(5, 7)) < 1) {
            date.setError("Enter (YYYY-MM-DD)!");
            return false;
            // Validate day
        } else if (parseInt(date.getEditText().getText().toString().substring(8)) < 1 ||
                parseInt(date.getEditText().getText().toString().substring(8)) > 31) {
            date.setError("Enter (YYYY-MM-DD)!");
            return false;
        } else if (parseInt(date.getEditText().getText().toString().substring(8)) < 1 ||
                parseInt(date.getEditText().getText().toString().substring(8)) > 30) {
            List<Integer> thirtyDaymonths = new ArrayList<>(Arrays.asList(4, 6, 9, 11));
            if (thirtyDaymonths.contains(parseInt(date.getEditText().getText().toString().substring(5, 7)))) {
                date.setError("Enter (YYYY-MM-DD)!");
                return false;
            }
        } else if (parseInt(date.getEditText().getText().toString().substring(8)) < 1 ||
                parseInt(date.getEditText().getText().toString().substring(8)) > 29) {

            if (parseInt(date.getEditText().getText().toString().substring(5, 7)) == 2) {
                date.setError("Enter (YYYY-MM-DD)!");
                return false;
            }
        }

        // Validate value
        if (value.getEditText().length() == 0) {
            name.setError("This field is required!");
            return false;
        }
        return true;
    }

    /**
     * Receives a new item if created from the AddItem Fragment.
     *
     * @return New Item if created; otherwise, returns null.
     */
    private Item parseItemFromAddItemActivity() {
        Intent receiverIntent = getIntent();
        Item receivedItem = receiverIntent.getParcelableExtra("edit");
        selectedPosition = getIntent().getIntExtra("editPositon", -1);

        if (receivedItem == null) {
            return null;
        }

        return receivedItem;
    }
}
