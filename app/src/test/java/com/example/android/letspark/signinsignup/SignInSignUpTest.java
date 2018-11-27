package com.example.android.letspark.signinsignup;

import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.service.ConnectivityService;
import com.example.android.letspark.service.FirebaseAuthenticationService;
import com.example.android.letspark.service.Service;
import com.example.android.letspark.service.SharedPreferenceService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of SignInSignUpPresenter.
 */
public class SignInSignUpTest {

    @Mock
    private ConnectivityService connectivityService;

    @Mock
    private SignInSignUpContract.View signInSignUpView;

    @Mock
    private FirebaseAuthenticationService firebaseAuthenticationService;

    @Mock
    private RemoteDataSource remoteDataSource;

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    @Captor
    private ArgumentCaptor<Service.ConnectivityService.GetConnectivityStatusResponseCallback>
            GetConnectivityStatusResponseCallbackArgumentCaptor;

    private SignInSignUpPresenter signInSignUpPresenter;

    @Before
    public void setup() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        signInSignUpPresenter = new SignInSignUpPresenter(signInSignUpView,
                firebaseAuthenticationService, connectivityService, remoteDataSource,
                sharedPreferenceService);
    }

    @Test
    public void createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test.
        signInSignUpPresenter = new SignInSignUpPresenter(signInSignUpView,
                firebaseAuthenticationService, connectivityService, remoteDataSource,
                sharedPreferenceService);

        // Then the presenter is set to the view.
        verify(signInSignUpView).setPresenter(signInSignUpPresenter);
    }

    @Test
    public void checkConnectivity_onInternetAvailableReceived_firesCreateSignInIntent() {
        signInSignUpPresenter.checkConnectivity();

        // Callback is captured.
        verify(connectivityService)
                .getConnectivityStatusResponse(GetConnectivityStatusResponseCallbackArgumentCaptor
                        .capture());
        GetConnectivityStatusResponseCallbackArgumentCaptor.getValue().onInternetAvailableReceived();

        // Check whether createSignInIntent is called.
        verify(signInSignUpView).createSignInIntent();
    }

    @Test
    public void checkConnectivity_onInternetUnavailable_firesShowConnectivityErrMsg() {
        signInSignUpPresenter.checkConnectivity();

        // Callback is captured.
        verify(connectivityService)
                .getConnectivityStatusResponse(GetConnectivityStatusResponseCallbackArgumentCaptor
                        .capture());
        GetConnectivityStatusResponseCallbackArgumentCaptor.getValue().onInternetUnavailable();

        // Check whether showConnectivityErrMsg is called.
        verify(signInSignUpView).showConnectivityErrMsg();
    }

    @Test
    public void start() {
        signInSignUpPresenter.start();

        verify(signInSignUpView).createSignInIntent();
    }




}
