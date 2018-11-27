package com.example.android.letspark.service;

import android.content.Context;
import android.content.SharedPreferences;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.MediumTest;
import androidx.test.platform.app.InstrumentationRegistry;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

@MediumTest
@RunWith(AndroidJUnit4.class)
public class SharedPreferenceServiceTest {

    private SharedPreferenceService sharedPreferenceService;

    @Before
    public void setup() {

        String preference_file_key = "com.example.android.letspark.PREFERENCE_FILE_KEY";

        Context context = InstrumentationRegistry.getInstrumentation().getContext();

        SharedPreferences sharedPreferences
                = context.getSharedPreferences(preference_file_key, Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPreferences.edit();


        sharedPreferenceService = new SharedPreferenceService(sharedPreferences, editor);
    }

    @Test
    public void getCurrentUserUid_uid_fireOnGetUid() {
        Service.SharedPreferenceService.GetCurrentUserUidCallback callback
                = mock(Service.SharedPreferenceService.GetCurrentUserUidCallback.class);

        String uid = "xxxxxx";

        sharedPreferenceService.getCurrentUserUid(callback);

        verify(callback).onGetUid(uid);
    }

}
