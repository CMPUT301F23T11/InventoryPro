package com.example.inventorypro;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;

import java.time.LocalDate;

public class MainActivity extends AppCompatActivity {
    private ItemList itemList;
    private ListView listView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find list view
        listView = findViewById(R.id.itemsListView);

        // create test list
        itemList = new ItemList(this, listView);
        Item item1 = new Item("Item1", "12.36", LocalDate.of(2023, 9, 12), "make1", "model1", "SN-12345", "Description description", "Comment comment");
        Item item2 = new Item("Item2", "8.5", LocalDate.of(2023, 9, 17), "make2", "model2", "SN-23456", "This is a description", "This is a comment");
        Item item3 = new Item("Item3", "7.97", LocalDate.of(2023, 10, 8), "make3", "model3", "SN-34567", "Another description", null);
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);

        TextView total = findViewById(R.id.totalText);
        total.setText("$" + itemList.getTotalValue().toString());
    }
}