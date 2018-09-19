package com.example.android.letspark.signinsignup;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.android.letspark.LetsParkApp;
import com.example.android.letspark.R;
import com.example.android.letspark.service.ConnectivityService;
import com.example.android.letspark.service.FirebaseAuthenticationService;
import com.example.android.letspark.utility.ActivityUtils;

import javax.inject.Inject;

public class SignInSignUpActivity extends AppCompatActivity {

    public static final int RC_SIGN_IN = 4;
    @Inject
    FirebaseAuthenticationService firebaseAuthenticationService;
    @Inject
    ConnectivityService connectivityService;
    private SignInSignUpFragment signInSignUpFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in_sign_up);

        signInSignUpFragment = (SignInSignUpFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);

        if (signInSignUpFragment == null) {
            // Create the fragment.
            signInSignUpFragment = SignInSignUpFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), signInSignUpFragment, R.id.contentFrame);
        }

        // Get the instance of LetsParkAppComponent to connect between our dependency provider
        // and dependency consumer.
        ((LetsParkApp) getApplication()).getLetsParkAppComponent()
                .inject(this);

        // TODO: Improve code by injecting dependency using Dagger 2
        // Create the presenter.
        new SignInSignUpPresenter(signInSignUpFragment, firebaseAuthenticationService,
                connectivityService);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            signInSignUpFragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }
}