package com.example.android.letspark.service;

import android.annotation.SuppressLint;
import android.location.Location;
import android.support.annotation.NonNull;

import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of location service.
 */
public class LocationService implements Service.LocationService {

    private LocationRequest locationRequest;

    private Task<LocationSettingsResponse> task;

    private FusedLocationProviderClient fusedLocationProviderClient;

    private LocationCallback locationCallback;

    @Inject
    public LocationService(LocationRequest locationRequest,
                           FusedLocationProviderClient fusedLocationProviderClient,
                           Task<LocationSettingsResponse> task) {
        this.locationRequest = checkNotNull(locationRequest);
        this.fusedLocationProviderClient = checkNotNull(fusedLocationProviderClient);
        this.task = checkNotNull(task);
    }

    @Override
    public void getLocationSettingResponse(final GetLocationSettingResponseCallback callback) {
        task.addOnSuccessListener(new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                callback.onSatisfyLocationSetting(locationSettingsResponse);
            }
        });

        task.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    callback.onNotSatisfyLocationSetting(e);
                }
            }
        });
    }

    /**
     * Annotation @Suppresslint is used since location permission has been checked explicitly
     * using method askLocationPermission in EmptyParkingBaysPresenter.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void getLastKnownLocationResponse(final GetLastKnownLocationResponseCallback callback) {
        fusedLocationProviderClient.getLastLocation()
                .addOnSuccessListener(new OnSuccessListener<Location>() {
                    @Override
                    // Got last known location. In some rare situations this can be null.
                    // Learn more detail by visit below link
                    // https://developer.android.com/training/location/retrieve-current#last-known
                    public void onSuccess(Location location) {
                        if (location != null) {
                            String originLatLng = Double.toString(location.getLatitude()) + "," +
                                    Double.toString(location.getLongitude());
                            callback.onLastKnownLocationReceived(originLatLng);
                        } else {
                            callback.onLastKnowLocationIsNull();
                        }
                    }
                });
    }

    /**
     * Get the location update using the LocationCallback callback approach.
     */
    @Override
    public void newLocationCallback() {
        this.locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                // Intended to be blank.
            }
        };
    }

    /**
     * Annotation @Suppresslint is used since location permission has been checked explicitly
     * using method askLocationPermission in EmptyParkingBaysPresenter.
     * <p>
     * This method start location update.
     */
    @SuppressLint("MissingPermission")
    @Override
    public void requestLocationUpdates() {
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback,
                null /* Looper */);
    }

    /**
     * Stop location update.
     */
    @Override
    public void removeLocationUpdates() {
        fusedLocationProviderClient.removeLocationUpdates(locationCallback);
    }
}
