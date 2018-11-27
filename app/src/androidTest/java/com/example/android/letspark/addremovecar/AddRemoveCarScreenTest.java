package com.example.android.letspark.addremovecar;

import com.example.android.letspark.R;
import com.example.android.letspark.data.model.Car;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withEffectiveVisibility;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class AddRemoveCarScreenTest {

    private IdlingResource idlingResource;

    /**
     * Use {@link ActivityScenario to launch and get access to the activity.
     * {@link ActivityScenario#onActivity(ActivityScenario.ActivityAction)} provides a thread-safe
     * mechanism to access the activity.
     */
    @Before
    public void registerIdlingResource() {
        ActivityScenario activityScenario = ActivityScenario.launch(AddRemoveCarActivity.class);
        activityScenario.onActivity(new ActivityScenario.ActivityAction<AddRemoveCarActivity>() {
            @Override
            public void perform(AddRemoveCarActivity activity) {
                idlingResource = activity.getIdlingResource();
                // To prove that the test fails, omit this call:
                IdlingRegistry.getInstance().register(idlingResource);
            }
        });
    }

    @Test
    public void loadUserCar_emptyCarList_showNoCarAddedYetInText() {
        // Check if "No car added yet" TextView is shown.
        onView(withId(R.id.text_no_vehicle_added_yet))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    //TODO: fix unable to check displayed message since SnackBar not in hierarchy
    @Test
    public void addCar_nonExistCarInUserAccount_showNewAddedCarInListAndDisplaySavedCarMessage() {
        String carNumberPlate = "QWE1234";

        // CLick "Add" floating action button.
        onView(withId(R.id.fab_add_car_plate_number)).perform(click());

        // Fill in alert dialog with car number plate.
        onView(withId(R.id.text_input_car_number_plate)).perform(typeText(carNumberPlate));

        // Click "Save" button.
        onView(withText("SAVE")).perform(click());

        // Check "QWE1234" is shown on screen.
        onData(instanceOf(Car.class))
                .inAdapterView(withId(R.id.list_cars))
                .check(matches(hasDescendant(withText(carNumberPlate))));
    }

    //TODO: fix unable to check displayed message since SnackBar not in hierarchy
    @Test
    public void deleteCar_carNumberPlateQWE1234_showNoCarAddedYetInText() {
        // CLick dustbin button.
        onData(instanceOf(Car.class))
                .inAdapterView(withId(R.id.list_cars))
                .atPosition(0)
                .onChildView(withId(R.id.image_delete))
                .perform(click());

        // Check if "No car added yet" TextView is shown.
        onView(withId(R.id.text_no_vehicle_added_yet))
                .check(matches(withEffectiveVisibility(ViewMatchers.Visibility.VISIBLE)));
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
