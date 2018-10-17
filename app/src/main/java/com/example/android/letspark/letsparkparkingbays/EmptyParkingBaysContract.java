package com.example.android.letspark.letsparkparkingbays;

import com.example.android.letspark.BasePresenter;
import com.example.android.letspark.BaseView;
import com.example.android.letspark.data.EmptyParkingBay;
import com.google.android.gms.maps.GoogleMap;

import java.util.List;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface EmptyParkingBaysContract {

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

        void showAddRemoveCarUi(String uid);

        void showSelectedCar();

        void showDurationOptionDialog();
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
    }
}
