package com.example.android.letspark.letsparkparkingbays;

import com.example.android.letspark.data.EmptyParkingBay;
import com.example.android.letspark.data.EmptyParkingBaysDataSource;
import com.example.android.letspark.service.Service;
import com.google.android.gms.location.LocationSettingsResponse;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.android.letspark.addremovecar.AddRemoveCarActivity.REQUEST_ADD_REMOVE_CAR;
import static com.example.android.letspark.letsparkparkingbays.EmptyParkingBaysActivity.REQUEST_CHECK_SETTINGS;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI (EmptyParkingBaysFragment), retrieves the data and updates
 * the UI as required.
 */
public class EmptyParkingBaysPresenter implements EmptyParkingBaysContract.Presenter {

    private EmptyParkingBaysDataSource emptyParkingBaysRemoteEmptyParkingBaysDataSource;

    private EmptyParkingBaysContract.View emptyParkingBaysView;

    private Service.LocationService locationService;

    private Service.DistanceMatrixService distanceMatrixService;

    private Service.ConnectivityService connectivityService;

    private String uid;

    public EmptyParkingBaysPresenter(String uid,
                                     EmptyParkingBaysDataSource emptyParkingBaysRemoteEmptyParkingBaysDataSource,
                                     EmptyParkingBaysContract.View emptyParkingBaysView,
                                     Service.LocationService locationService,
                                     Service.DistanceMatrixService distanceMatrixService,
                                     Service.ConnectivityService connectivityService) {
        this.uid = uid;
        this.emptyParkingBaysRemoteEmptyParkingBaysDataSource = checkNotNull(emptyParkingBaysRemoteEmptyParkingBaysDataSource);
        this.emptyParkingBaysView = checkNotNull(emptyParkingBaysView);
        this.locationService = checkNotNull(locationService);
        this.distanceMatrixService = checkNotNull(distanceMatrixService);
        this.connectivityService = checkNotNull(connectivityService);

        emptyParkingBaysView.setPresenter(this);
    }

    @Override
    public void loadEmptyParkingBays() {
        emptyParkingBaysRemoteEmptyParkingBaysDataSource.getEmptyParkingBays(new EmptyParkingBaysDataSource.
                LoadEmptyParkingBaysCallBack() {
            @Override
            public void onEmptyParkingBaysLoaded(List<EmptyParkingBay> emptyParkingBayList) {
                List<EmptyParkingBay> temp = filterEmptyParkingBays(emptyParkingBayList);
                emptyParkingBaysView.showEmptyParkingBays(temp);
            }

            @Override
            public void onDataNotAvailable() {
                emptyParkingBaysView.showLoadingEmptyParkingBaysError();
            }
        });
    }

    @Override
    public void start() {
        checkConnectivity();
    }

