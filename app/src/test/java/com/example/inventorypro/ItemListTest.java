package com.example.inventorypro;

import static org.junit.Assert.assertEquals;

import android.content.Context;
import android.widget.ListView;

import org.junit.Test;

import java.math.BigDecimal;
<<<<<<< HEAD
=======
import java.time.LocalDate;
>>>>>>> bb81172e53d4faac12d48342b13faf4adf2566f3
import java.util.ArrayList;

public class ItemListTest {
    private ItemList mockItemList() {
<<<<<<< HEAD
        ItemList itemList = new ItemList(null, null,null);
        Item item1 = new Item("Item1", 12.36, null, null, null, null, null, null,null);
        Item item2 = new Item("Item2", 8.5, null, null, null, null, null, null,null);
        Item item3 = new Item("Item3", 7.97, null, null, null, null, null, null,null);
=======
        ItemList itemList = new ItemListTestObj(null, null,null);
        Item item1 = new Item("Item1",
                12.36,
                LocalDate.of(2023, 10, 11),
                "make a",
                null,
                null,
                "description 2",
                null,
                null);
        Item item2 = new Item("Item2",
                8.5,
                LocalDate.of(2023, 10, 13),
                "make c",
                null,
                null,
                "description 3",
                null,
                null);
        Item item3 = new Item("Item3",
                7.97,
                LocalDate.of(2022, 10, 14),
                "make b",
                null,
                null,
                "description 1",
                null,
                null);
>>>>>>> bb81172e53d4faac12d48342b13faf4adf2566f3
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        return itemList;

    }
    private void deleteSelectedItems(ItemList itemList) {
        ArrayList<Item> copy = new ArrayList<>(itemList.getItemList());
        for (Item item : copy) {
            if (item.isSelected()) {
                itemList.remove(item);
            }
        }
    }
    @Test
    public void testTotal() {
            ItemList mockList = mockItemList();
            assertEquals(true, mockList.getTotalValue()==28.83);
    }

    @Test
<<<<<<< HEAD
=======
    public void testSortValue() {
        // create mock list
        ItemList mockList = mockItemList();

        // create new user preferences object
        UserPreferences.createInstance(null);
        UserPreferences userPreferences = UserPreferences.getInstance();

        SortSettings sortSettings = userPreferences.getSortSettings();
        sortSettings.setSortOrder(SortFragment.SortOrder.ASCENDING);
        sortSettings.setSortType(SortFragment.SortType.VALUE);
        ItemListTestObj mockListTestObj = (ItemListTestObj) mockList;
        mockListTestObj.sortTest();

        assert(7.97 == mockList.get(0).getValue());
        assert(8.5 == mockList.get(1).getValue());
        assert(12.36 == mockList.get(2).getValue());
    }

    @Test
    public void testSortDate() {
        // create mock list
        ItemList mockList = mockItemList();

        // create new user preferences object
        UserPreferences.createInstance(null);
        UserPreferences userPreferences = UserPreferences.getInstance();

        SortSettings sortSettings = userPreferences.getSortSettings();
        sortSettings.setSortOrder(SortFragment.SortOrder.DESCENDING);
        sortSettings.setSortType(SortFragment.SortType.DATE);
        ItemListTestObj mockListTestObj = (ItemListTestObj) mockList;
        mockListTestObj.sortTest();

        assert("Item2" == mockList.get(0).getName());
        assert("Item1" == mockList.get(1).getName());
        assert("Item3" == mockList.get(2).getName());
    }

    @Test
    public void testSortMake() {
        // create mock list
        ItemList mockList = mockItemList();

        // create new user preferences object
        UserPreferences.createInstance(null);
        UserPreferences userPreferences = UserPreferences.getInstance();

        SortSettings sortSettings = userPreferences.getSortSettings();
        sortSettings.setSortOrder(SortFragment.SortOrder.DESCENDING);
        sortSettings.setSortType(SortFragment.SortType.MAKE);
        ItemListTestObj mockListTestObj = (ItemListTestObj) mockList;
        mockListTestObj.sortTest();

        assert("make c" == mockList.get(0).getMake());
        assert("make b" == mockList.get(1).getMake());
        assert("make a" == mockList.get(2).getMake());
    }

    @Test
    public void testSortDescription() {
        // create mock list
        ItemList mockList = mockItemList();

        // create new user preferences object
        UserPreferences.createInstance(null);
        UserPreferences userPreferences = UserPreferences.getInstance();

        SortSettings sortSettings = userPreferences.getSortSettings();
        sortSettings.setSortOrder(SortFragment.SortOrder.ASCENDING);
        sortSettings.setSortType(SortFragment.SortType.DESCRIPTION);
        ItemListTestObj mockListTestObj = (ItemListTestObj) mockList;
        mockListTestObj.sortTest();

        assert("description 1" == mockList.get(0).getDescription());
        assert("description 2" == mockList.get(1).getDescription());
        assert("description 3" == mockList.get(2).getDescription());
    }

    @Test
>>>>>>> bb81172e53d4faac12d48342b13faf4adf2566f3
    public void testDeleteSelectedItems() {
        ItemList itemList = mockItemList();

        itemList.get(1).setSelected(true);
        itemList.get(2).setSelected(true);

        assertEquals(3, itemList.getItemList().size());

        deleteSelectedItems(itemList);

        assertEquals(1, itemList.getItemList().size());
        assertEquals("Item1", itemList.get(0).getName());

    }
}

class ItemListTestObj extends ItemList {
    public ItemListTestObj(Context context, ListView itemListView, DatabaseManager database) {
        super(context, itemListView, database);
    }

    public void sortTest() {
        sort();
    }

    public void filterTest() {
        filter();
    }
}