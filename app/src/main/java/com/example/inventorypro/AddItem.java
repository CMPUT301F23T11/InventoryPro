package com.example.inventorypro;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class AddItem extends AppCompatActivity {
    private Item newItem;
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

        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validateInput()){
                    /*LocalDate itemDate = LocalDate.of(parseInt(date.getText().toString().substring(0,4)),parseInt(date.getText().toString().substring(5,7)),parseInt(date.getText().toString().substring(8,10)));
                    newItem = new Item(name.getText().toString(),
                    Double.parseDouble(value.getText().toString()),
                    itemDate,
                    make.getText().toString(),
                    model.getText().toString(),
                    serialNumber.getText().toString(),
                    description.getText().toString(),
                    comments.getText().toString());*/
                    sendString();



                }
            }
        });
        cancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cancel();
            }
        });





    }

    private void sendString(){
        Intent sendItemIntent = new Intent(this, MainActivity.class);
        ArrayList<String> itemSending = new ArrayList();
        itemSending.add(name.getText().toString());
        itemSending.add(value.getText().toString());
        itemSending.add(date.getText().toString());
        itemSending.add(make.getText().toString());
        itemSending.add(model.getText().toString());
        itemSending.add(serialNumber.getText().toString());
        itemSending.add(description.getText().toString());
        itemSending.add(comments.getText().toString());




        sendItemIntent.putStringArrayListExtra("new Item",  itemSending);
        startActivity(sendItemIntent);

        //sendItemIntent.putExtra("new Item", name.getText().toString());
        startActivity(sendItemIntent);
    }
    private void cancel(){
        Intent cancelIntent = new Intent(this, MainActivity.class);
        startActivity(cancelIntent);
    }
    private void sendItem(){
        Intent sendItemIntent = new Intent(this, MainActivity.class);
        sendItemIntent.putExtra("new Item",  newItem);
        startActivity(sendItemIntent);
    }
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