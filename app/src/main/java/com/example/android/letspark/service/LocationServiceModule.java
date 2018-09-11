package com.example.android.letspark.service;

import android.content.Context;

import com.example.android.letspark.dependencyinjection.LetsParkAppScope;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. We use this to pass in the LocationRequest and SettingsClient dependency
 * to the LocationService.
 */
@Module
public class LocationServiceModule {

    private Context context;

    public LocationServiceModule(Context context) {
        this.context = context;
    }

    @Provides
    @LetsParkAppScope
    LocationRequest provideLocationRequest() {
        return new LocationRequest()
                .setInterval(10000)
                .setFastestInterval(5000)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

    }

    @Provides
    @LetsParkAppScope
    SettingsClient provideSettingsClient() {
        return LocationServices.getSettingsClient(context);
    }

    @Provides
    @LetsParkAppScope
    FusedLocationProviderClient provideFusedLocationProviderClient() {
        return LocationServices.getFusedLocationProviderClient(context);
    }

    @Provides
    @Inject
    @LetsParkAppScope
    LocationSettingsRequest.Builder provideBuilder(LocationRequest locationRequest) {
        return new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);
    }

    @Provides
    @Inject
    @LetsParkAppScope
    Task<LocationSettingsResponse> provideTask(SettingsClient settingsClient,
                                               LocationSettingsRequest.Builder builder) {
        return settingsClient.checkLocationSettings(builder.build());
    }
}
