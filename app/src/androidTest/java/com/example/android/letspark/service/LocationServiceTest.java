package com.example.android.letspark.service;

import android.content.Context;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import static junit.framework.Assert.assertNotNull;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.fail;

/**
 * Instrumented unit tests for the implementation of LocationService.
 */
@MediumTest
@RunWith(AndroidJUnit4.class)
public class LocationServiceTest {

    private LocationRequest locationRequest;

    private SettingsClient settingsClient;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationSettingsRequest.Builder builder;

    private Task<LocationSettingsResponse> task;

    private LocationService locationService;

    @Before
    public void setup() {
        locationRequest = new LocationRequest()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        settingsClient = LocationServices.getSettingsClient(context);

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);

        builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        task = settingsClient.checkLocationSettings(builder.build());

        new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // Intended to be blank.
            }
        };

        locationService = new LocationService(locationRequest, fusedLocationProviderClient, task);
    }

    @Test
    public void getLocationSettingResponse() {
        locationService.getLocationSettingResponse(new Service
                .LocationService.GetLocationSettingResponseCallback() {
            @Override
            public void onSatisfyLocationSetting(LocationSettingsResponse locationSettingsResponse) {
//                assertNotNull(locationSettingsResponse);
                assertNull(locationSettingsResponse);
            }

            @Override
            public void onNotSatisfyLocationSetting(Exception e) {
                assertThat(e.getMessage(), is("6: RESOLUTION_REQUIRED"));
            }
        });
    }

    @Test
    public void getLastKnownLocationResponse() {
        locationService.getLastKnownLocationResponse(new Service.LocationService
                .GetLastKnownLocationResponseCallback() {
            @Override
            public void onLastKnownLocationReceived(String originLatLng) {
                assertNotNull(originLatLng);
            }

            @Override
            public void onLastKnowLocationIsNull() {
                fail("Location is Null");
            }
        });
    }
}
