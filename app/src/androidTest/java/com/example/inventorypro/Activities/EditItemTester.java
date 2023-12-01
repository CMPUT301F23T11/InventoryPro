package com.example.inventorypro.Activities;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.Matchers.anything;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.inventorypro.R;
import com.example.inventorypro.User;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class EditItemTester {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        User.createInstance("EDITING", "DEBUG_TEST_USER_EMAIL_ID", "DEBUG_TEST_USER_DISPLAY_NAME");
    }

    /**
     * Test to check if item is editted correctly
     * @throws InterruptedException
     */
    @Test
    public void TestEdit() throws InterruptedException {
        // Click on Add Item button
        onView(withId(R.id.addButton)).perform(click());

        // Input Item values
        onView(withId(R.id.itemInput)).perform(ViewActions.replaceText("Test Item"));
        onView(withId(R.id.valueInput)).perform(ViewActions.replaceText("15.0"));
        onView(withId(R.id.makeInput)).perform(ViewActions.replaceText("Test make"));
        onView(withId(R.id.modelInput)).perform(ViewActions.replaceText("Test model"));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.replaceText("#0000"));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.replaceText("Test description"));
        onView(withId(R.id.commentsInput)).perform(ViewActions.replaceText("Test comments"));
        onView(withId(R.id.confirm_button)).perform(click());


        //click Item
        onData(anything()).inAdapterView(withId(R.id.itemsListView)).atPosition(0).perform(click());

        onView(withId(R.id.edit_button)).perform(click());

        //edit text
        onView(withId(R.id.itemInput)).perform(ViewActions.replaceText("Test  2 Item"));

        //Click Confirm Button
        onView(withId(R.id.confirm_button)).perform(click());







        onData(anything()).inAdapterView(withId(R.id.itemsListView)).atPosition(0)
                .onChildView(withId(R.id.checkbox)).perform(click());
        onView(withId(R.id.deleteButton)).perform(click());


    }
}
