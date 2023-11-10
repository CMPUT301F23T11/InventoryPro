package com.example.inventorypro;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
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
        UserPreferences.createInstance("DEBUG_TEST_USER");
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
    public void TestViewandEdit(){
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
        onView(withId(R.id.itemsListView));

        onView(withId(R.id.confirm_button)).perform(click());

        onData(anything()).inAdapterView(withId(R.id.itemsListView)).atPosition(0).perform(click());

        //TODO: Need to fix this, ie click Item
        /*onData(anything()).inAdapterView(allOf(withId(R.id.itemsListView), childAtPosition(
                                withClassName(is("androidx.constraintlayout.widget.ConstraintLayout")),
                                0))).atPosition(0).perform(click());*/


        onView(withText("Test Item")).check(matches(isDisplayed()));
        // onView(withId(R.id.viewItemName).matches("Test Item");


    }

}

