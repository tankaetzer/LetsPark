package com.example.android.letspark.service;

import android.content.Intent;

import com.google.android.gms.location.LocationSettingsResponse;

/**
 * Interface for location service, distance matrix service, connectivity service and
 * Firebase authentication service.
 */
public interface Service {

    interface LocationService {
        void getLocationSettingResponse(GetLocationSettingResponseCallback callback);

        void requestLocationUpdates();

        void newLocationCallback();

        void removeLocationUpdates();

        void getLastKnownLocationResponse(GetLastKnownLocationResponseCallback callback);

        interface GetLocationSettingResponseCallback {
            void onSatisfyLocationSetting(LocationSettingsResponse locationSettingsResponse);

            void onNotSatisfyLocationSetting(Exception e);
        }

        interface GetLastKnownLocationResponseCallback {
            void onLastKnownLocationReceived(String originLatLng);

            void onLastKnowLocationIsNull();
        }
    }

    interface DistanceMatrixService {

        void getDistanceMatrixResponse(String originLatLng, String destinationLatLng,
                                       GetDistanceMatrixResponseCallback callback);

        interface GetDistanceMatrixResponseCallback {
            void onDistanceAndDurationReceived(String distance, String duration);

            void onNoInternet();
        }
    }

    interface ConnectivityService {
        boolean isConnected();

        void getConnectivityStatusResponse(GetConnectivityStatusResponseCallback callback);

        interface GetConnectivityStatusResponseCallback {
            void onInternetAvailableReceived();

            void onInternetUnavailable();
        }
    }

    interface FirebaseAuthenticationService {
        void getCurrentUserResponse(int resultCode, Intent data,
                                    GetCurrentUserResponseCallback callback);

        interface GetCurrentUserResponseCallback {
            void onResultOk(String email);

            void onNoInternet();

            void onClickBackButton();
        }
    }
}
