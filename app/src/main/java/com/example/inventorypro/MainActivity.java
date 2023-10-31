package com.example.inventorypro;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ItemList itemList;
    private ArrayList<Item> dataList = new ArrayList<>();

    private ListView listView;
    private DatabaseManager database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find list view
        listView = findViewById(R.id.itemsListView);

        // creates test database
        database = new DatabaseManager();

        // create database connected test list
        itemList = new ItemList(this, listView, database);
        database.connect("testUserID", itemList);

        TextView total = findViewById(R.id.totalText);
        total.setText(String.format("$%.2f", itemList.getTotalValue()));

        // replaces item1 with a random item (testing behavior).
        ((ImageButton)findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemIntent = new Intent(getBaseContext(), AddItem.class);
                startActivity(addItemIntent);
            }
        });

        // Try to get new item from intent.
        Item potentialItem = parseItemFromAddItemActivity();
        if (potentialItem != null){
            itemList.add(potentialItem);
        }
    }
    private Item parseItemFromAddItemActivity(){

        Intent receiverIntent = getIntent();

        ArrayList<String> receivedItem = receiverIntent.getStringArrayListExtra("new Item");
        if(receivedItem==null) {
            return null;
        }

        LocalDate itemDate = LocalDate.of(parseInt(receivedItem.get(2).toString().substring(0,4)),parseInt(receivedItem.get(2).toString().substring(5,7)),parseInt(receivedItem.get(2).toString().substring(8,10)));
        Item newItem = new Item(receivedItem.get(0),            //name
                Double.parseDouble(receivedItem.get(1)),        //value
                itemDate,                                       //date
                receivedItem.get(3).toString(),                 //make
                receivedItem.get(4).toString(),                 //model
                receivedItem.get(5).toString(),                 //serial number
                receivedItem.get(6).toString(),                 //description
                receivedItem.get(7).toString());                //comments

        Toast.makeText(getBaseContext(),""+receivedItem.size(),Toast.LENGTH_LONG).show();

        return newItem;


    }
}