package com.example.android.letspark.home;

import android.util.Log;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.model.ActiveParking;
import com.example.android.letspark.data.model.EmptyParkingBay;
import com.example.android.letspark.service.Service;
import com.google.android.gms.location.LocationSettingsResponse;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;
import org.joda.time.Interval;

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

    public static final String ZONE_ID = "Asia/Kuala_Lumpur";

    private long endTime;

    private boolean isBeforeFivePm;

    private String email = "";

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
                        homeView.shouldShowRequestPermissionRationale());
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
            loadParkingBasedOnUserRole();
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
    public boolean checkValidCarNumberPlateAndDuration(String carNumberPlate, int duration,
                                                       String parking) {
        if (carNumberPlate.isEmpty()) {
            homeView.showCarNumberPlateErrMsg();
            return false;
        } else if (duration == 0) {
            homeView.showDurationErrMsg();
            return false;
        } else if (parking.isEmpty()) {
            homeView.showParkingErrMsg();
            return false;
        } else {
            return true;
        }
    }

    @Override
    public double determinePayment(int duration) {
        if (duration >= 1 && duration <= 7) {
            return duration * 0.45;
        } else if (duration == 8 || duration == 9) {
            return 3.50;
        } else {
            return 0;
        }
    }

    @Override
    public long unixTimeSummation(long unixTime, int hour) {
        // Convert hour to minutes, then from minutes to second, after that from second to
        // millisecond since epoch.
        long hourInUnixTime = hour * 60 * 60 * 1000;

        // TODO: uncomment for demonstration
        // long hourInUnixTime = 15 * 1000;
        return unixTime + hourInUnixTime;
    }

    @Override
    public long convertHourToMilliseconds(int hour) {
        return hour * 60 * 60 * 1000;
    }

    @Override
    public void checkEndTimeIsBeforeFivePm(final String carNumberPlate, final int duration,
                                           final String parking) {
        homeView.showProgressBar(true);
        final long hourInMilliseconds = convertHourToMilliseconds(duration);
        dataSource.getCurrentUnixTime(new DataSource.GetCurrentUnixTimeCallback() {
            @Override
            public void onGetCurrentUnixTime(Long currentUnixTime) {
                homeView.showProgressBar(false);
                endTime = currentUnixTime + hourInMilliseconds;
                Instant instantEndTime = Instant.ofEpochMilli(endTime);
                DateTimeZone zone = DateTimeZone.forID(ZONE_ID);
                DateTime dtEndTime = new DateTime(instantEndTime, zone);

                DateTime today = new DateTime(zone);
                DateTime todayFivePm
                        = today.withHourOfDay(17).withMinuteOfHour(0).withSecondOfMinute(0);
                isBeforeFivePm = dtEndTime.isBefore(todayFivePm);
                if (isBeforeFivePm) {
                    pay(carNumberPlate, duration, parking);
                } else {
                    homeView.showProgressBar(true);
                    final long hourInMilliseconds = convertHourToMilliseconds(duration);
                    dataSource.getCurrentUnixTime(new DataSource.GetCurrentUnixTimeCallback() {
                        @Override
                        public void onGetCurrentUnixTime(Long currentUnixTime) {
                            homeView.showProgressBar(false);
                            endTime = currentUnixTime + hourInMilliseconds;
                            long timeLeftToFivePmInMinute = durationBetweenTwoDateTime(currentUnixTime);
                            int suggestedDuration = suggestDuration(timeLeftToFivePmInMinute);
                            pay(carNumberPlate, suggestedDuration, parking);
                        }

                        @Override
                        public void onCancelled(String errMsg) {
                            homeView.showProgressBar(false);
                            homeView.showDbErrMsg(errMsg);
                        }
                    });
                }
            }

            @Override
            public void onCancelled(String errMsg) {
                homeView.showProgressBar(false);
                homeView.showDbErrMsg(errMsg);
            }
        });
    }

    @Override
    public long durationBetweenTwoDateTime(long currentTime) {
        Instant instantCurrentTime = Instant.ofEpochMilli(currentTime);
        DateTimeZone zone = DateTimeZone.forID(ZONE_ID);
        DateTime dtCurrentTime = new DateTime(instantCurrentTime, zone);

        DateTime today = new DateTime(zone);
        DateTime todayFivePm = today.withHourOfDay(17).withMinuteOfHour(0).withSecondOfMinute(0);

        Duration duration = new Duration(dtCurrentTime, todayFivePm);
        return duration.getStandardMinutes();
    }

    @Override
    public int suggestDuration(long timeLeftToFivePmInMinute) {
        int suggestDuration;

        // Within 1 hour
        if (timeLeftToFivePmInMinute > 0 && timeLeftToFivePmInMinute <= 60) {
            suggestDuration = 1;
        }// Within 1 and 2 hour
        else if (timeLeftToFivePmInMinute > 60 && timeLeftToFivePmInMinute <= 120) {
            suggestDuration = 2;
        }// Within 2 and 3 hour
        else if (timeLeftToFivePmInMinute > 120 && timeLeftToFivePmInMinute <= 180) {
            suggestDuration = 3;
        }// Within 3 and 4 hour
        else if (timeLeftToFivePmInMinute > 180 && timeLeftToFivePmInMinute <= 240) {
            suggestDuration = 4;
        }// Within 4 and 5 hour
        else if (timeLeftToFivePmInMinute > 240 && timeLeftToFivePmInMinute <= 300) {
            suggestDuration = 5;
        }// Within 5 and 6 hour
        else if (timeLeftToFivePmInMinute > 300 && timeLeftToFivePmInMinute <= 360) {
            suggestDuration = 6;
        }// Within 6 and 7 hour
        else if (timeLeftToFivePmInMinute > 360 && timeLeftToFivePmInMinute <= 420) {
            suggestDuration = 7;
        }// Within 7 and 8 hour
        else if (timeLeftToFivePmInMinute > 420 && timeLeftToFivePmInMinute <= 480) {
            suggestDuration = 8;
        } // Within 8 and 9 hour
        else if (timeLeftToFivePmInMinute > 480 && timeLeftToFivePmInMinute <= 540) {
            suggestDuration = 9;
        } else {
            suggestDuration = 0;
        }
        return suggestDuration;
    }

    @Override
    public void checkCurrentTimeWithinParkingPeriod(final String carNumberPlate,
                                                    final int duration, final String parking) {
        homeView.showProgressBar(true);
        dataSource.getCurrentUnixTime(new DataSource.GetCurrentUnixTimeCallback() {
            @Override
            public void onGetCurrentUnixTime(Long currentUnixTime) {
                homeView.showProgressBar(false);

                Instant instantCurrentTime = Instant.ofEpochMilli(currentUnixTime);
                DateTimeZone zone = DateTimeZone.forID(ZONE_ID);
                DateTime dtCurrentTime = new DateTime(instantCurrentTime, zone);

                DateTime today = new DateTime(zone);
                DateTime todayFivePm
                        = today.withHourOfDay(17).withMinuteOfHour(0).withSecondOfMinute(0);

                DateTime todayEightAm
                        = today.withHourOfDay(8).withMinuteOfHour(0).withSecondOfMinute(0);

                Interval interval = new Interval(todayEightAm, todayFivePm);
                boolean withinParkingPeriod = interval.contains(dtCurrentTime);

                if (withinParkingPeriod) {
                    checkEndTimeIsBeforeFivePm(carNumberPlate, duration, parking);
                } else {
                    homeView.showNotInParkingEnforcementPeriodMsg();
                }
            }

            @Override
            public void onCancelled(String errMsg) {
                homeView.showProgressBar(false);
                homeView.showDbErrMsg(errMsg);
            }
        });
    }

    @Override
    public void pay(final String carNumberPlate, final int duration, final String parking) {
        //TODO: delete once payment feature is done
        final double payment = determinePayment(duration);
        homeView.showProgressBar(true);
        sharedPreferenceService.getCurrentUserUid(new Service
                .SharedPreferenceService.GetCurrentUserUidCallback() {
            @Override
            public void onGetUid(final String uid) {
                dataSource.writeNewTransaction(carNumberPlate, uid, parking, duration,
                        payment, new DataSource.GetStartTimeAndTransactionIdCallback() {
                            @Override
                            public void onGetStartTime(Long startTime, String transactionId) {
                                homeView.showProgressBar(false);
                                long hourInMillisecond = convertHourToMilliseconds(duration);
                                long endTime = unixTimeSummation(startTime, duration);

                                homeView.showProgressBar(true);
                                dataSource.writeNewActiveParking(uid, carNumberPlate, parking,
                                        startTime, hourInMillisecond, endTime, transactionId, payment,
                                        new DataSource.WriteActiveParkingCallback() {
                                            @Override
                                            public void onSuccess() {
                                                homeView.showProgressBar(false);
                                                homeView.showActiveParkingUiWithPaymentMsg(payment);
                                            }

                                            @Override
                                            public void onFailure(String errMsg) {
                                                homeView.showProgressBar(false);
                                                homeView.showDbErrMsg(errMsg);
                                            }
                                        });
                            }

                            @Override
                            public void onCancelled(String errMsg) {
                                homeView.showProgressBar(false);
                                homeView.showDbErrMsg(errMsg);
                            }
                        });
            }
        });
    }

    @Override
    public void checkExistActiveParking(boolean validCarAndDuration, final String carNumberPlate,
                                        final int duration, final String parking) {
        if (validCarAndDuration) {
            homeView.showProgressBar(true);
            sharedPreferenceService.getCurrentUserUid(new Service.SharedPreferenceService
                    .GetCurrentUserUidCallback() {
                @Override
                public void onGetUid(String uid) {
                    dataSource.getActiveParking(uid, new DataSource.GetActiveParkingCallback() {
                        @Override
                        public void onGetActiveParking(ActiveParking activeParking) {
                            homeView.showProgressBar(false);
                            boolean timerRunning = false;
                            if (activeParking != null) {
                                timerRunning = activeParking.getTimerRunning();
                            }
                            if (timerRunning) {
                                homeView.showActiveParkingUiWithActiveParkingExistMsg(timerRunning);
                            } else {
                                checkCurrentTimeWithinParkingPeriod(carNumberPlate, duration,
                                        parking);
                            }
                        }

                        @Override
                        public void onCancelled(String errMsg) {
                            homeView.showProgressBar(false);
                            homeView.showDbErrMsg(errMsg);
                        }
                    });
                }
            });
        }
    }

    @Override
    public void selectParking() {
        homeView.showParkingOptionDialog();
    }

    @Override
    public void loadViolatedParkingBays() {
        dataSource.getViolatedParkingBays(new DataSource.LoadViolatedParkingBaysCallBack() {
            @Override
            public void onViolatedParkingBaysLoaded(final List<EmptyParkingBay> violatedParkingBayList) {

                dataSource.getCurrentUnixTime(new DataSource.GetCurrentUnixTimeCallback() {
                    @Override
                    public void onGetCurrentUnixTime(Long currentUnixTime) {
                        List<EmptyParkingBay> temp
                                = filterViolatedParkingBays(violatedParkingBayList, currentUnixTime);
                        homeView.showViolatedParkingBays(temp);
                    }

                    @Override
                    public void onCancelled(String errMsg) {
                        homeView.showDbErrMsg(errMsg);
                    }
                });
            }

            @Override
            public void onDataNotAvailable() {
                homeView.showLoadingEmptyParkingBaysError();
            }
        });
    }

    @Override
    public List<EmptyParkingBay> filterViolatedParkingBays(final List<EmptyParkingBay> violatedParkingBayList,
                                                           long currentUnixTime) {
        Log.e("@@unixTime", String.valueOf(currentUnixTime));
        final List<EmptyParkingBay> temp = new ArrayList<>();

        for (int index = 0; index < violatedParkingBayList.size(); index++) {
            boolean vacant = violatedParkingBayList.get(index).getVacancy();
            Log.e("@@vacant", String.valueOf(vacant));
            long endTime = violatedParkingBayList.get(index).getEndTime();
            Log.e("@@endTime", String.valueOf(endTime));
            if (currentUnixTime > endTime && !vacant) {
                temp.add(violatedParkingBayList.get(index));
            }
        }
        return temp;
    }

    @Override
    public void loadParkingBasedOnUserRole() {
        sharedPreferenceService.getCurrentUserEmail(new Service.SharedPreferenceService
                .GetCurrentUserEmailCallback() {
            @Override
            public void onGetEmail(String email) {
                setEmail(email);
                if (email.contains("@letspark.com")) {
                    homeView.hideHistoryPaymentMethodActiveParkingMenuItemAndFloatingActionButton();
                    loadViolatedParkingBays();
                } else {
                    homeView.showSelectCarDurationParkingView(true);
                    loadEmptyParkingBays();
                }
            }
        });
    }

    @Override
    public boolean showActionBarButtonBasedOnUserRole() {
        if (email.contains("@letspark.com")) {
            return true;
        } else {
            return false;
        }
    }

    private void setEmail(String email) {
        this.email = email;
    }
}

