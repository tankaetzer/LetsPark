package com.example.android.letspark.home;

import com.example.android.letspark.BasePresenter;
import com.example.android.letspark.BaseView;
import com.example.android.letspark.data.model.EmptyParkingBay;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface HomeContract {

    interface View extends BaseView<Presenter> {
        void showEmptyParkingBays(List<EmptyParkingBay> emptyParkingBayList);

        void setGoogleMap(GoogleMap googleMap);

        void showLoadingEmptyParkingBaysError();

        GoogleMap getMap();

        void showLocationErrMsgWithAction();

        boolean checkSelfPermission();

        boolean shouldShowRequestPermissionRationale();

        void requestLocationPermissions();

        void showLocationSettingDialog(Exception e);

        void setDistanceDurationAndRate(String distance, String duration, double rate);

        void showDistanceDurationAndRate(boolean show);

        void showProgressBar(boolean show);

        void showDistanceDurationCalculationErrMsg();

        void showGettingLocationMsg();

        void showConnectivityAndLocationErrMsg();

        void showConnectivityErrMsg();

        void setRateAndDefaultDistanceDuration(double rate);

        void showAddRemoveCarUi();

        void showSelectedCar();

        void showDurationOptionDialog();

        void showCarNumberPlateErrMsg();

        void showDurationErrMsg();

        void showDbErrMsg(String errMsg);

        void showActiveParkingUi();

        void showNotInParkingEnforcementPeriodMsg();

        void showActiveParkingUiWithPaymentMsg(double payment);

        void showActiveParkingUiWithActiveParkingExistMsg(boolean timerRunning);

        void showParkingOptionDialog();

        void showParkingErrMsg();

        void showViolatedParkingBays(List<EmptyParkingBay> violatedParkingBayList);

        void showSelectCarDurationParkingView(boolean show);

        void hideHistoryPaymentMethodActiveParkingMenuItemAndFloatingActionButton();
    }

    interface Presenter extends BasePresenter {
        void loadEmptyParkingBays();

        void askLocationSetting();

        void askLocationPermission(boolean notGranted, boolean showRequestPermissionRationale);

        void requestDistanceMatrix(String destinationLatLng, double rate);

        void startLocationUpdate();

        void createLocationCallback();

        void stopLocationUpdate();

        boolean getConnectivityStatus();

        void checkConnectivity();

        void hideDistanceDurationRateTextviewAndProgressbar();

        void processOnLastKnowLocationIsNullView(boolean isInternetConnect, double rate);

        void getDistanceMatrixResponse(String originLatLng, String destinationLatLng, double rate);

        List<EmptyParkingBay> filterEmptyParkingBays(List<EmptyParkingBay> emptyParkingBayList);

        void selectCar();

        void result(int requestCode, int resultCode);

        void selectDuration();

        void pay(String carNumberPlate, int duration, String parking);

        boolean checkValidCarNumberPlateAndDuration(String carNumberPlate, int duration, String parking);

        double determinePayment(int duration);

        long unixTimeSummation(long unixTime, int hour);

        long convertHourToMilliseconds(int hour);

        void checkEndTimeIsBeforeFivePm(String carNumberPlate, int duration, String parking);

        long durationBetweenTwoDateTime(long currentTime);

        int suggestDuration(long timeLeftToFivePmInMinute);

        void checkCurrentTimeWithinParkingPeriod(String carNumberPlate, int duration, String parking);

        void checkExistActiveParking(boolean validCarAndDuration, String carNumberPlate,
                                     int duration, String parking);

        void selectParking();

        void loadViolatedParkingBays();

        List<EmptyParkingBay> filterViolatedParkingBays(List<EmptyParkingBay> violatedParkingBayList,
                                                        long currentUnixTime);

        void loadParkingBasedOnUserRole();

        boolean showActionBarButtonBasedOnUserRole();
    }
}
