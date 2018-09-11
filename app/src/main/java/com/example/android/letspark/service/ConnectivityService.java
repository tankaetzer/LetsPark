package com.example.android.letspark.service;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import javax.inject.Inject;

/**
 * Implementation of connectivity service.
 */
public class ConnectivityService implements Service.ConnectivityService {

    private ConnectivityManager connectivityManager;

    @Inject
    public ConnectivityService(ConnectivityManager connectivityManager) {
        this.connectivityManager = connectivityManager;
    }

    /**
     * Determine if mobile device have an internet connection.
     *
     * @return true if network connectivity exists or is in the process of being established,
     * false otherwise.
     */
    public boolean isConnected() {
        NetworkInfo activeNetwork = connectivityManager.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    @Override
    public void getConnectivityStatusResponse(GetConnectivityStatusResponseCallback callback) {
        boolean internetAvailable = isConnected();

        if (internetAvailable) {
            callback.onInternetAvailableReceived();
        } else {
            callback.onInternetUnavailable();
        }
    }
}
