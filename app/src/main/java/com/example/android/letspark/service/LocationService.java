package com.example.android.letspark.service;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

public class LocationService implements Service {

    private LocationRequest locationRequest;

    private LocationSettingsRequest.Builder builder;

    private Task<LocationSettingsResponse> task;

    private Activity activity;

    private Context context;

    /**
     * Prevent direct instantiation.
     */
    private LocationService(Activity activity, Context context) {
        this.activity = activity;
        this.context = context;
    }

    public static LocationService getInstance(Activity activity, Context context) {
        return new LocationService(activity, context);
    }

    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    public void setBuilder() {
        this.builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
    }

    public void checkCurrentLocationSetting() {
        SettingsClient client = LocationServices.getSettingsClient(activity);
        task = client.checkLocationSettings(builder.build());
    }

    @Override
    public void getLocationSettingResponse(final getLocationSettingResponseCallback callback) {
        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                callback.onSatisfyLocationSetting();
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
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
