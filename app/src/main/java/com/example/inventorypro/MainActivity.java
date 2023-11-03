package com.example.inventorypro;

import static java.lang.Integer.parseInt;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
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

        // test sorting settings (these could be conceivably saved per user)
        SortSettings sortSettings = new SortSettings();

        // create database connected test list
        ItemList itemList = new ItemList(this, listView, database, sortSettings);
        database.connect("gan", itemList);
        ItemList.setInstance(itemList);


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

        // show sort filter dialog fragment
        ((ImageButton)findViewById(R.id.sortFilterButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment sortFilterDialogFragment = new SortFilterDialogFragment();
                sortFilterDialogFragment.show(getSupportFragmentManager(), "sortFilterDialogFragment");
            }
        });
    }

    /**
     * Receives New Item if created from the AddItem Fragment
     * @return
     * New Item if created else returns null
     */
    private Item parseItemFromAddItemActivity(){

        Intent receiverIntent = getIntent();
        Item receivedItem = receiverIntent.getParcelableExtra("new Item");
        if(receivedItem==null) {
            return null;
        }

        return receivedItem;
    }
}