package com.example.android.letspark.service;

import com.example.android.letspark.dependencyinjection.LetsParkAppScope;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. This module class provide CountDownTimerService object.
 */
@Module
public class CountDownTimerServiceModule {

    @Provides
    @LetsParkAppScope
    CountDownTimerService provideCountDownTimerService() {
        return new CountDownTimerService();
    }
}
