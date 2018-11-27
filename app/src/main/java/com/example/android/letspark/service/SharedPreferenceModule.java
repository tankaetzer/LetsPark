package com.example.android.letspark.service;

import android.content.Context;
import android.content.SharedPreferences;

import com.example.android.letspark.R;
import com.example.android.letspark.dependencyinjection.LetsParkAppScope;

import javax.inject.Inject;

import dagger.Module;
import dagger.Provides;

/**
 * This is a Dagger module. This module class provide SharedPreferences object.
 */
@Module
public class SharedPreferenceModule {

    private Context context;

    public SharedPreferenceModule(Context context) {
        this.context = context;
    }

    @Provides
    @LetsParkAppScope
    SharedPreferences provideSharedPreferences() {
        return context.getSharedPreferences(context.getString(R.string.preference_file_key),
                Context.MODE_PRIVATE);
    }

    @Provides
    @Inject
    @LetsParkAppScope
    SharedPreferences.Editor provideEditor(SharedPreferences sharedPref) {
        return sharedPref.edit();
    }
}
