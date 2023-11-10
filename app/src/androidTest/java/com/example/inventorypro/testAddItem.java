package com.example.inventorypro;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.inventorypro.Activities.MainActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class testAddItem {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        UserPreferences.createInstance("DEBUG_TEST_USER");
    }

    private String safeString(String s) {
        return s == null ? " " : s;
    }

    @Test
    public void inputValidation() {
        // Click on Add Item button
        onView(withId(R.id.addButton)).perform(click());

        //Click on confirm without required info(ie. name and value)
        onView(withId(R.id.confirm_button)).perform(click());

        Espresso.onView(withText("This field is required!"))
                .check(matches(ViewMatchers.isDisplayed()));

        // Click on Cancel Item button
        onView(withId(R.id.cancel_button)).perform(click());



    }


    @Test
    public void testaddedItem(){
        // Click on Add Item button
        onView(withId(R.id.addButton)).perform(click());

        // Input Item values
        onView(withId(R.id.itemInput)).perform(ViewActions.replaceText("Test Item"));
        onView(withId(R.id.valueInput)).perform(ViewActions.replaceText("15.0"));
        onView(withId(R.id.makeInput)).perform(ViewActions.replaceText("Test make"));
        onView(withId(R.id.modelInput)).perform(ViewActions.replaceText("Test model"));

        //Click Confirm Button
        onView(withId(R.id.confirm_button)).perform(click());

        // Check if the ListView contains the item with text "Test Item"
        onView(withId(R.id.itemsListView))
                .check(matches(hasDescendant(withText("Test Item"))));




    }


}

