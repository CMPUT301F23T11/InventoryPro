package com.example.inventorypro.Activities;

import static java.lang.Integer.parseInt;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.inventorypro.Fragments.CreateTagsFragment;
import com.example.inventorypro.DatabaseManager;
import com.example.inventorypro.FilterSettings;
import com.example.inventorypro.Fragments.DeleteItemsConfirmationDialog;
import com.example.inventorypro.Fragments.SelectTagsFragment;
import com.example.inventorypro.Fragments.UserProfileFragment;
import com.example.inventorypro.Helpers;
import com.example.inventorypro.Item;
import com.example.inventorypro.ItemArrayAdapter;
import com.example.inventorypro.ItemList;
import com.example.inventorypro.R;
import com.example.inventorypro.Fragments.SortFilterDialogFragment;
import com.example.inventorypro.Fragments.SortFragment;
import com.example.inventorypro.SortSettings;
import com.example.inventorypro.TagList;
import com.example.inventorypro.User;
import com.example.inventorypro.Fragments.ViewItemFragment;
import com.google.android.gms.common.api.OptionalModuleApi;
import com.google.android.gms.common.moduleinstall.ModuleInstall;
import com.google.android.gms.common.moduleinstall.ModuleInstallClient;
import com.google.android.gms.common.moduleinstall.ModuleInstallRequest;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.google.mlkit.vision.barcode.common.Barcode;
import com.google.mlkit.vision.codescanner.GmsBarcodeScanning;

import java.security.cert.PKIXRevocationChecker;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.UUID;
import java.util.Arrays;

/**
 * The MainActivity is effectively the "main screen" which launches various dialogues and other activities based on user input.
 */
public class MainActivity extends AppCompatActivity {
    // UI elements
    private ListView listView;
    private ImageButton deleteButton;
    private ImageButton profileButton;
    private ImageButton createsTagsButton,scanButton;
    private int editPosition;
    private ConstraintLayout sortBar;
    private ChipGroup sortChipGroup;
    private ConstraintLayout filterBar;
    private ChipGroup filterChipGroup;
    private ImageButton sortOrderButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if(User.getInstance()==null){
            Intent signInActivity = new Intent(getBaseContext(), SignInActivity.class);
            startActivity(signInActivity);
            return;
        }

        // Initialize UI objects
        listView = findViewById(R.id.itemsListView);
        deleteButton = findViewById(R.id.deleteButton);
        profileButton = findViewById(R.id.profileButton);
        sortBar = findViewById(R.id.sortBar);
        sortChipGroup = findViewById(R.id.sortChipGroup);
        sortOrderButton = findViewById(R.id.sortOrderButton);
        filterBar = findViewById(R.id.filterBar);
        filterChipGroup = findViewById(R.id.filterChipGroup);

        createsTagsButton = findViewById(R.id.createsTagsButton);
        scanButton = findViewById(R.id.scanButton);
        createsTagsButton.setOnClickListener(Helpers.notImplementedClickListener);

