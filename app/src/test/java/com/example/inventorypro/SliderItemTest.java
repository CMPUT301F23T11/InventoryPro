package com.example.inventorypro;

import android.net.Uri;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class SliderItemTest {

    @Mock
    private Uri mockUri;

    @Test
    public void testGetImage() {
        // Mock behavior of the Uri
        String expectedUriString = "content://mock.uri/image";
        when(mockUri.toString()).thenReturn(expectedUriString);

        // Create a SliderItem with the mock Uri
        SliderItem sliderItem = new SliderItem(mockUri);

        // Get the image URI from the SliderItem
        String retrievedUriString = sliderItem.getImage().toString();

        // Verify that the retrieved URI matches the expected URI string
        assertEquals(expectedUriString, retrievedUriString);
    }
}
