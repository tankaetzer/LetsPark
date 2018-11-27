package com.example.android.letspark.home;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.model.EmptyParkingBay;
import com.example.android.letspark.service.Service;
import com.google.android.gms.location.LocationSettingsResponse;

import java.util.ArrayList;
import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.android.letspark.addremovecar.AddRemoveCarActivity.REQUEST_ADD_REMOVE_CAR;
import static com.example.android.letspark.home.HomeActivity.REQUEST_CHECK_SETTINGS;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI (HomeFragment), retrieves the data and updates
 * the UI as required.
 */
public class HomePresenter implements HomeContract.Presenter {

    private DataSource dataSource;

    private HomeContract.View homeView;

    private Service.LocationService locationService;

    private Service.DistanceMatrixService distanceMatrixService;

    private Service.ConnectivityService connectivityService;

    private Service.SharedPreferenceService sharedPreferenceService;

    public HomePresenter(DataSource dataSource,
                         HomeContract.View homeView,
                         Service.LocationService locationService,
                         Service.DistanceMatrixService distanceMatrixService,
                         Service.ConnectivityService connectivityService,
                         Service.SharedPreferenceService sharedPreferenceService) {
        this.dataSource = checkNotNull(dataSource);
        this.homeView = checkNotNull(homeView);
        this.locationService = checkNotNull(locationService);
        this.distanceMatrixService = checkNotNull(distanceMatrixService);
        this.connectivityService = checkNotNull(connectivityService);
        this.sharedPreferenceService = checkNotNull(sharedPreferenceService);
        homeView.setPresenter(this);
    }

    @Override
    public void loadEmptyParkingBays() {
        dataSource.getEmptyParkingBays(new DataSource.
                LoadEmptyParkingBaysCallBack() {
            @Override
            public void onEmptyParkingBaysLoaded(List<EmptyParkingBay> emptyParkingBayList) {
                List<EmptyParkingBay> temp = filterEmptyParkingBays(emptyParkingBayList);
                homeView.showEmptyParkingBays(temp);
            }

            @Override
            public void onDataNotAvailable() {
                homeView.showLoadingEmptyParkingBaysError();
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
                askLocationPermission(homeView.checkSelfPermission(),
                        homeView
                                .shouldShowRequestPermissionRationale());
            }

            @Override
            public void onNotSatisfyLocationSetting(Exception e) {
                homeView.showLocationSettingDialog(e);
            }
        });
    }

    @Override
    public void askLocationPermission(boolean notGranted, boolean showRequestPermissionRationale) {
        // Check if the access fine location permission has been granted.
        if (notGranted) {
            // True if permission is not granted since user has previously denied the request.
            if (showRequestPermissionRationale) {
                homeView.showLocationErrMsgWithAction();
            } else {
                homeView.requestLocationPermissions();
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
        homeView.showProgressBar(true);
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
                homeView.showConnectivityErrMsg();
            }
        });
    }

    @Override
    public void hideDistanceDurationRateTextviewAndProgressbar() {
        homeView.showProgressBar(false);
        homeView.showDistanceDurationAndRate(false);
    }

    @Override
    public void processOnLastKnowLocationIsNullView(boolean isInternetConnect, double rate) {

        homeView.showProgressBar(false);

        if (isInternetConnect) {
            homeView.setRateAndDefaultDistanceDuration(rate);
            homeView.showDistanceDurationAndRate(true);
            homeView.showGettingLocationMsg();
        } else {
            homeView.showDistanceDurationAndRate(false);
            homeView.showConnectivityAndLocationErrMsg();
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
                        homeView.showProgressBar(false);
                        homeView.setDistanceDurationAndRate(distance, duration,
                                rate);
                        homeView.showDistanceDurationAndRate(true);
                    }

                    @Override
                    public void onNoInternet() {
                        homeView.showProgressBar(false);
                        homeView.showDistanceDurationAndRate(false);
                        homeView.showDistanceDurationCalculationErrMsg();
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
        homeView.showAddRemoveCarUi();
    }

    @Override
    public void result(int requestCode, int resultCode) {
        if (requestCode == REQUEST_CHECK_SETTINGS && resultCode == RESULT_OK) {
            askLocationPermission(homeView.checkSelfPermission(),
                    homeView.shouldShowRequestPermissionRationale());
        } else if (requestCode == REQUEST_ADD_REMOVE_CAR && resultCode == RESULT_OK) {
            homeView.showSelectedCar();
        }
    }

    @Override
    public void selectDuration() {
        homeView.showDurationOptionDialog();
    }

    @Override
    public void pay(final String carNumberPlate, final int duration) {
        boolean carNumberPlateAndDurationValid =
                checkValidCarNumberPlateAndDuration(carNumberPlate, duration);

        final double payment = determinePayment(duration);

        //TODO: delete once payment feature is done
        if (carNumberPlateAndDurationValid) {
            sharedPreferenceService.getCurrentUserUid(new Service
                    .SharedPreferenceService.GetCurrentUserUidCallback() {
                @Override
                public void onGetUid(String uid) {
                    dataSource.writeNewTransaction(uid, carNumberPlate, duration, payment);
                }
            });
        }
    }

    @Override
    public boolean checkValidCarNumberPlateAndDuration(String carNumberPlate, int duration) {
        if (carNumberPlate.isEmpty()) {
            homeView.showCarNumberPlateErrMsg();
            return false;
        } else if (duration == 0) {
            homeView.showDurationErrMsg();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public double determinePayment(int duration) {
        if (duration >= 1 && duration <= 4) {
            return duration * 0.45;
        } else if (duration == 9) {
            return 3.50;
        } else {
            return 0;
        }
    }


}
