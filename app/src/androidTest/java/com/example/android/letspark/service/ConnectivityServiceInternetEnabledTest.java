package com.example.android.letspark.service;

import android.content.Context;
import android.net.ConnectivityManager;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Instrumented unit tests for the implementation of ConnectivityService.
 * <p>
 * Please enable WiFi or Mobile data before running this test.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class ConnectivityServiceInternetEnabledTest {

    private com.example.android.letspark.service.ConnectivityService connectivityService;

    @Before
    public void setup() {

        ConnectivityManager connectivityManager
                = (ConnectivityManager) InstrumentationRegistry
                .getInstrumentation()
                .getContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);

        connectivityService = new com.example.android.letspark.service
                .ConnectivityService(connectivityManager);
    }

    @Test
    public void isConnected_internetConnected_returnTrue() {
        boolean isConnected = connectivityService.isConnected();

        // Expected result is true since internet available.
        assertTrue(isConnected);
    }

    @Test
    public void getConnectivityStatusResponse() {

        Service.ConnectivityService.GetConnectivityStatusResponseCallback callback = mock(Service.
                ConnectivityService.GetConnectivityStatusResponseCallback.class);

        connectivityService.getConnectivityStatusResponse(callback);

        // Check whether onInternetAvailableReceived is called and onInternetUnavailable is
        // never called.
        verify(callback).onInternetAvailableReceived();
        verify(callback, never()).onInternetUnavailable();
    }
}
