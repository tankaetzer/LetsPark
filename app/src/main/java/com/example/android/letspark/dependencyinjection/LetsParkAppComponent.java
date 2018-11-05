package com.example.android.letspark.dependencyinjection;

import com.example.android.letspark.addremovecar.AddRemoveCarActivity;
import com.example.android.letspark.data.RemoteDataSourceModule;
import com.example.android.letspark.home.HomeActivity;
import com.example.android.letspark.service.ConnectivityServiceModule;
import com.example.android.letspark.service.DistanceMatrixServiceModule;
import com.example.android.letspark.service.FirebaseAuthenticationModule;
import com.example.android.letspark.service.LocationServiceModule;
import com.example.android.letspark.signinsignup.SignInSignUpActivity;

import dagger.Component;

/**
 * Determine all the modules that has to be used and in which classes these dependency injection
 * should work.
 */
@Component(modules = {LocationServiceModule.class, RemoteDataSourceModule.class,
        DistanceMatrixServiceModule.class, ConnectivityServiceModule.class,
        FirebaseAuthenticationModule.class})
@LetsParkAppScope
public interface LetsParkAppComponent {
    void inject(HomeActivity homeActivity);

    void inject(SignInSignUpActivity signInSignUpActivity);

    void inject(AddRemoveCarActivity addRemoveCarActivity);
}
