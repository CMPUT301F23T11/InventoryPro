package com.example.inventorypro;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.anything;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.assertion.ViewAssertions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import com.example.inventorypro.Activities.MainActivity;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;

public class ViewItemTester {
    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<>(MainActivity.class);

    @BeforeClass
    public static void setup() {
        User.createInstance("DEBUG_VIEW");
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }

    @Test
    public void TestViewItem() throws InterruptedException {
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

        //check values added are same as values viewed
        onView(withId(R.id.viewItemName))
                .check(ViewAssertions.matches(hasDescendant(withText("Test Item"))));
        onView(withId(R.id.viewValue))
                .check(ViewAssertions.matches(hasDescendant(withText("15.0"))));
        onView(withId(R.id.viewMake))
                .check(ViewAssertions.matches(hasDescendant(withText("Test make"))));
        onView(withId(R.id.viewModel))
                .check(ViewAssertions.matches(hasDescendant(withText("Test model"))));
        onView(withId(R.id.viewSerialNumber))
                .check(ViewAssertions.matches(hasDescendant(withText("#0000"))));
        onView(withId(R.id.viewDescription))
                .check(ViewAssertions.matches(hasDescendant(withText("Test description"))));
        onView(withId(R.id.viewComments))
                .check(ViewAssertions.matches(hasDescendant(withText("Test comments"))));



    }


}

