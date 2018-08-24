package com.example.android.letspark;

import android.app.Application;

import com.example.android.letspark.dependencyinjection.DaggerLetsParkComponent;
import com.example.android.letspark.dependencyinjection.LetsParkComponent;
import com.example.android.letspark.service.LocationServiceModule;

/**
 * Create LetsParkComponent and define getLetsParkComponent so that it can be assessed in all
 * classes.
 */
public class LetsParkApplication extends Application {

    private LetsParkComponent letsParkComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        letsParkComponent = DaggerLetsParkComponent.builder()
                .locationServiceModule(new LocationServiceModule(getApplicationContext()))
                .build();
    }

    public LetsParkComponent getLetsParkComponent() {
        return letsParkComponent;
    }
}