        requestPermission(Manifest.permission.READ_EXTERNAL_STORAGE);
        requestPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);

        //TODO: might experience bugs related to static instances being destroyed if you leave and reopen app.
        // solution might be to resend the user to re-login if static vars are null.

        // Need to re-hook instance variables from MainActivity (since this activity can be destroyed).
        ItemList.getInstance().hook(this,listView);

        //Redirect to add Item activity
        ((ImageButton)findViewById(R.id.addButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent addItemIntent = new Intent(getBaseContext(), AddItemActivity.class);
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
                Boolean anyItemSelected = ItemList.getInstance().getSelectedItems().size() > 0;
                if(!anyItemSelected){
                    Helpers.toast(getBaseContext(),"Select item(s) to delete first.");
                    return;
                }

                DialogFragment deleteItemsConfirmationDialog = new DeleteItemsConfirmationDialog();
                deleteItemsConfirmationDialog.show(getSupportFragmentManager(), "deleteItemsConfirmationDialog");
            }
        });

        ((ImageButton)findViewById(R.id.createsTagsButton)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // check if any items are selected. If items are select go to select tags page, else
                // go to create tags page
                ItemList itemList = ItemList.getInstance();
                ArrayList<Item> selectedItems = itemList.getSelectedItems();
                if (!selectedItems.isEmpty()) {
                    DialogFragment selectTags = new SelectTagsFragment(selectedItems, null);
                    selectTags.show(getSupportFragmentManager(), "selectTags");
                } else {
                    DialogFragment createTags = new CreateTagsFragment();
                    createTags.show(getSupportFragmentManager(), "createTags");
                }
            }
        });

        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogFragment userProfileFragment = new UserProfileFragment();
                userProfileFragment.show(getSupportFragmentManager(), "userProfileFragment");
            }
        });

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                openViewItemDialog(position);
            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Context context = getBaseContext();
                ModuleInstallClient moduleInstallClient = ModuleInstall.getClient(context);
                OptionalModuleApi barcodeScanningModule = GmsBarcodeScanning.getClient(context);

                moduleInstallClient
                        .areModulesAvailable(barcodeScanningModule)
                        .addOnSuccessListener(
                                response -> {
                                    if (!response.areModulesAvailable()) {
                                        // Install barcode scanning module as it is not installed
                                        ModuleInstallRequest moduleInstallRequest =
                                                ModuleInstallRequest.newBuilder()
                                                        .addApi(barcodeScanningModule)
                                                        .build();

                                        moduleInstallClient.installModules(moduleInstallRequest);
                                        Helpers.toast(context, getString(R.string.barcode_scan_download_message));
                                    } else {
                                        GmsBarcodeScanning.getClient(context)
                                                .startScan()
                                                .addOnSuccessListener(barcode -> {
                                                    OnBarcodeScanSuccessListener(barcode);
                                                })
                                                .addOnCanceledListener(() -> {
                                                    Helpers.toast(getBaseContext(), getString(R.string.barcode_scan_cancelled_message));
                                                })
                                                .addOnFailureListener(e -> {
                                                    String failureMessage = getString(R.string.barcode_scan_failed_message) + e.getMessage();
                                                    Helpers.toast(getBaseContext(), failureMessage);
                                                });
                                    }
                                })
                        .addOnFailureListener(
                                e -> {
                                    Log.e("SCANNING", getString(R.string.barcode_scan_download_check_failed_message) + e.getMessage());
                                });
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
//            ItemList.getInstance().replace(editedItem,editPosition);
            ItemList.getInstance().updateItem(editedItem);
        }

        showSortAndFilterChips();
        refreshTotalText();

        // Debugging
