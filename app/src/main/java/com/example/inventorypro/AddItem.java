package com.example.inventorypro;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputLayout;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Activity to Help create Items
 */
public class AddItem extends AppCompatActivity {
    private TextInputLayout name;
    private TextInputLayout date;
    private TextInputLayout make;
    private TextInputLayout model;
    private TextInputLayout serialNumber;
    private TextInputLayout description;
    private TextInputLayout comments;
    private TextInputLayout value;

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

        date.getEditText().setText(LocalDate.now().toString());

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
        //create date in LocalDate format from the user input
        LocalDate itemDate = Helpers.ParseDate(date.getEditText().getText().toString());

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
     * validate all the user inputs
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
}