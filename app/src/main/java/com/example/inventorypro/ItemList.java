package com.example.inventorypro;

import android.content.Context;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.inventorypro.Activities.MainActivity;
import com.example.inventorypro.Fragments.SortFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import kotlin.jvm.Synchronized;

/**
 * The list of items for the user automatically synchronized using a DatabaseManager.
 * Uses Singleton pattern.
 */
public class ItemList extends SynchronizedList<Item> {
    @Override
    public void refresh() {
        super.refresh();

        if(context != null){
            ((MainActivity)context).refreshTotalText();
            ((MainActivity)context).showSortAndFilterChips();
        }
    }

    private static ItemList instance = null;
    public static ItemList getInstance(){
        return instance;
    }
    public static void setInstance(ItemList itemList){
        instance = itemList;
    }

    // TODO: Find by barcode
    // TODO: Find by tag
    // TODO: Apply tags to items
    // TODO: Call database management

    public ItemList(DatabaseManager database) {
        super(database);
    }

    @Override
    public void hook(Context context, ListView itemListView) {
        super.hook(context, itemListView);

        // setup list view
        if (context != null && itemListView != null) {
            itemArrayAdapter = new ItemArrayAdapter(context, this, itemList);
            itemListView.setAdapter(itemArrayAdapter);
        }
    }

    @Override
    public void postProcess() {
        super.postProcess();

        filter();
        sort();
    }

    @Override
    protected void addToDatabase(Item item) {
        database.addItem(item);
    }

    @Override
    protected void removeFromDatabase(Item item, boolean deepDelete) {
        database.removeItem(item, deepDelete);
    }
    public ArrayList<Item> getOriginalItemList() {
        return originalItemList;
    }

    public void updateItem(Item item) {
        database.removeItem(item, false);
        database.addItem(item);
    }

    /**
     * Sorts the list of items according to the sorting settings (does not update the UI). Use ItemList.refresh() instead.
     */
    protected void sort(){
        if(User.getInstance().getSortSettings() == null){
            Log.e("ITEMLIST", "Sort settings is null.");
            return;
        }

        SortFragment.SortType sortType = User.getInstance().getSortSettings().getSortType();
        SortFragment.SortOrder sortOrder = User.getInstance().getSortSettings().getSortOrder();

        switch (sortType) {
            case NONE:
                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> item1.getName().compareTo(item2.getName()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> item1.getName().compareTo(item2.getName()));
                }
                break;
            case DATE:
                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> item1.getLocalDate().toString().compareTo(item2.getLocalDate().toString()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> item1.getLocalDate().toString().compareTo(item2.getLocalDate().toString()));
                }
                break;
            case MAKE:
                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> item1.getMake().compareTo(item2.getMake()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> item1.getMake().compareTo(item2.getMake()));
                }
                break;
            case VALUE:
                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> (int) Math.signum(item1.getValue() - item2.getValue()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> (int) Math.signum(item1.getValue() - item2.getValue()));
                }
                break;
            case DESCRIPTION:
                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> item1.getDescription().compareTo(item2.getDescription()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> item1.getDescription().compareTo(item2.getDescription()));
                }
                break;
            case TAG:
                // sort the tags of every item (this might be really really performance heavy), optimize here if needed
                for (Item i : itemList){
                    List<String> tags = i.getTags();
                    if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                        Collections.sort(tags, (item1, item2) -> item1.compareTo(item2));
                    } else {
                        Collections.sort(tags, (item2, item1) -> item1.compareTo(item2));
                    }
                    i.setTags(tags); // Updating the database is not necessary since the set is the same.
                }

                // sort ascending or descending
                if (sortOrder == SortFragment.SortOrder.ASCENDING) {
                    Collections.sort(itemList, (item1, item2) -> item1.tagRepresentation().compareTo(item2.tagRepresentation()));
                } else {
                    Collections.sort(itemList, (item2, item1) -> item1.tagRepresentation().compareTo(item2.tagRepresentation()));
                }
                break;
        }
    }

    /**
     * Filters the list of items according to the sorting settings (does not update the UI). Use ItemList.refresh() instead.
     */
    protected void filter(){
        if(User.getInstance().getFilterSettings() == null){
            Log.e("ITEMLIST", "Filter settings is null.");
            return;
        }

        ArrayList<Item> filteredItems = new ArrayList<>();
        for (Item i : itemList){
            if(User.getInstance().getFilterSettings().itemSatisfiesFilter(i)){
                filteredItems.add(i);
            }
        }
        itemList.clear();
        itemList.addAll(filteredItems);
    }

    /**
     * Gets position of the first item for a given serial number
     * @param serialNumber The serial number of interest.
     * @return position of item if such an item exists, otherwise -1
     */
    public int getPositionFromSerialNumber(String serialNumber) {
        for (int i = 0; i < itemList.size(); i++) {
            if (serialNumber.equals(itemList.get(i).getSerialNumber())) {
                return i;
            }
        }

        return -1;
    }

    /**
     * Gets the total value of all items in the list.
     * @return the total value of all items in the list.
     */
    public double getTotalValue() {
        double total = 0d;
        for (Item item : itemList) {
            total+=item.getValue();
        }
        return total;
    }

    /**
     * Gets a list of all selected items
     * @return an ArrayList of all selected items
     */
    public ArrayList<Item> getSelectedItems() {
        ArrayList<Item> items = new ArrayList<Item>();
        for (Item item : itemList) {
            if (item.isSelected()) {
                items.add(item);
            }
        }
        return items;
    }
}