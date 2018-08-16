package com.example.android.letspark.letsparkparkingbays;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;

@RunWith(AndroidJUnit4.class)
public class EmptyParkingBaysViewTest {

    private static final String BASIC_SAMPLE_PACKAGE = "com.example.android.letspark";

    private static final int LAUNCH_TIMEOUT = 5000;

    @Rule
    public ActivityTestRule<EmptyParkingBaysActivity> activityRule
            = new ActivityTestRule<>(EmptyParkingBaysActivity.class);
    private UiDevice device;

    @Before
    public void setUp() {
        // Initialize UiDevice instance
        device = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        device.pressHome();

        // Wait for launcher
        final String launcherPackage = device.getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        device.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)),
                LAUNCH_TIMEOUT);

        // Launch the app
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);

        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT);
    }

    @Test
    public void verifyEmptyParkingBaysMarkerOnGoogleMap() throws UiObjectNotFoundException {
        // Find marker with title of 'KK3-1'
        UiObject marker = device.findObject(new UiSelector().descriptionContains("KK3-1"));

        marker.click();
    }

    @Test
    public void clickLocationPermissionAllowButtonThenDisplayAllowMessage() throws
            UiObjectNotFoundException {
        // Android Marshmallow 6.0 API level version is 23.
        if (Build.VERSION.SDK_INT >= 23) {
            UiObject allowBtn = device.findObject(new UiSelector().text("Allow"));

            allowBtn.click();

            // Verify the displayed message is "Location permission has been granted."
            UiObject result = device.findObject(
                    new UiSelector().text("Location permission has been granted."));
            assertEquals("Location permission has been granted.", result.getText());
        }
    }

    @Test
    public void clickLocationPermissionDenyButtonThenDisplayDenyMessage() throws
            UiObjectNotFoundException {
        // Android Marshmallow 6.0 API level version is 23.
        if (Build.VERSION.SDK_INT >= 23) {
            UiObject denyBtn = device.findObject(new UiSelector().text("Deny"));

            denyBtn.click();

            // Verify the displayed message is "Location permission was denied."
            UiObject result = device.findObject(
                    new UiSelector().text("Location permission was denied."));
            assertEquals("Location permission was denied.", result.getText());
        }
    }

    @Test
    public void showPreviouslyDenyMessageWithOkButton() throws UiObjectNotFoundException {
        // Android Marshmallow 6.0 API level version is 23.
        if (Build.VERSION.SDK_INT >= 23) {

            clickLocationPermissionDenyButtonThenDisplayDenyMessage();

            setUp();

            UiObject previouslyDenyMessage = device.findObject(new UiSelector().resourceId("com." +
                    "example.android.letspark:id/snackbar_text"));

            // Verify previously deny message is "Access to the location service is required to
            // show your current location on the map."
            assertEquals("Access to the location service is required to show your " +
                    "current location on the map.", previouslyDenyMessage.getText());

            // Verify ok button is exist.
            UiObject btnOk = device.findObject(new UiSelector().resourceId("com." +
                    "example.android.letspark:id/snackbar_action"));
            assertEquals("OK", btnOk.getText());
        }
    }

    @Test
    public void clickOkButtonOnPreviouslyShowDenyMessageToRequestPermission() throws
            UiObjectNotFoundException {
        // Android Marshmallow 6.0 API level version is 23.
        if (Build.VERSION.SDK_INT >= 23) {
            showPreviouslyDenyMessageWithOkButton();

            UiObject btnOk = device.findObject(new UiSelector().resourceId("com." +
                    "example.android.letspark:id/snackbar_action"));

            btnOk.click();

            // Verify the request permission dialog is "Allow LetsPark to access this device's
            // location?"
            UiObject result = device.findObject(
                    new UiSelector().text("Allow LetsPark to access this device's location?"));
            assertEquals("Allow LetsPark to access this device's location?", result.getText());
        }
    }

    @Test
    public void checkNeverAskAgainAndClickLocationPermissionDenyButtonThenDisplayDenyMessage()
            throws UiObjectNotFoundException {
        // Android Marshmallow 6.0 API level version is 23.
        if (Build.VERSION.SDK_INT >= 23) {

            clickOkButtonOnPreviouslyShowDenyMessageToRequestPermission();

            UiObject neverAskAgainChk = device.findObject(new UiSelector().
                    resourceId("com.android.packageinstaller:id/do_not_ask_checkbox"));

            neverAskAgainChk.click();

            UiObject denyBtn = device.findObject(new UiSelector().text("Deny"));

            denyBtn.click();

            // Verify deny message is "Go to Settings and enable the location permission to
            // maximum utilise Google Map features"
            UiObject result = device.findObject(
                    new UiSelector().resourceId("com.example.android.letspark:id/snackbar_text"));
            assertEquals("Go to Settings and enable the location permission to maximum " +
                    "utilise Google Map features", result.getText());
        }
    }
}
