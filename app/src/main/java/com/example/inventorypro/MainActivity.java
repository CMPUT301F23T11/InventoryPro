package com.example.inventorypro;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    private ListView listView;
    private ImageButton deleteButton;
    private ImageButton profileButton;
    private ImageButton createsTagsButton,scanButton;
    private int editPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // find list view
        listView = findViewById(R.id.itemsListView);
        // creates a delete button
        deleteButton = findViewById(R.id.deleteButton);
        profileButton = findViewById(R.id.profileButton);

        createsTagsButton = findViewById(R.id.createsTagsButton);
        scanButton = findViewById(R.id.scanButton);
        createsTagsButton.setOnClickListener(Helpers.NotImplementedClickListener);
        scanButton.setOnClickListener(Helpers.NotImplementedClickListener);

        // Only create a single instance of ItemList.
        if(ItemList.getInstance() == null){
            // creates test database
            DatabaseManager database = new DatabaseManager();
            // create database connected test list
            ItemList itemList = new ItemList(this, listView, database);
            database.connect(UserPreferences.getInstance().getUserID(), itemList);
            ItemList.setInstance(itemList);
        } else{
            // Need to re-hook instance variables from MainActivity.
            ItemList.getInstance().resetup(this,listView);
        }

        //Redirect to add Item activity
        ((ImageButton)findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemIntent = new Intent(getBaseContext(), AddItem.class);
                startActivity(addItemIntent);
            }
        });

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

        ((ImageButton)findViewById(R.id.createsTagsButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Helpers.NotImplementedToast(v.getContext());
                DialogFragment createTags = new CreateTagsFragment();
                createTags.show(getSupportFragmentManager(), "createTags");
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent signInActivity = new Intent(getBaseContext(), SignInActivity.class);
                signInActivity.putExtra("logout", true);
                startActivity(signInActivity);

                Helpers.Toast(view.getContext(),"You have been signed out.");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                onItemClicked(position);
            }
        });

        // Try to get new item from intent.
        Item potentialItem = parseItemFromAddItemActivity();
        if (potentialItem != null){
            ItemList.getInstance().add(potentialItem);
        }

        // Try to get edited item from intent.
        Item editedItem = parseItemFromEdit();
        if (editedItem != null){
            //itemList.add(editedItem);
            ItemList.getInstance().replace(editedItem,editPosition);

        }

        refreshTotalText();
    }

    /**
     * Refreshes the UI with the calculated new total value for all items.
     */
    public void refreshTotalText(){
        TextView total = findViewById(R.id.totalText);
        total.setText(String.format("$%.2f", ItemList.getInstance().getTotalValue()));
    }

    /**
     * Deletes all the selected items from the listview as well as the database and updates the total value accordingly.
     */
    private void deleteSelectedItems() {
        // TODO: there might be a better way to do this down the line.
        // Actually there is, just do this operation on the ItemList.
        Boolean removedAtLeastOne = Boolean.FALSE;

        ItemList itemList = ItemList.getInstance();

        ArrayList<Item> copy = new ArrayList<>(itemList.getItemList());
        for (Item item : copy) {
            if (item.isSelected()) {
                itemList.remove(item);
                removedAtLeastOne = Boolean.TRUE;
            }
        }

        if(!removedAtLeastOne){
            Helpers.Toast(getBaseContext(),"Select item(s) to delete first.");
        }
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
     * Receives edit Item if created from the AddItem Fragment
     * @return
     * New Item if created else returns null
     */
    private Item parseItemFromEdit(){

        Intent receiverIntent = getIntent();
        Item receivedItem = receiverIntent.getParcelableExtra("edit Item");
        editPosition = receiverIntent.getIntExtra("edit Position",-1);
        if(receivedItem==null) {
            return null;
        }

        return receivedItem;
    }

    public void onItemClicked(int position) {
        // Retrieve the item based on the position
        ItemList itemList = ItemList.getInstance();
        Item item = itemList.get(position);
        // Create a Bundle and put the Item object into it
        Bundle args = new Bundle();
        args.putParcelable(ViewItem_Fragment.ARG_ITEM, item);

        // Create an instance of the ViewItem_Fragment fragment and set the Bundle as its arguments
        ViewItem_Fragment fragment = ViewItem_Fragment.newInstance(item,position);

        // Use a FragmentManager to display the ViewItem_Fragment fragment as a dialog
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Begin a transaction to show the ViewItem_Fragment fragment as a dialog
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment.show(transaction, "viewItemDialog");
    }
}