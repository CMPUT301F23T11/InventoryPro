package com.example.inventorypro.Activities;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.example.inventorypro.Helpers;
import com.example.inventorypro.Item;
import com.example.inventorypro.R;
import com.example.inventorypro.User;
import com.google.android.material.textfield.TextInputLayout;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * AddItem Activity is responsible for gathering user input and re-creating the MainActivity with the parsed Item.
 */
public class AddItemActivity extends AppCompatActivity {
    private TextView header;
    private TextInputLayout name;
    private TextInputLayout date;
    private TextInputLayout make;
    private TextInputLayout model;
    private TextInputLayout serialNumber;
    private TextInputLayout description;
    private TextInputLayout comments;
    private TextInputLayout value;
    private ImageButton addTagButton, addImageButton, serialNumberScanButton;
    private int selectedPosition;
    private boolean editMode = false;
    List<String> tags;

    private Button confirmButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        if(User.getInstance()==null){
            Intent addItemIntent = new Intent(getBaseContext(), SignInActivity.class);
            startActivity(addItemIntent);
            return;
        }

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

        // Set the default date to the current date
        date.getEditText().setText(LocalDate.now().toString());

        addTagButton = findViewById(R.id.addTagButton);
        addImageButton = findViewById(R.id.addImageButton);
        serialNumberScanButton = findViewById(R.id.serialNumberScanButton);

        addTagButton.setOnClickListener(Helpers.notImplementedClickListener);
        addImageButton.setOnClickListener(Helpers.notImplementedClickListener);

        serialNumberScanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                GmsBarcodeScanning.getClient(getBaseContext())
                    .startScan()
                    .addOnSuccessListener(barcode -> {
                        serialNumber.getEditText().setText(barcode.getRawValue());
                    })
                    .addOnCanceledListener(() -> {
                        Helpers.toast(getBaseContext(), getString(R.string.barcode_scan_cancelled_message));
                    })
                    .addOnFailureListener(e -> {
                        String failureMessage = getString(R.string.barcode_scan_failed_message) + e.getMessage();
                        Helpers.toast(getBaseContext(), failureMessage);
                    });
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
        Item potentialItem = tryGetItemFromIntent();
        String potentialSerialNumber = tryGetSerialNumberFromIntent();

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
        } else if (potentialSerialNumber != null) {
            serialNumber.getEditText().setText(potentialSerialNumber);
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
    private Item tryGetItemFromIntent() {
        Intent receiverIntent = getIntent();
        Item receivedItem = receiverIntent.getParcelableExtra(getString(R.string.edit_item_intent));
        selectedPosition = getIntent().getIntExtra(getString(R.string.edit_item_position_intent), -1);

        if (receivedItem == null) {
            return null;
        }

        return receivedItem;
    }

    private String tryGetSerialNumberFromIntent() {
        Intent receiverIntent = getIntent();
        return receiverIntent.getStringExtra(getString(R.string.serial_number_intent));
    }
}
