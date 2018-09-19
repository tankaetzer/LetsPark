package com.example.android.letspark.service;

import com.example.android.letspark.dependencyinjection.LetsParkAppScope;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. This module class provide FirebaseAuthenticationService object.
 */
@Module
public class FirebaseAuthenticationModule {

    @Provides
    @LetsParkAppScope
    FirebaseAuthenticationService provideFirebaseAuthenticationService() {
        return new FirebaseAuthenticationService();
    }
}
