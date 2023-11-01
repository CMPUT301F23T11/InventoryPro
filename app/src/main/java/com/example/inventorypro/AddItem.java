package com.example.inventorypro;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.time.LocalDate;

/**
 * Activity to Help create Items
 */
public class AddItem extends AppCompatActivity {
    private EditText name;
    private EditText date;
    private EditText make;
    private EditText model;
    private EditText serialNumber;
    private EditText description;
    private EditText comments;
    private EditText value;

    private Button  confirmButton;
    private Button cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_item);

        //gets values from the EditText
        name = findViewById(R.id.itemNameText);
        value = findViewById(R.id.itemValue);
        date = findViewById(R.id.itemDateText);
        make = findViewById(R.id.itemMakeText);
        model= findViewById(R.id.itemModelText);
        serialNumber = findViewById(R.id.serialNumberText);
        description = findViewById(R.id.itemDescriptionText);
        comments = findViewById(R.id.itemComments);
        confirmButton = findViewById(R.id.confirm_button);
        cancelButton = findViewById(R.id.cancel_button);

        //calls sendItem if all inputs are valid
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()){
                    sendItem();
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
    }

    /**
     * creates a new item from the user inputs
     * sends the item back to the main activity
     */
    private void sendItem(){
        //intent to return to main activity
        Intent sendItemIntent = new Intent(this, MainActivity.class);

        //create date in LocalDate format from the user input
        LocalDate itemDate = LocalDate.of(parseInt(date.getText().toString().substring(0,4)),parseInt(date.getText().toString().substring(5,7)),parseInt(date.getText().toString().substring(8,10)));

        //create new input
        Item newItem = new Item(name.getText().toString(),
                Double.parseDouble(value.getText().toString()),
                itemDate,
                make.getText().toString(),
                model.getText().toString(),
                serialNumber.getText().toString(),
                description.getText().toString(),
                comments.getText().toString());


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
     * validate all the user inputs
     * @return
     * true if all inputs pass validation
     * false otherwise
     */
    private boolean validateInput(){
        if(name.length() == 0){
            name.setError("This field is required!");
            return false;
        }

        if(date.length() == 0){
            date.setError("This field is required!");
            return false;
        } else if(date.length() != 10){
            date.setError("Invalid number of characters");
            return false;
        } else if(parseInt(date.getText().toString().substring(5,7))>12) {
            date.setError("Invalid Date! Enter (YYYY-MM-DD)!");
            return false;
        } else if(parseInt(date.getText().toString().substring(5,7))<1) {
            date.setError("Invalid Date! Enter (YYYY-MM-DD)!");
            return false;
        }

        if(value.length() == 0){
            name.setError("This field is required!");
            return false;
        }
        return true;
    }
}