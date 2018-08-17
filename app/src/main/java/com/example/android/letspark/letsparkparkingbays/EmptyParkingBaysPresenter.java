package com.example.android.letspark.letsparkparkingbays;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.android.letspark.data.EmptyParkingBay;
import com.example.android.letspark.data.EmptyParkingBaysDataSource;

import java.util.List;

import static com.example.android.letspark.letsparkparkingbays.EmptyParkingBaysActivity.LOCATION_PERMISSION_REQUEST_CODE;

/**
 * Listens to user actions from the UI (EmptyParkingBaysFragment), retrieves the data and updates
 * the UI as required.
 */
public class EmptyParkingBaysPresenter implements EmptyParkingBaysContract.Presenter {

    private EmptyParkingBaysDataSource emptyParkingBaysRemoteDataSource;

    private EmptyParkingBaysContract.View emptyParkingBaysView;

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
