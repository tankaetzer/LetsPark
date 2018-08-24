package com.example.android.letspark.data;

import com.example.android.letspark.dependencyinjection.LetsParkApplicationScope;

import dagger.Module;
import dagger.Provides;


/**
 * This is a Dagger module. We use this to pass in the EmptyParkingBaysRemoteDataSource dependency
 * to the presenter.
 */
@Module
public class EmptyParkingBaysRemoteDataSourceModule {

    @Provides
    @LetsParkApplicationScope
    EmptyParkingBaysRemoteDataSource provideEmptyParkingBaysRemoteDataSource() {
        return new EmptyParkingBaysRemoteDataSource();
    }
}
