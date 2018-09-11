package com.example.android.letspark.dependencyinjection;

import com.example.android.letspark.data.EmptyParkingBaysRemoteDataSourceModule;
import com.example.android.letspark.letsparkparkingbays.EmptyParkingBaysActivity;
import com.example.android.letspark.service.ConnectivityServiceModule;
import com.example.android.letspark.service.DistanceMatrixServiceModule;
import com.example.android.letspark.service.LocationServiceModule;

import dagger.Component;

/**
 * Determine all the modules that has to be used and in which classes these dependency injection
 * should work.
 */
@Component(modules = {LocationServiceModule.class, EmptyParkingBaysRemoteDataSourceModule.class,
        DistanceMatrixServiceModule.class, ConnectivityServiceModule.class})
@LetsParkAppScope
public interface LetsParkAppComponent {
    void inject(EmptyParkingBaysActivity emptyParkingBaysActivity);
}