    @Override
    public void askLocationSetting() {
        locationService.getLocationSettingResponse(new Service.LocationService.
                GetLocationSettingResponseCallback() {
            @Override
            public void onSatisfyLocationSetting(LocationSettingsResponse locationSettingsResponse) {
                askLocationPermission(emptyParkingBaysView.checkSelfPermission(),
                        emptyParkingBaysView
                                .shouldShowRequestPermissionRationale());
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
                emptyParkingBaysView.showLocationErrMsgWithAction();
            } else {
                emptyParkingBaysView.requestLocationPermissions();
            }
        } else {
            // Permission has already been granted.
            loadEmptyParkingBays();
        }
    }

    /**
     * Request distance and duration from Google Distance Matrix API.
     *
     * @param destinationLatLng destination latitude longitude
     * @param rate              parking price rate for an hour.
     */
    @Override
    public void requestDistanceMatrix(final String destinationLatLng, final double rate) {
        emptyParkingBaysView.showProgressBar(true);
        locationService.getLastKnownLocationResponse(new Service.LocationService
                .GetLastKnownLocationResponseCallback() {
            @Override
            public void onLastKnownLocationReceived(String originLatLng) {
                getDistanceMatrixResponse(originLatLng, destinationLatLng, rate);
            }

            @Override
            public void onLastKnowLocationIsNull() {
                boolean isInternetConnect = getConnectivityStatus();
                processOnLastKnowLocationIsNullView(isInternetConnect, rate);
            }
        });
    }

    @Override
    public void createLocationCallback() {
        locationService.newLocationCallback();
    }

    @Override
    public void startLocationUpdate() {
        locationService.requestLocationUpdates();
    }

    @Override
    public void stopLocationUpdate() {
        locationService.removeLocationUpdates();
    }

    @Override
    public boolean getConnectivityStatus() {
        return connectivityService.isConnected();
    }

    @Override
    public void checkConnectivity() {
        connectivityService.getConnectivityStatusResponse(new Service.ConnectivityService
                .GetConnectivityStatusResponseCallback() {
            @Override
            public void onInternetAvailableReceived() {
                askLocationSetting();
            }

            @Override
            public void onInternetUnavailable() {
                emptyParkingBaysView.showConnectivityErrMsg();
            }
        });
    }

    @Override
    public void hideDistanceDurationRateTextviewAndProgressbar() {
        emptyParkingBaysView.showProgressBar(false);
        emptyParkingBaysView.showDistanceDurationAndRate(false);
    }

    @Override
    public void processOnLastKnowLocationIsNullView(boolean isInternetConnect, double rate) {

        emptyParkingBaysView.showProgressBar(false);

        if (isInternetConnect) {
            emptyParkingBaysView.setRateAndDefaultDistanceDuration(rate);
            emptyParkingBaysView.showDistanceDurationAndRate(true);
            emptyParkingBaysView.showGettingLocationMsg();
        } else {
            emptyParkingBaysView.showDistanceDurationAndRate(false);
            emptyParkingBaysView.showConnectivityAndLocationErrMsg();
        }
    }

    @Override
    public void getDistanceMatrixResponse(String originLatLng, String destinationLatLng,
                                          final double rate) {
        distanceMatrixService.getDistanceMatrixResponse(originLatLng, destinationLatLng,
                new Service.DistanceMatrixService.GetDistanceMatrixResponseCallback() {
                    @Override
                    public void onDistanceAndDurationReceived(String distance,
                                                              String duration) {
                        emptyParkingBaysView.showProgressBar(false);
                        emptyParkingBaysView.setDistanceDurationAndRate(distance, duration,
                                rate);
                        emptyParkingBaysView.showDistanceDurationAndRate(true);
                    }

                    @Override
                    public void onNoInternet() {
                        emptyParkingBaysView.showProgressBar(false);
                        emptyParkingBaysView.showDistanceDurationAndRate(false);
                        emptyParkingBaysView.showDistanceDurationCalculationErrMsg();
                    }
                });
    }

    @Override
    public List<EmptyParkingBay> filterEmptyParkingBays(List<EmptyParkingBay> emptyParkingBayList) {
        List<EmptyParkingBay> temp = new ArrayList<>();
        for (int index = 0; index < emptyParkingBayList.size(); index++) {
            boolean emptyParking = emptyParkingBayList.get(index).getVacancy();
            if (emptyParking) {
                temp.add(emptyParkingBayList.get(index));
            }
        }
        return temp;
    }

    @Override
    public void selectCar() {
        emptyParkingBaysView.showAddRemoveCarUi(uid);
    }

    @Override
    public void result(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            askLocationPermission(emptyParkingBaysView.checkSelfPermission(),
                    emptyParkingBaysView.shouldShowRequestPermissionRationale());
        } else if (requestCode == REQUEST_ADD_REMOVE_CAR && resultCode == RESULT_OK) {
            emptyParkingBaysView.showSelectedCar();
        }
    }

    @Override
    public void selectDuration() {
        emptyParkingBaysView.showDurationOptionDialog();
    }
}
