package com.example.android.letspark.dependencyinjection;

import com.example.android.letspark.data.EmptyParkingBaysRemoteDataSourceModule;
import com.example.android.letspark.letsparkparkingbays.EmptyParkingBaysActivity;
import com.example.android.letspark.service.LocationServiceModule;

import dagger.Component;


@Component(modules = {LocationServiceModule.class, EmptyParkingBaysRemoteDataSourceModule.class})
@LetsParkApplicationScope
public interface LetsParkComponent {
    void inject(EmptyParkingBaysActivity emptyParkingBaysActivity);
}
