package com.example.android.letspark.service;

import android.content.Context;
import android.net.ConnectivityManager;

import com.example.android.letspark.dependencyinjection.LetsParkAppScope;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. This module class provide ConnectivityManager object.
 */
@Module
public class ConnectivityServiceModule {

    private Context context;

    public ConnectivityServiceModule(Context context) {
        this.context = context;
    }

    @Provides
    @LetsParkAppScope
    ConnectivityManager provideConnectivityManager() {
        return (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }
}
