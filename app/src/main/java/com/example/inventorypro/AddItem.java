package com.example.inventorypro;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputLayout;

import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

//TODO: preserve filter settings and sort settings of the MainActivity
// might justify putting these into some user preferences static object.

/**
 * AddItem Activity is responsible for gathering user input and re-creating the MainActivity with the parsed Item.
 */
public class AddItem extends AppCompatActivity {
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

    private Button  confirmButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //gets values from the EditText
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

        date.getEditText().setText(LocalDate.now().toString());

        addTagButton = findViewById(R.id.addTagButton);
        addImageButton = findViewById(R.id.addImageButton);
        addCodeButton = findViewById(R.id.addcode_button);

        addTagButton.setOnClickListener(Helpers.NotImplementedClickListener);
        addImageButton.setOnClickListener(Helpers.NotImplementedClickListener);
        addCodeButton.setOnClickListener(Helpers.NotImplementedClickListener);

        //calls sendItem if all inputs are valid
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()){
                    if (editMode){
                        sendEditItem();
                    }
                    else{
                        sendItem();
                    }
                }
            }
        });
        // calls cancel, if user wants to return to main activity
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });

        // Try to get new item from intent.
        Item potentialItem = parseItemFromAddItemActivity();
        if (potentialItem != null){
            //set editText to the values of the selected Item
            name.getEditText().setText(potentialItem.getName());
            name.setHelperText("");
            value.getEditText().setText(String.valueOf(potentialItem.getValue()));
            date.getEditText().setText(potentialItem.getLocalDate().toString());
            make.getEditText().setText(potentialItem.getMake());
            model.getEditText().setText(potentialItem.getModel());
            serialNumber.getEditText().setText(potentialItem.getSerialNumber());
            description.getEditText().setText(potentialItem.getDescription());
            comments.getEditText().setText(potentialItem.getComment());
            //changing the header to Edit text
            header.setText("Edit Item");

            editMode = true;
        }
    }

    /**
     * Parses the item if this activity is in edit mode and starts MainActivity
     */
    private void sendEditItem() {
        //intent to return to main activity
        Intent sendEditIntent = new Intent(this, MainActivity.class);

        //create date in LocalDate format from the user input
        LocalDate itemDate = Helpers.parseDate(date.getEditText().getText().toString());

        //create new input
        Item editItem = new Item(name.getEditText().getText().toString(),
                Double.parseDouble(value.getEditText().getText().toString()),
                itemDate,
                make.getEditText().getText().toString(),
                model.getEditText().getText().toString(),
                serialNumber.getEditText().getText().toString(),
                description.getEditText().getText().toString(),
                comments.getEditText().getText().toString(),tags);


        //sends the item back to main activity
        sendEditIntent.putExtra("edit Item", editItem);
        sendEditIntent.putExtra("edit Position", selectedPosition);
        startActivity(sendEditIntent);

    }

    /**
     * Creates a new item from the user inputs
     * sends the item back to the main activity
     */
    private void sendItem(){
        //create date in LocalDate format from the user input
        LocalDate itemDate = Helpers.parseDate(date.getEditText().getText().toString());

        //create new input
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

        //intent to return to main activity
        Intent sendItemIntent = new Intent(this, MainActivity.class);
        //sends the item back to main activity
        sendItemIntent.putExtra("new Item", newItem);
        startActivity(sendItemIntent);
    }

    /**
     * Returns to main activity
     */
    private void cancel(){
        Intent cancelIntent = new Intent(this, MainActivity.class);
        startActivity(cancelIntent);
    }

    /**
     * Validate all the user inputs
     * @return
     * true if all inputs pass validation
     * false otherwise
     */
    private boolean validateInput(){
        //validate name
        if(name.getEditText().length() == 0){
            name.setError("This field is required!");
            return false;
        }

        //validate date
        if(date.getEditText().length() == 0){
            date.setError("This field is required!");
            return false;
        } else if(date.getEditText().length() != 10){
            date.setError("Enter (YYYY-MM-DD)!");
            return false;
            //check for month
        } else if(parseInt(date.getEditText().getText().toString().substring(5,7))>12 || parseInt(date.getEditText().getText().toString().substring(5,7))<1) {
            date.setError("Enter (YYYY-MM-DD)!");
            return false;
            //validate day
        } else if(parseInt(date.getEditText().getText().toString().substring(8))<1 || parseInt(date.getEditText().getText().toString().substring(8)) > 31) {
            date.setError("Enter (YYYY-MM-DD)!");
            return false;
        } else if(parseInt(date.getEditText().getText().toString().substring(8))<1 || parseInt(date.getEditText().getText().toString().substring(8)) > 30) {
            List<Integer> thirtyDaymonths = new ArrayList<>(Arrays.asList(4,6,9,11));
            if (thirtyDaymonths.contains(parseInt(date.getEditText().getText().toString().substring(5,7)))){
                date.setError("Enter (YYYY-MM-DD)!");
                return false;
            }
        } else if(parseInt(date.getEditText().getText().toString().substring(8))<1 || parseInt(date.getEditText().getText().toString().substring(8)) > 29) {

            if (parseInt(date.getEditText().getText().toString().substring(5,7)) == 2){
                date.setError("Enter (YYYY-MM-DD)!");
                return false;
            }



        }

        if(value.getEditText().length() == 0){
            name.setError("This field is required!");
            return false;
        }
        return true;
    }

    /**
     * Receives New Item if created from the AddItem Fragment
     * @return
     * New Item if created else returns null
     */
    private Item parseItemFromAddItemActivity(){

        Intent receiverIntent = getIntent();
        Item receivedItem = receiverIntent.getParcelableExtra("edit");
        selectedPosition = getIntent().getIntExtra("editPositon", -1);

        if(receivedItem==null) {
            return null;
        }

        return receivedItem;
    }

}