package com.example.android.letspark.letsparkparkingbays;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.support.test.InstrumentationRegistry;
import android.support.test.filters.MediumTest;
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

/**
 * Tests for the the main screen which contains markers of empty parking bays.
 */
@MediumTest
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
        Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);

        // Clear out any previous instances
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
        context.startActivity(intent);

        // Wait for the app to appear
        device.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)),
                LAUNCH_TIMEOUT);
    }

    /**
     * TODO: Fix Java.lang.RuntimeException: Could not launch intent Intent within 45 seconds.
     */
//      @Test
//    public void noInternetAccess_showConnectivityErrMsg() throws UiObjectNotFoundException {
//        // Android Marshmallow 6.0 API level version is 23.
//        if (Build.VERSION.SDK_INT >= 23) {
//            UiObject enableConnectivityMsg = device.findObject(new UiSelector().resourceId("com." +
//                    "example.android.letspark:id/snackbar_text"));
//
//            // Verify enableConnectivityMsg is "Please enable Wi-Fi or Mobile data."
//            assertEquals("Please enable Wi-Fi or Mobile data",
//                    enableConnectivityMsg.getText());
//
//            // Verify ok button is exist.
//            UiObject button_ok = device.findObject(new UiSelector().resourceId("com." +
//                    "example.android.letspark:id/snackbar_action"));
//            assertEquals("OK", button_ok.getText());
//        }
//    }
    @Test
    public void locationIsDisabled_askForLocationSettingDialog() throws UiObjectNotFoundException {
        // Opens the notification shade and find "Location" button.
        device.openNotification();
        device.wait(Until.hasObject(By.descContains("Location")), LAUNCH_TIMEOUT);
        UiObject location = device.findObject(new UiSelector().resourceId("com.android.systemui:id/qs_button_1"));

        if (location.getContentDescription().equals("Location is disabled.")) {
            // Close notification.
            device.pressBack();

            UiObject location_dialog = device.findObject(
                    new UiSelector().resourceId("com.google.android.gms:id/message"));
            assertEquals("For best results, turn on device location, which uses Google’s " +
                    "location service. ", location_dialog.getText());

            UiObject button_ok = device.findObject(new UiSelector().packageName("com.google.android.gms")
                    .resourceId("android:id/button1"));
            button_ok.click();
        }
    }

    @Test
    public void clickLocationPermissionAllowButton_showAllowMessage() throws
            UiObjectNotFoundException {
        // Android Marshmallow 6.0 API level version is 23.
        if (Build.VERSION.SDK_INT >= 23) {
            UiObject button_allow = device.findObject(new UiSelector().text("Allow"));

            button_allow.click();

            // Verify the displayed message is "Location permission has been granted."
            UiObject allowMessage = device.findObject(new UiSelector().resourceId("com." +
                    "example.android.letspark:id/snackbar_text"));
            assertEquals("Location permission has been granted.",
                    allowMessage.getText());
        }
    }

    @Test
    public void clickLocationPermissionDenyButton_showDenyMessage() throws
            UiObjectNotFoundException {
        // Android Marshmallow 6.0 API level version is 23.
        if (Build.VERSION.SDK_INT >= 23) {
            UiObject button_deny = device.findObject(new UiSelector().text("Deny"));

            button_deny.click();

            UiObject denyMessage = device.findObject(new UiSelector().resourceId("com." +
                    "example.android.letspark:id/snackbar_text"));
            assertEquals("Location permission was denied.", denyMessage.getText());
        }
    }

    @Test
    public void showPreviouslyDenyMessageWithOkButton() throws UiObjectNotFoundException {
        // Android Marshmallow 6.0 API level version is 23.
        if (Build.VERSION.SDK_INT >= 23) {
            clickLocationPermissionDenyButton_showDenyMessage();

            setUp();

            UiObject previouslyDenyMessage = device.findObject(new UiSelector().resourceId("com." +
                    "example.android.letspark:id/snackbar_text"));

            // Verify previously deny message is "Access to the location service is required to
            // show your current location on the map."
            assertEquals("Access to the location service is required to show your " +
                    "current location on the map.", previouslyDenyMessage.getText());

            // Verify ok button is exist.
            UiObject button_ok = device.findObject(new UiSelector().resourceId("com." +
                    "example.android.letspark:id/snackbar_action"));
            assertEquals("OK", button_ok.getText());
        }
    }

    @Test
    public void clickOkButtonOnPreviouslyShowDenyMessage_requestLocationPermission() throws
            UiObjectNotFoundException {
        // Android Marshmallow 6.0 API level version is 23.
        if (Build.VERSION.SDK_INT >= 23) {
            showPreviouslyDenyMessageWithOkButton();

            UiObject button_ok = device.findObject(new UiSelector().resourceId("com." +
                    "example.android.letspark:id/snackbar_action"));

            button_ok.click();

            // Verify the request permission dialog is "Allow LetsPark to access this device's
            // location?"
            UiObject result = device.findObject(
                    new UiSelector().text("Allow LetsPark to access this device's location?"));
            assertEquals("Allow LetsPark to access this device's location?",
                    result.getText());
        }
    }

    @Test
    public void checkNeverAskAgainAndClickLocationPermissionDenyButton_showDenyMessage()
            throws UiObjectNotFoundException {
        // Android Marshmallow 6.0 API level version is 23.
        if (Build.VERSION.SDK_INT >= 23) {
            clickOkButtonOnPreviouslyShowDenyMessage_requestLocationPermission();

            UiObject neverAskAgainChk = device.findObject(new UiSelector().
                    resourceId("com.android.packageinstaller:id/do_not_ask_checkbox"));

            neverAskAgainChk.click();

            UiObject button_deny = device.findObject(new UiSelector().text("Deny"));

            button_deny.click();

            // Verify deny message is "Go to Settings and enable the location permission to
            // maximum utilise Google Map features"
            UiObject result = device.findObject(
                    new UiSelector().resourceId("com.example.android.letspark:id/snackbar_text"));
            assertEquals("Go to Settings and enable the location permission to maximum " +
                    "utilise Google Map features", result.getText());
        }
    }

    @Test
    public void verifyEmptyParkingBaysMarkerOnGoogleMap() throws UiObjectNotFoundException {
        // Find marker with title of 'KK3-3'
        UiObject marker = device.findObject(new UiSelector().descriptionContains("KK3-3"));
        marker.click();
    }

    @Test
    public void clickMarker_verifyMarkerWithCorrectDetails()
            throws UiObjectNotFoundException {
        // Find marker with title of 'KK3-3'
        UiObject marker = device.findObject(new UiSelector().descriptionContains("KK3-3"));
        marker.click();

        // Expected distance may be vary depend on tester current location.
        UiObject distance = device.findObject(
                new UiSelector().resourceId("com.example.android.letspark:id/text_distance"));
        assertEquals("3.2 km", distance.getText());

        UiObject rate = device.findObject(
                new UiSelector().resourceId("com.example.android.letspark:id/text_rate"));
        assertEquals("RM0.89", rate.getText());

        // Expected duration may be vary depend on tester current location.
        UiObject duration = device.findObject(
                new UiSelector().resourceId("com.example.android.letspark:id/text_duration"));
        assertEquals("6 mins", duration.getText());
    }
}
