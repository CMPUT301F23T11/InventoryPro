package com.example.inventorypro;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.type.DateTime;

import java.time.LocalDate;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    // UI elements
    private ListView listView;
    private ImageButton deleteButton;
    private ImageButton profileButton;
    private DatabaseManager database;
    private ConstraintLayout sortBar;
    private ChipGroup sortChipGroup;
    private ConstraintLayout filterBar;
    private ChipGroup filterChipGroup;
    private ImageButton sortOrderButton;
    private int editPosition;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize UI objects
        listView = findViewById(R.id.itemsListView);
        deleteButton = findViewById(R.id.deleteButton);
        profileButton = findViewById(R.id.profileButton);
        sortBar = findViewById(R.id.sortBar);
        sortChipGroup = findViewById(R.id.sortChipGroup);
        sortOrderButton = findViewById(R.id.sortOrderButton);
        filterBar = findViewById(R.id.filterBar);
        filterChipGroup = findViewById(R.id.filterChipGroup);

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
        ItemList itemList = ItemList.getInstance();

        ArrayList<Item> copy = new ArrayList<>(itemList.getItemList());
        for (Item item : copy) {
            if (item.isSelected()) {
                itemList.remove(item);
            }
        }
        //


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


    /**
     * Dynamically show/hides sort and filter bars and relevant chips
     * @return None
     */
    public void showSortAndFilterChips() {
        UserPreferences userPreferences = UserPreferences.getInstance();

        // reset all previous chips and hide bars
        sortChipGroup.removeAllViews();
        filterChipGroup.removeAllViews();
        sortBar.setVisibility(View.GONE);
        filterBar.setVisibility(View.GONE);

        // populate sort chips
        SortSettings sortSettings = userPreferences.getSortSettings();
        SortFragment.SortType sortType = sortSettings.getSortType();
        String chipText = sortType.describe();

        if (sortType != SortFragment.SortType.NONE) {
            Chip sortChip = InitializeChip(sortType.describe());
            sortChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userPreferences.getSortSettings().setSortType(SortFragment.SortType.NONE);
                    ItemList.getInstance().refresh();
                    sortChipGroup.removeView(sortChip);

                    if (sortChipGroup.getChildCount() == 0) {
                        sortBar.setVisibility(View.GONE);
                    }
                }
            });
            sortChipGroup.addView(sortChip);

            SortFragment.SortOrder sortOrder = userPreferences.getSortSettings().getSortOrder();
            if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                sortOrderButton.setImageDrawable(getDrawable(R.drawable.baseline_arrow_upward_24));
            } else {
                sortOrderButton.setImageDrawable(getDrawable(R.drawable.baseline_arrow_downward_24));
            }
            sortOrderButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    SortFragment.SortOrder sortOrder = userPreferences.getSortSettings().getSortOrder();
                    if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                        userPreferences.getSortSettings().setSortOrder(SortFragment.SortOrder.DESCENDING);
                        ItemList.getInstance().refresh();
                        sortOrderButton.setImageDrawable(getDrawable(R.drawable.baseline_arrow_downward_24));

                    } else {
                        userPreferences.getSortSettings().setSortOrder(SortFragment.SortOrder.ASCENDING);
                        ItemList.getInstance().refresh();
                        sortOrderButton.setImageDrawable(getDrawable(R.drawable.baseline_arrow_upward_24));
                    }
                }
            });
        }

        // populate filter chips

        FilterSettings filterSettings = userPreferences.getFilterSettings();

        // filter keyword chips
        ArrayList<String> filterKeywords = filterSettings.getKeywords();
        if (filterKeywords != null) {
            String filterKeywordsString = String.join(", ", filterSettings.getKeywords());
            String filterKeywordsChipText = String.format("keywords: %s", filterKeywordsString);
            Chip filterKeywordsChip = InitializeChip(filterKeywordsChipText);

            filterKeywordsChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userPreferences.getFilterSettings().setKeywords(null);
                    ItemList.getInstance().refresh();
                    filterChipGroup.removeView(filterKeywordsChip);

                    if (filterChipGroup.getChildCount() == 0) {
                        filterBar.setVisibility(View.GONE);
                    }
                }
            });
            filterChipGroup.addView(filterKeywordsChip);
        }

        // filter from chip
        LocalDate filterFrom = filterSettings.getFrom();
        String filterFromChipText = String.format("from: %s", filterFrom);
        if (filterFrom != null) {
            Chip filterFromChip = InitializeChip(filterFromChipText);
            filterFromChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userPreferences.getFilterSettings().setFrom(null);
                    ItemList.getInstance().refresh();
                    filterChipGroup.removeView(filterFromChip);

                    if (filterChipGroup.getChildCount() == 0) {
                        filterBar.setVisibility(View.GONE);
                    }
                }
            });
            filterChipGroup.addView(filterFromChip);
        }

        // filter to chip
        LocalDate filterTo = filterSettings.getTo();
        String filterToChipText = String.format("to: %s", filterTo);
        if (filterTo != null) {
            Chip filterToChip = InitializeChip(filterToChipText);
            filterToChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userPreferences.getFilterSettings().setTo(null);
                    ItemList.getInstance().refresh();
                    filterChipGroup.removeView(filterToChip);

                    if (filterChipGroup.getChildCount() == 0) {
                        filterBar.setVisibility(View.GONE);
                    }
                }
            });
            filterChipGroup.addView(filterToChip);
        }

        if (sortChipGroup.getChildCount() > 0) {
            sortBar.setVisibility(View.VISIBLE);
        }

        if (filterChipGroup.getChildCount() > 0) {
            filterBar.setVisibility(View.VISIBLE);
        }

        // TO DO: need to implement chips for tags and makes
        ArrayList<String> filterMakes = filterSettings.getMakes();
        ArrayList<String> filterTags = filterSettings.getTags();
    }

    private Chip InitializeChip(String description) {
        Chip chip = new Chip(this);
        chip.setText(description);
        chip.setCloseIcon(getDrawable(R.drawable.baseline_cancel_24));
        chip.setCloseIconVisible(true);
        return chip;
    }
}