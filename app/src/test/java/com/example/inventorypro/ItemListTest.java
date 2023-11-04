package com.example.inventorypro;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import java.math.BigDecimal;

public class ItemListTest {
    private ItemList mockItemList() {
        ItemList itemList = new ItemList(null, null,null,new SortSettings(),new FilterSettings());
        Item item1 = new Item("Item1", 12.36, null, null, null, null, null, null);
        Item item2 = new Item("Item2", 8.5, null, null, null, null, null, null);
        Item item3 = new Item("Item3", 7.97, null, null, null, null, null, null);
        itemList.add(item1);
        itemList.add(item2);
        itemList.add(item3);
        return itemList;
    }
    @Test
    public void testTotal() {
            ItemList mockList = mockItemList();
            assertEquals(true, mockList.getTotalValue()==28.83);
    }
}
