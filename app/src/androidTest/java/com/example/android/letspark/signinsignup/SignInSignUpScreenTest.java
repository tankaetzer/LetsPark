package com.example.android.letspark.signinsignup;

import android.content.Context;
import android.content.Intent;
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

import static junit.framework.Assert.fail;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsNull.notNullValue;
import static org.junit.Assert.assertEquals;

/**
 * Tests for the the sign in screen.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class SignInSignUpScreenTest {

    private static final String BASIC_SAMPLE_PACKAGE = "com.example.android.letspark";

    private static final int LAUNCH_TIMEOUT = 5000;

    private final static String EMAIL = "tkttan@hotmail.com";

    private final static String PASSWORD = "qwerty";

    @Rule
    public ActivityTestRule<SignInSignUpActivity> signInSignUpActivityTestRule =
            new ActivityTestRule<>(SignInSignUpActivity.class);

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

    // TODO: complete the test using Espresso Idling Resource
//    @Test
//    public void clickSignInButton_opensEmptyParkingBaysUi() {
//        // Enter email
//        onView(withId(R.id.email)).perform(typeText(EMAIL));
//
//        // Click NEXT button
//        onView(withId(R.id.button_next)).perform(click());
//
//        // Enter password
//        onView(withId(R.id.password)).perform(typeText(PASSWORD));
//
//        // Click SIGN IN button
//        onView(withId(R.id.button_done)).perform(click());
//
////        Verify empty parking bays screen is shown
////        onView(withId(R.id.textView)).check(matches(isDisplayed()));
//    }

    @Test
    public void noInternetConnection_showConnectivityErrMsg() throws UiObjectNotFoundException {
        // Opens the notification shade and find "Wi-Fi" button.
        device.openNotification();
        device.wait(Until.hasObject(By.descContains("Wi-Fi")), LAUNCH_TIMEOUT);

        UiObject wiFi = device.findObject(new UiSelector()
                .resourceId("com.android.systemui:id/qs_first_button"));

        UiObject mobileData = device.findObject(new UiSelector()
                .resourceId("com.android.systemui:id/qs_button_2"));

        UiObject enableConnectivityErrMsg = device.findObject(new UiSelector().resourceId("com." +
                "example.android.letspark:id/snackbar_text"));

        // Disable Wi-Fi if was enabled previously.
        if (wiFi.getContentDescription().equalsIgnoreCase("Wi-Fi is enabled.")) {
            wiFi.click();
        }

        // Disable mobile data if was enabled previously.
        if (mobileData.getContentDescription().equalsIgnoreCase("Mobile data is enabled.")) {
            mobileData.click();
        }

        // Close notification.
        device.pressBack();

        // Verify message is "Please enable Wi-Fi or Mobile data."
        assertEquals("Please enable Wi-Fi or Mobile data.",
                enableConnectivityErrMsg.getText());

        // Verify ok button is exist.
        UiObject button_ok = device.findObject(new UiSelector().resourceId("com." +
                "example.android.letspark:id/snackbar_action"));
        assertEquals("OK", button_ok.getText());
    }

    @Test
    public void pressBackButtonAtSignInScreen_showPhoneHomeScreen() {
        // Close soft keyboard.
        device.pressBack();

        // Press back button again to Android home screen.
        device.pressBack();

        // Verify Android home screen is shown by locating Apps button.
        UiObject button_apps = device.findObject(new UiSelector().text("Apps"));

        if (!button_apps.exists()) {
            fail("Apps button not exist");
        }
    }
}
