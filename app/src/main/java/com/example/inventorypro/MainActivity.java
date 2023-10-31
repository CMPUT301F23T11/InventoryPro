package com.example.inventorypro;

import static java.lang.Integer.parseInt;
import static java.sql.DriverManager.println;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.temporal.ChronoField;
import java.util.ArrayList;

import kotlin.random.Random;

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

        // add items to test list
        Item item1 = new Item("Item1", 12.36, LocalDate.of(2023, 9, 12), "make1", "model1", "SN-12345", "Description description", "Comment comment");
        Item item2 = new Item("Item2", 8.5, LocalDate.of(2023, 9, 17), "make2", "model2", "SN-23456", "This is a description", "This is a comment");
        Item item3 = new Item("Item3", 7.97, LocalDate.of(2023, 10, 8), "make3", "model3", "SN-34567", "Another description", null);
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);

        // removes item from test list
        itemList.remove(item3);



        TextView total = findViewById(R.id.totalText);
        total.setText(String.format("$%.2f", itemList.getTotalValue()));

        // replaces item1 with a random item (testing behavior).
        ((ImageButton)findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //itemList.remove(item1);
                //item1.setName("Item"+Random.Default.nextInt());
                //itemList.add(item1);
                Item item346 = new Item("test20202", 55.97, LocalDate.of(2023, 10, 8), "make3", "model3", "SN-34567", "Another description", null);
                itemList.add(item346);
                ArrayList<String> receivedItem = addItem();
                LocalDate itemDate = LocalDate.of(parseInt(receivedItem.get(2).toString().substring(0,4)),parseInt(receivedItem.get(2).toString().substring(5,7)),parseInt(receivedItem.get(2).toString().substring(8,10)));
                Item newItem = new Item(receivedItem.get(0),            //name
                        Double.parseDouble(receivedItem.get(1)),        //value
                        itemDate,                                       //date
                        receivedItem.get(3).toString(),                 //make
                        receivedItem.get(4).toString(),                 //model
                        receivedItem.get(5).toString(),                 //serial number
                        receivedItem.get(6).toString(),                 //description
                        receivedItem.get(7).toString());                //comments

                itemList.add(newItem);


                //Manually trying
                /*Item item34 = new Item("test20955", 55.97, LocalDate.of(2023, 10, 8), "make3", "model3", "SN-34567", "Another description", null);
                itemList.add(item34);
                itemList.remove(item34);*/

            }
        });
    }
    private ArrayList<String> addItem(){
        Intent addItemIntent = new Intent(this, AddItem.class);
        startActivity(addItemIntent);
        Intent receiverIntent = getIntent();
        ArrayList<String> receivedItem = receiverIntent.getStringArrayListExtra("new Item");
        /*LocalDate itemDate = LocalDate.of(parseInt(receivedItem.get(2).toString().substring(0,4)),parseInt(receivedItem.get(2).toString().substring(5,7)),parseInt(receivedItem.get(2).toString().substring(8,10)));
        Item newItem = new Item(receivedItem.get(0),            //name
                Double.parseDouble(receivedItem.get(1)),        //value
                itemDate,                                       //date
                receivedItem.get(3).toString(),                 //make
                receivedItem.get(4).toString(),                 //model
                receivedItem.get(5).toString(),                 //serial number
                receivedItem.get(6).toString(),                 //description
                receivedItem.get(7).toString());                //comments

        itemList.add(newItem);*/


        return receivedItem;


    }
}