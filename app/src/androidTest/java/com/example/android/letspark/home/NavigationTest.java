package com.example.android.letspark.home;

import com.example.android.letspark.R;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.ActivityTestRule;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

@RunWith(AndroidJUnit4.class)
@LargeTest
public class NavigationTest {

    @Rule
    public ActivityTestRule<HomeActivity> activityTestRule =
            new ActivityTestRule<>(HomeActivity.class);

    @Test
    public void clickOnSelectCar_ShowCarListScreen() {
        // Click "Select Car" text.
        onView(withId(R.id.text_select_car)).perform(click());

        // Check if "Car" screen is shown.
        onView(withText("Car")).check(matches(isDisplayed()));
    }

    @Test
    public void clickOnSelectDuration_ShowDurationAlertDialog() {
        // Click "Select Duration" text.
        onView(withId(R.id.text_select_duration)).perform(click());

        // Check if alert dialog screen with "Select Duration" is shown.
        onView(withText("Select Duration")).check(matches(isDisplayed()));
    }
}
