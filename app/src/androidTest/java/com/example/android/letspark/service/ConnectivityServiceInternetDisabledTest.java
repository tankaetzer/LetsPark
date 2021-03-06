package com.example.android.letspark.service;

import android.content.Context;
import android.net.ConnectivityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertFalse;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Instrumented unit tests for the implementation of ConnectivityService.
 * <p>
 * Please disable WiFi and Mobile data before running this test.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class ConnectivityServiceInternetDisabledTest {

    private ConnectivityService connectivityService;

    @Before
    public void setup() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) InstrumentationRegistry
                .getInstrumentation()
                .getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        connectivityService = new ConnectivityService(connectivityManager);
    }

    @Test
    public void isConnected_noInternet_returnFalse() {
        boolean isConnected = connectivityService.isConnected();

        // Expected result is false since internet unavailable.
        assertFalse(isConnected);
    }

    @Test
    public void getConnectivityStatusResponse_noInternet_firesOnInternetUnavailable() {
        Service.ConnectivityService.GetConnectivityStatusResponseCallback callback = mock(Service.
                ConnectivityService.GetConnectivityStatusResponseCallback.class);

        connectivityService.getConnectivityStatusResponse(callback);

        // Check whether onInternetUnavailable is called and onInternetAvailableReceived is
        // never called.
        verify(callback).onInternetUnavailable();
        verify(callback, never()).onInternetAvailableReceived();
    }
}
