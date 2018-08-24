package com.example.android.letspark.service;

import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

public class LocationService implements Service {

    private LocationRequest locationRequest;

    private LocationSettingsRequest.Builder builder;

    private Task<LocationSettingsResponse> task;

    private SettingsClient settingsClient;

    @Inject
    public LocationService(LocationRequest locationRequest, SettingsClient settingsClient) {
        this.locationRequest = checkNotNull(locationRequest);
        this.settingsClient = checkNotNull(settingsClient);
    }

    @Override
    public void createLocationRequest() {
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void setBuilder() {
        this.builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
    }

    @Override
    public void checkCurrentLocationSetting() {
        task = settingsClient.checkLocationSettings(builder.build());
    }

    @Override
    public void getLocationSettingResponse(final getLocationSettingResponseCallback callback) {
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                callback.onSatisfyLocationSetting();
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    callback.onNotSatisfyLocationSetting(e);
                }
            }
        });
    }
}
