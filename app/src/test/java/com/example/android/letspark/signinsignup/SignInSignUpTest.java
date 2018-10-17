package com.example.android.letspark.signinsignup;

import android.test.suitebuilder.annotation.SmallTest;

import com.example.android.letspark.data.EmptyParkingBaysRemoteDataSource;
import com.example.android.letspark.service.ConnectivityService;
import com.example.android.letspark.service.FirebaseAuthenticationService;
import com.example.android.letspark.service.Service;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;

@SmallTest
public class SignInSignUpTest {

    @Mock
    private ConnectivityService connectivityService;

    @Mock
    private SignInSignUpContract.View signInSignUpView;

    @Mock
    private FirebaseAuthenticationService firebaseAuthenticationService;

    @Mock
    private EmptyParkingBaysRemoteDataSource emptyParkingBaysRemoteDataSource;

    @Captor
    private ArgumentCaptor<Service.ConnectivityService.GetConnectivityStatusResponseCallback>
            GetConnectivityStatusResponseCallbackArgumentCaptor;

    private SignInSignUpPresenter signInSignUpPresenter;

    @Before
    public void setup() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        signInSignUpPresenter = new SignInSignUpPresenter(signInSignUpView,
                firebaseAuthenticationService, connectivityService, emptyParkingBaysRemoteDataSource);
    }

    @Test
    public void createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test.
        signInSignUpPresenter = new SignInSignUpPresenter(signInSignUpView,
                firebaseAuthenticationService, connectivityService, emptyParkingBaysRemoteDataSource);

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
}
