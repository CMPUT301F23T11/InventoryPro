package com.example.inventorypro;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.action.ViewActions;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.inventorypro.Activities.MainActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.time.LocalDate;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @BeforeClass
    public static void setup(){
        UserPreferences.createInstance("DEBUG_TEST_USER");
    }

    private String safeString(String s){
        return s == null ? " " : s;
    }
    private void inputItem(Item i){
        onView(withId(R.id.itemInput)).perform(ViewActions.typeText(safeString(i.getName())));
        onView(withId(R.id.valueInput)).perform(ViewActions.typeText(String.format("%f", i.getValue())));
        onView(withId(R.id.dateInput)).perform(ViewActions.typeText(safeString(i.getLocalDate().toString())));
        onView(withId(R.id.makeInput)).perform(ViewActions.typeText(safeString(i.getMake())));
        onView(withId(R.id.modelInput)).perform(ViewActions.typeText(safeString(i.getModel())));
        onView(withId(R.id.serialNumberInput)).perform(ViewActions.typeText(safeString(i.getSerialNumber())));
        onView(withId(R.id.descriptionInput)).perform(ViewActions.typeText(safeString(i.getDescription())));
        onView(withId(R.id.commentsInput)).perform(ViewActions.typeText(safeString(i.getComment())));
    }

    @Test
    public void testAdd(){


        // Click on Add City button
        onView(withId(R.id.addButton)).perform(click());
        inputItem(new Item("Item3",
                7.97,
                LocalDate.of(2022, 10, 14),
                "make b",
                null,
                null,
                "description 1",
                null,
                null));
    }
}