/*
        Item test = new Item("test",10d,LocalDate.of(2023,11,28),
                "m1","model","112","","",
                Arrays.asList("t1", "t2"));
        ItemList.getInstance().add(test);*/
    }

    /**
     * Refreshes the UI with the calculated new total value for all items.
     */
    public void refreshTotalText(){
        TextView total = findViewById(R.id.totalText);
        total.setText(String.format("$%.2f", ItemList.getInstance().getTotalValue()));
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

    /**
     * Called when a item is clicked in the list of items. Launches ViewItem window.
     * @param position The position of the item that's clicked in the list.
     */
    private void openViewItemDialog(int position) {
        // Retrieve the item based on the position
        ItemList itemList = ItemList.getInstance();
        Item item = itemList.get(position);
        // Create a Bundle and put the Item object into it
        Bundle args = new Bundle();
        args.putParcelable(ViewItemFragment.ARG_ITEM, item);

        // Create an instance of the ViewItem_Fragment fragment and set the Bundle as its arguments
        ViewItemFragment fragment = ViewItemFragment.newInstance(item,position);

        // Use a FragmentManager to display the ViewItem_Fragment fragment as a dialog
        FragmentManager fragmentManager = getSupportFragmentManager();

        // Begin a transaction to show the ViewItem_Fragment fragment as a dialog
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        fragment.show(transaction, "viewItemDialog");
    }

    /**
     * Requests the input permission from the end-user.
     * @param permission The permission to request.
     */
    private void requestPermission(String permission){
        if (ContextCompat.checkSelfPermission(
                this, permission) ==
                PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
        }
        else {
            // You can directly ask for the permission.
            ActivityCompat.requestPermissions(this,
                    new String[] { permission },
                    1);
        }

    }

    /**
     * The result of the permission request from the user.
     * @param requestCode The request code
     * @param permissions The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     *
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions,
                                           int[] grantResults) {
        super.onRequestPermissionsResult(requestCode,permissions,grantResults);
        switch (requestCode) {
            case 0:
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // Permission is granted. Continue the action or workflow
                    // in your app.

                }  else {
                    finishAffinity();
                }
        }

    }


    /**
     * Dynamically show/hides sort and filter bars and relevant chips
     * @return None
     */
    public void showSortAndFilterChips() {
        User userPreferences = User.getInstance();

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
            Chip sortChip = initializeChip(sortType.describe());
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
            Chip filterKeywordsChip = initializeChip(filterKeywordsChipText);

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
            Chip filterFromChip = initializeChip(filterFromChipText);
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
            Chip filterToChip = initializeChip(filterToChipText);
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

        // filter makes chip
        ArrayList<String> filterMakes = filterSettings.getMakes();
        if (filterMakes != null&&filterMakes.size()>0) {
            String filterMakesChipText = String.format("makes: %s", String.join(", ", filterMakes));
            Chip filterMakesChip = initializeChip(filterMakesChipText);
            filterMakesChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userPreferences.getFilterSettings().setMakes(null);
                    ItemList.getInstance().refresh();
                    filterChipGroup.removeView(filterMakesChip);

                    if (filterChipGroup.getChildCount() == 0) {
                        filterBar.setVisibility(View.GONE);
                    }
                }
            });

            filterChipGroup.addView(filterMakesChip);
        }

        // filter tags chip
        ArrayList<String> filterTags = filterSettings.getTags();
        if (filterTags != null) {
            String filterTagsChipText = String.format("tags: %s", String.join(", ", filterTags));
            Chip filterTagsChip = initializeChip(filterTagsChipText);
            filterTagsChip.setOnCloseIconClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    userPreferences.getFilterSettings().setTags(null);
                    ItemList.getInstance().refresh();
                    filterChipGroup.removeView(filterTagsChip);

                    if (filterChipGroup.getChildCount() == 0) {
                        filterBar.setVisibility(View.GONE);
                    }
                }
            });

            filterChipGroup.addView(filterTagsChip);
        }

        if (sortChipGroup.getChildCount() > 0) {
            sortBar.setVisibility(View.VISIBLE);
        }

        if (filterChipGroup.getChildCount() > 0) {
            filterBar.setVisibility(View.VISIBLE);
        }
    }

    /**
     * Creates a chip for displaying the current sorting and filter options.
     * @param description The text for the chip.
     * @return
     */
    private Chip initializeChip(String description) {
        Chip chip = new Chip(this);
        chip.setText(description);
        chip.setCloseIcon(getDrawable(R.drawable.baseline_cancel_24));
        chip.setCloseIconVisible(true);
        return chip;
    }

    /**
     * Opens edit item view for existing item if barcode already exists. Otherwise, opens edit
     * item view with only serial number field populated
     * @param barcode resultant barcode upon successful completion of scan.
     * @return
     */
    private void OnBarcodeScanSuccessListener(Barcode barcode) {
        String serialNumber = barcode.getRawValue();
        int scannedItemPosition = ItemList.getInstance().getPositionFromSerialNumber(serialNumber);

        Intent editItemIntent = new Intent(getBaseContext(), AddItemActivity.class);
        if (scannedItemPosition < 0) {
            // open edit view for new item with only serial number populated
            editItemIntent.putExtra(getString(R.string.serial_number_intent), serialNumber);
        } else {
            // open edit view for existing item
            Item scannedItem = ItemList.getInstance().get(scannedItemPosition);
            editItemIntent.putExtra(getString(R.string.edit_item_intent), scannedItem);
            editItemIntent.putExtra(getString(R.string.edit_item_position_intent), scannedItemPosition);
        }

        startActivity(editItemIntent);
    }
}