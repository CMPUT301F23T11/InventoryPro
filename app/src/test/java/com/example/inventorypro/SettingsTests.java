package com.example.inventorypro;

import static org.junit.Assert.assertEquals;

import com.example.inventorypro.Fragments.SortFragment;

import org.junit.Test;

import java.time.LocalDate;

public class SettingsTests {



    @Test
    public void testDefaultBehaviour() {
        // Ensure default behaviour is as expected
        SortSettings ss = new SortSettings();
        assertEquals(true, ss.getSortOrder()== SortFragment.SortOrder.ASCENDING);
        assertEquals(true, ss.getSortType()== SortFragment.SortType.NONE);

        Item item1 = new Item("Item1",
                12.36,
                LocalDate.of(2023, 10, 11),
                "make a",
                null,
                null,
                "description 2",
                null,
                null,
                null,null);

        // Ensure an empty filter accepts the generic item
        FilterSettings fs = new FilterSettings();
        assertEquals(true, fs.itemSatisfiesFilter(item1));
    }
}
