package com.example.inventorypro;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.lang.reflect.Array;
import java.time.LocalDate;
import java.util.ArrayList;
import java.time.Instant;
import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ItemList itemList;
    private ArrayList<Item> dataList = new ArrayList<>();

    private ListView listView;
    private ImageButton deleteButton;
    private DatabaseManager database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find list view
        listView = findViewById(R.id.itemsListView);
        // creates a delete button
        deleteButton = findViewById(R.id.deleteButton);

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

        // show sort filter dialog fragment
        ((ImageButton)findViewById(R.id.sortFilterButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DialogFragment sortFilterDialogFragment = new SortFilterDialogFragment();
                sortFilterDialogFragment.show(getSupportFragmentManager(), "sortFilterDialogFragment");
            }
        });

        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteSelectedItems();
            }
        });
    }
    /**
     * Deletes all the selected items from the listview as well as the database and updates the total value accordingly.
     */
    private void deleteSelectedItems() {
        // TODO: there might be a better way to do this down the line.
        
        ArrayList<Item> copy = new ArrayList<>(itemList.getItemList());
        for (Item item : copy) {
            if (item.isSelected()) {
                itemList.remove(item);
            }
        }

        TextView total = findViewById(R.id.totalText);
        total.setText(String.format("$%.2f", itemList.getTotalValue()));
    }
    public double getTotalValue() {
        double total = 0d;
        for (Item item : itemList.getItemList()) {
            total+=item.getValue();
        }
        return total;
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

    /**
     * Get a reference to itemList
     */
    public ItemList getItemList() {
        return itemList;
    }
}