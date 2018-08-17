package com.example.android.letspark.letsparkparkingbays;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.letspark.data.EmptyParkingBay;
import com.example.android.letspark.data.EmptyParkingBaysDataSource;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.List;

import static com.example.android.letspark.letsparkparkingbays.EmptyParkingBaysActivity.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.android.letspark.letsparkparkingbays.EmptyParkingBaysActivity.REQUEST_CHECK_SETTINGS;

/**
 * Listens to user actions from the UI (EmptyParkingBaysFragment), retrieves the data and updates
 * the UI as required.
 */
public class EmptyParkingBaysPresenter implements EmptyParkingBaysContract.Presenter {

    private EmptyParkingBaysDataSource emptyParkingBaysRemoteDataSource;

    private EmptyParkingBaysContract.View emptyParkingBaysView;

    private LocationRequest locationRequest;

    public EmptyParkingBaysPresenter(EmptyParkingBaysDataSource emptyParkingBaysRemoteDataSource,
                                     EmptyParkingBaysContract.View emptyParkingBaysView) {
        this.emptyParkingBaysRemoteDataSource = emptyParkingBaysRemoteDataSource;
        this.emptyParkingBaysView = emptyParkingBaysView;

        emptyParkingBaysView.setPresenter(this);
    }

    @Override
    public void loadEmptyParkingBays() {
        emptyParkingBaysRemoteDataSource.getEmptyParkingBays(new EmptyParkingBaysDataSource.
                LoadEmptyParkingBaysCallBack() {
            @Override
            public void onEmptyParkingBaysLoaded(List<EmptyParkingBay> emptyParkingBayList) {
                emptyParkingBaysView.showEmptyParkingBays(emptyParkingBayList);
            }

            @Override
            public void onDataNotAvailable() {
                emptyParkingBaysView.showLoadingEmptyParkingBaysError();
            }
        });
    }

    @Override
    public void start() {
        loadEmptyParkingBays();
    }

    @Override
    public void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    @Override
    public void askChangeLocationSetting(final Activity activity, final Context context) {
        createLocationRequest();

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);

        // Check whether current location settings are satisfied.
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());

        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                askLocationPermission(activity, context);
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                }
            }
        });
    }

    @Override
    public void askLocationPermission(Activity activity, Context context) {
        // Check if the access fine location permission has been granted.
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            // True if permission is not granted since user has previously denied the request.
            if (ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                emptyParkingBaysView.showErrorMessageWithAction();
            } else {
                // Request the permission
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            // Permission has already been granted.
        }
    }
}
