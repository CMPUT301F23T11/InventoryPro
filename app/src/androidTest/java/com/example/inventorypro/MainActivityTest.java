package com.example.inventorypro;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import static org.hamcrest.CoreMatchers.allOf;
import static org.hamcrest.CoreMatchers.is;

import androidx.test.espresso.intent.Intents;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import com.example.inventorypro.Activities.AddItemActivity;
import com.example.inventorypro.Activities.MainActivity;
import com.example.inventorypro.Activities.SignInActivity;

import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {

    @Rule
    public ActivityScenarioRule<MainActivity> scenario = new
            ActivityScenarioRule<MainActivity>(MainActivity.class);

    @BeforeClass
    public static void setup(){
        User.createInstance("DEBUG_TEST_USER");
    }

    @Test
    public void switchActivitiesTest(){
        Intents.init();
        onView(withId(R.id.addButton)).perform(click());
        intended(hasComponent(AddItemActivity.class.getName()));
        Intents.release();

        onView(withId(R.id.cancel_button)).perform(click());

        Intents.init();
        onView(withId(R.id.profileButton)).perform(click());
        intended(hasComponent(SignInActivity.class.getName()));
        Intents.release();
    }
}
