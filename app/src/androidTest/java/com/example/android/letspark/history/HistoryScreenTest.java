package com.example.android.letspark.history;

import com.example.android.letspark.R;
import com.example.android.letspark.data.model.History;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.core.app.ActivityScenario;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;

import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.hasDescendant;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.instanceOf;

@RunWith(AndroidJUnit4.class)
@MediumTest
public class HistoryScreenTest {

    private IdlingResource idlingResource;

    /**
     * Use {@link ActivityScenario to launch and get access to the activity.
     * {@link ActivityScenario#onActivity(ActivityScenario.ActivityAction)} provides a thread-safe
     * mechanism to access the activity.
     */
    @Before
    public void registerIdlingResource() {
        ActivityScenario activityScenario = ActivityScenario.launch(HistoryActivity.class);
        activityScenario.onActivity(new ActivityScenario.ActivityAction<HistoryActivity>() {
            @Override
            public void perform(HistoryActivity activity) {
                idlingResource = activity.getIdlingResource();
                // To prove that the test fails, omit this call:
                IdlingRegistry.getInstance().register(idlingResource);
            }
        });
    }

    @Test
    public void loadUserHistory_listItemZero_carNumberPlateQWE1234AndOneHourDurationWithPaymentZeroPointFourFive() {
        // Check that the history is loaded.
        onData(instanceOf(History.class))
                .atPosition(0)
                .inAdapterView(withId(R.id.list_history))
                .check(matches(hasDescendant(withText("QWE1234"))))
                .check(matches(hasDescendant(withText("1"))))
                .check(matches(hasDescendant(withText("RM0.45"))));
    }

    @After
    public void unregisterIdlingResource() {
        if (idlingResource != null) {
            IdlingRegistry.getInstance().unregister(idlingResource);
        }
    }
}
