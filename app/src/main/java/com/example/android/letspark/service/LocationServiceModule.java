package com.example.android.letspark.service;

import android.content.Context;

import com.example.android.letspark.dependencyinjection.LetsParkApplicationScope;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.SettingsClient;

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
    @LetsParkApplicationScope
    LocationRequest provideLocationRequest() {
        return new LocationRequest();
    }

    @Provides
    @LetsParkApplicationScope
    SettingsClient provideSettingsClient() {
        return LocationServices.getSettingsClient(context);
    }
}
