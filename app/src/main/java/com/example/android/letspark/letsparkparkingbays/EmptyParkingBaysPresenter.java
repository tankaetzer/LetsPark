package com.example.android.letspark.letsparkparkingbays;

import com.example.android.letspark.data.EmptyParkingBay;
import com.example.android.letspark.data.EmptyParkingBaysDataSource;
import com.example.android.letspark.service.Service;

import java.util.List;

/**
 * Listens to user actions from the UI (EmptyParkingBaysFragment), retrieves the data and updates
 * the UI as required.
 */
public class EmptyParkingBaysPresenter implements EmptyParkingBaysContract.Presenter {

    private EmptyParkingBaysDataSource emptyParkingBaysRemoteDataSource;

    private EmptyParkingBaysContract.View emptyParkingBaysView;

    private Service locationService;

    public EmptyParkingBaysPresenter(EmptyParkingBaysDataSource emptyParkingBaysRemoteDataSource,
                                     EmptyParkingBaysContract.View emptyParkingBaysView,
                                     Service locationService) {
        this.emptyParkingBaysRemoteDataSource = emptyParkingBaysRemoteDataSource;
        this.emptyParkingBaysView = emptyParkingBaysView;
        this.locationService = locationService;

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
    public void askLocationSetting() {
        locationService.createLocationRequest();

        locationService.setBuilder();

        // Check whether current location settings are satisfied.
        locationService.checkCurrentLocationSetting();

        locationService.getLocationSettingResponse(new Service.getLocationSettingResponseCallback() {
            @Override
            public void onSatisfyLocationSetting() {
                askLocationPermission(emptyParkingBaysView.checkSelfPermission(),
                        emptyParkingBaysView.shouldShowRequestPermissionRationale());
            }

            @Override
            public void onNotSatisfyLocationSetting(Exception e) {
                emptyParkingBaysView.showLocationSettingDialog(e);
            }
        });
    }

    @Override
    public void askLocationPermission(boolean notGranted, boolean showRequestPermissionRationale) {
        // Check if the access fine location permission has been granted.
        if (notGranted) {
            // True if permission is not granted since user has previously denied the request.
            if (showRequestPermissionRationale) {
                emptyParkingBaysView.showErrorMessageWithAction();
            } else {
                emptyParkingBaysView.requestLocationPermissions();
            }
        } else {
            // Permission has already been granted.
        }
    }
}
