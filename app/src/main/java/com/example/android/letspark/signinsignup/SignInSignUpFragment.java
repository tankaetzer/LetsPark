package com.example.android.letspark.signinsignup;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.letspark.R;
import com.example.android.letspark.letsparkparkingbays.EmptyParkingBaysActivity;
import com.firebase.ui.auth.AuthUI;

import java.util.Arrays;
import java.util.List;

import static com.example.android.letspark.signinsignup.SignInSignUpActivity.RC_SIGN_IN;

public class SignInSignUpFragment extends Fragment implements SignInSignUpContract.View {

    private View root;

    private SignInSignUpContract.Presenter signInSignUpPresenter;

    public SignInSignUpFragment() {
        // Require empty constructor so it can be instantiated when restoring Activity's state.
    }

    public static SignInSignUpFragment newInstance() {
        return new SignInSignUpFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_sign_in_sign_up, container, false);
        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        signInSignUpPresenter.start();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        signInSignUpPresenter.result(requestCode, resultCode, data);
    }

    @Override
    public void setPresenter(SignInSignUpContract.Presenter presenter) {
        signInSignUpPresenter = presenter;
    }

    /**
     * Create Firebase Authentication UI.
     */
    public void createSignInIntent() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build());

        // Create and launch sign-in intent
        startActivityForResult(AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    @Override
    public void showConnectivityErrMsg() {
        Snackbar.make(root, R.string.authentication_enable_connectivity_error,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_ok,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        signInSignUpPresenter.checkConnectivity();
                    }
                }).show();
    }

    @Override
    public void closeActivity() {
        getActivity().finishAffinity();
    }

    @Override
    public void showEmptyParkingBaysUi() {
        Intent intent = new Intent(getContext(), EmptyParkingBaysActivity.class);
        startActivity(intent);
    }
}
