package com.example.android.letspark.data;

import com.example.android.letspark.dependencyinjection.LetsParkAppScope;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import dagger.Module;
import dagger.Provides;


/**
 * This is a Dagger module. We use this to pass in the EmptyParkingBaysRemoteDataSource dependency
 * to the presenter.
 */
@Module
public class EmptyParkingBaysRemoteDataSourceModule {

    @Provides
    @LetsParkAppScope
    DatabaseReference provideDatabaseReference() {
        return FirebaseDatabase.getInstance().getReference();
    }
}
