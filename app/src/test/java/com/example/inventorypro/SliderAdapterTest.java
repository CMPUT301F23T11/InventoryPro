package com.example.inventorypro;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;

public class SliderAdapterTest {

    @Test
    public void getItemCount_shouldReturnItemCount() {
        // Create a list of SliderItems to use in the test
        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(mock(SliderItem.class));
        sliderItems.add(mock(SliderItem.class));
        // Add more items as needed

        // Create a SliderAdapter instance
        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, null, true); // Pass null for ViewPager2

        // Test getItemCount() method
        int expectedItemCount = sliderItems.size();
        int actualItemCount = sliderAdapter.getItemCount();
        assertEquals("Item count should match", expectedItemCount, actualItemCount);
    }

    @Test
    public void convertUrisToStringArray_shouldConvertUrisToArray() {
        // Create a list of SliderItems to use in the test
        List<SliderItem> sliderItems = new ArrayList<>();
        sliderItems.add(mock(SliderItem.class));
        sliderItems.add(mock(SliderItem.class));
        // Add more items as needed

        // Create a SliderAdapter instance
        SliderAdapter sliderAdapter = new SliderAdapter(sliderItems, null, true); // Pass null for ViewPager2

        String[] expectedArray = new String[2]; // Array of size 2 to hold null elements
        expectedArray[0] = null;
        expectedArray[1] = null;
        String[] actualArray = sliderAdapter.convertUrisToStringArray();
        assertArrayEquals("Arrays should be equal", expectedArray, actualArray);
    }

}
