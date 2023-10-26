package com.example.inventorypro;

import java.math.BigDecimal;
import java.util.ArrayList;

public class ItemList {
    private ArrayList<Item> itemList = new ArrayList<Item>();

    // TODO - Sorting
    // TODO - Filtering
    // TODO - Find by barcode
    // TODO - Find by tag
    // TODO - Apply tags to items

    /**
     * Adds an item to the list of items and calls the database manager.
     * @param item The item to add.
     */
    public void add(Item item) {
        itemList.add(item);
        // TODO - Call database
    }

    /**
     * Removes an item from the list of items and calls the database manager.
     * @param item The item to remove.
     */
    public void remove(Item item) {
        itemList.remove(item);
        // TODO - Call database
    }

    /**
     * Gets the total value of all items in the list.
     * @return the total value of all items in the list.
     */
    public BigDecimal getTotalValue() {
        BigDecimal total = new BigDecimal("0");
        for (Item item : itemList) {
            // if value is not null then add to total
            if (item.getValue() != null) {
                total = total.add(item.getValue());
            }
        }
        return total;
    }
}