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

        void showErrorMessageWithAction();

        boolean checkSelfPermission();

        boolean shouldShowRequestPermissionRationale();

        void requestLocationPermissions();

        void showLocationSettingDialog(Exception e);
    }

    interface Presenter extends BasePresenter {
        void loadEmptyParkingBays();

        void askLocationSetting();

        void askLocationPermission(boolean notGranted, boolean showRequestPermissionRationale);
    }
}
