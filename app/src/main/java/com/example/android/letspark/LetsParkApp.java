package com.example.android.letspark;

import android.app.Application;

import com.example.android.letspark.data.RemoteDataSourceModule;
import com.example.android.letspark.dependencyinjection.DaggerLetsParkAppComponent;
import com.example.android.letspark.dependencyinjection.LetsParkAppComponent;
import com.example.android.letspark.service.ConnectivityServiceModule;
import com.example.android.letspark.service.DistanceMatrixServiceModule;
import com.example.android.letspark.service.FirebaseAuthenticationModule;
import com.example.android.letspark.service.LocationServiceModule;
import com.example.android.letspark.service.SharedPreferenceModule;

import net.danlew.android.joda.JodaTimeAndroid;

/**
 * Create LetsParkAppComponent and define getLetsParkAppComponent so that it can be assessed in all
 * classes.
 */
public class LetsParkApp extends Application {

    private LetsParkAppComponent letsParkAppComponent;

    @Override
    public void onCreate() {
        super.onCreate();
        letsParkAppComponent = DaggerLetsParkAppComponent.builder()
                .locationServiceModule(new LocationServiceModule(getApplicationContext()))
                .distanceMatrixServiceModule(new DistanceMatrixServiceModule(getString(R.string.base_url)))
                .connectivityServiceModule(new ConnectivityServiceModule(getApplicationContext()))
                .firebaseAuthenticationModule(new FirebaseAuthenticationModule())
                .remoteDataSourceModule(new RemoteDataSourceModule())
                .sharedPreferenceModule(new SharedPreferenceModule(getApplicationContext()))
                .build();
        JodaTimeAndroid.init(this);
    }

    public LetsParkAppComponent getLetsParkAppComponent() {
        return letsParkAppComponent;
    }
}
