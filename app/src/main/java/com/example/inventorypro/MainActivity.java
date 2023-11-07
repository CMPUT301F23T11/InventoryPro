package com.example.inventorypro;

import static java.lang.Integer.parseInt;
import static java.security.AccessController.getContext;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
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
    private ListView listView;
    private ImageButton deleteButton;
    private ImageButton profileButton;
    private DatabaseManager database;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find list view
        listView = findViewById(R.id.itemsListView);
        // creates a delete button
        deleteButton = findViewById(R.id.deleteButton);
        profileButton = findViewById(R.id.profileButton);

        // test sorting settings (these could be conceivably saved per user)
        SortSettings sortSettings = new SortSettings();
        FilterSettings filterSettings = new FilterSettings();

        // creates test database
        database = new DatabaseManager();
        // create database connected test list
        ItemList itemList = new ItemList(this, listView, database, sortSettings,filterSettings);
        database.connect(getUserIdFromIntent(), itemList);
        ItemList.setInstance(itemList);


        TextView total = findViewById(R.id.totalText);
        total.setText(String.format("$%.2f", itemList.getTotalValue()));

        // replaces item1 with a random item (testing behavior).
        ((ImageButton)findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemIntent = new Intent(getBaseContext(), AddItem.class);
                addItemIntent.putExtra(getString(R.string.user_id_token), getUserIdFromIntent());
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

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInActivity = new Intent(getBaseContext(), SignInActivity.class);
                signInActivity.putExtra("logout", true);
                startActivity(signInActivity);
            }
        });
    }
    /**
     * Deletes all the selected items from the listview as well as the database and updates the total value accordingly.
     */
    private void deleteSelectedItems() {
        // TODO: there might be a better way to do this down the line.
        // Actually there is, just do this operation on the ItemList.
        ItemList itemList = ItemList.getInstance();

        ArrayList<Item> copy = new ArrayList<>(itemList.getItemList());
        for (Item item : copy) {
            if (item.isSelected()) {
                itemList.remove(item);
            }
        }
        //

        TextView total = findViewById(R.id.totalText);
        total.setText(String.format("$%.0f", itemList.getTotalValue()));
    }
    public double getTotalValue() {
        return ItemList.getInstance().getTotalValue();
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
     * Extracts user id from intent
     * @return unique google id token if exists null otherwise
     */
    private String getUserIdFromIntent() {
        Intent intent = getIntent();
        String userIdToken = getString(R.string.user_id_token);
        if (intent.hasExtra(userIdToken)) {
            return intent.getExtras().getString(userIdToken);
        }

        return null;
    }
}