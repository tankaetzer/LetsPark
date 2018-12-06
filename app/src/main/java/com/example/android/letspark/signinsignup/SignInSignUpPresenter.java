package com.example.android.letspark.signinsignup;

import android.content.Intent;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.service.Service;

import static com.example.android.letspark.signinsignup.SignInSignUpActivity.RC_SIGN_IN;
import static com.google.common.base.Preconditions.checkNotNull;

public class SignInSignUpPresenter implements SignInSignUpContract.Presenter {

    private SignInSignUpContract.View signInSignUpView;

    private Service.FirebaseAuthenticationService firebaseAuthenticationService;

    private Service.ConnectivityService connectivityService;

    private Service.SharedPreferenceService sharedPreferenceService;

    private DataSource dataSource;

    public SignInSignUpPresenter(SignInSignUpContract.View signInSignUpView,
                                 Service.FirebaseAuthenticationService firebaseAuthenticationService,
                                 Service.ConnectivityService connectivityService,
                                 DataSource dataSource,
                                 Service.SharedPreferenceService sharedPreferenceService) {
        this.signInSignUpView = checkNotNull(signInSignUpView);
        this.firebaseAuthenticationService = checkNotNull(firebaseAuthenticationService);
        this.connectivityService = checkNotNull(connectivityService);
        this.dataSource = checkNotNull(dataSource);
        this.sharedPreferenceService = checkNotNull(sharedPreferenceService);
        signInSignUpView.setPresenter(this);
    }

    @Override
    public void start() {
        signInSignUpView.createSignInIntent();
    }

    @Override
    public void result(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            firebaseAuthenticationService.getCurrentUserResponse(resultCode, data,
                    new Service.FirebaseAuthenticationService.GetCurrentUserResponseCallback() {
                        @Override
                        public void onResultOk(String email, String uid) {
                            signInSignUpView.showHomeUi();
                            dataSource.writeNewUser(uid, email);
                            sharedPreferenceService.setCurrentUserUid(uid);
                            sharedPreferenceService.setCurrentUserEmail(email);
                        }

                        @Override
                        public void onNoInternet() {
                            signInSignUpView.showConnectivityErrMsg();
                        }

                        @Override
                        public void onClickBackButton() {
                            signInSignUpView.closeActivity();
                        }
                    });
        }
    }

    @Override
    public void checkConnectivity() {
        connectivityService.getConnectivityStatusResponse(new Service.ConnectivityService
                .GetConnectivityStatusResponseCallback() {
            @Override
            public void onInternetAvailableReceived() {
                signInSignUpView.createSignInIntent();
            }

            @Override
            public void onInternetUnavailable() {
                signInSignUpView.showConnectivityErrMsg();
            }
        });
    }
}
