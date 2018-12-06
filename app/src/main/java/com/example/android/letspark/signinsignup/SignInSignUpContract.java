package com.example.android.letspark.signinsignup;

import android.content.Intent;

import com.example.android.letspark.BasePresenter;
import com.example.android.letspark.BaseView;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface SignInSignUpContract {

    interface View extends BaseView<Presenter> {
        void createSignInIntent();

        void showConnectivityErrMsg();

        void closeActivity();

        void showHomeUi();
    }

    interface Presenter extends BasePresenter {
        void result(int requestCode, int resultCode, Intent data);

        void checkConnectivity();
    }
}
