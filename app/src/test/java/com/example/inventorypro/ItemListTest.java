package com.example.inventorypro;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ItemListTest {
    private ItemList mockItemList() {
        ItemList itemList = new ItemList(null, null,null);
        Item item1 = new Item("Item1", 12.36, null, null, null, null, null, null,null);
        Item item2 = new Item("Item2", 8.5, null, null, null, null, null, null,null);
        Item item3 = new Item("Item3", 7.97, null, null, null, null, null, null,null);
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
