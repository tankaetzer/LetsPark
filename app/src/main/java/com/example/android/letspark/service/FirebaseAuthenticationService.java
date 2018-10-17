package com.example.android.letspark.service;

import android.content.Intent;

import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.app.Activity.RESULT_OK;

/**
 * Implementation of Firebase authentication service.
 */
public class FirebaseAuthenticationService implements Service.FirebaseAuthenticationService {

    private String email;

    private String uid;

    @Override
    public void getCurrentUserResponse(int resultCode, Intent data,
                                       GetCurrentUserResponseCallback callback) {
        IdpResponse response = IdpResponse.fromResultIntent(data);

        if (resultCode == RESULT_OK) {
            // Successfully signed in.
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            email = user.getEmail();
            uid = user.getUid();
            callback.onResultOk(email, uid);
        } else {
            // Sign in failed due to no internet connection.
            if (response != null && response.getError().getMessage()
                    .equals("No internet connection")) {
                callback.onNoInternet();
            } else {
                // User canceled the sign-in flow using the back button.
                callback.onClickBackButton();
            }
        }
    }
}
