package com.example.android.letspark.service;

import android.content.SharedPreferences;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of SharedPreference service.
 */
public class SharedPreferenceService implements Service.SharedPreferenceService {

    private SharedPreferences sharedPref;

    private SharedPreferences.Editor editor;

    private static final String PREF_UID = "UID";

    private static final String PREF_EMAIL = "EMAIL";

    @Inject
    public SharedPreferenceService(SharedPreferences sharedPref, SharedPreferences.Editor editor) {
        this.sharedPref = checkNotNull(sharedPref);
        this.editor = checkNotNull(editor);
    }

    @Override
    public void setCurrentUserUid(String uid) {
        editor.putString(PREF_UID, uid);
        editor.commit();
    }

    @Override
    public void setCurrentUserEmail(String email) {
        editor.putString(PREF_EMAIL, email);
        editor.commit();
    }

    @Override
    public void getCurrentUserUid(GetCurrentUserUidCallback callback) {
        String uid = sharedPref.getString(PREF_UID, "xxxxxx");
        callback.onGetUid(uid);
    }

    @Override
    public void getCurrentUserEmail(GetCurrentUserEmailCallback callback) {
        String email = sharedPref.getString(PREF_EMAIL, "xxxxxx");
        callback.onGetEmail(email);
    }
}
