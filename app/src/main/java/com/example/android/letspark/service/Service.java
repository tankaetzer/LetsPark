package com.example.android.letspark.service;

public interface Service {

    void getLocationSettingResponse(getLocationSettingResponseCallback callback);

    void createLocationRequest();

    void setBuilder();

    void checkCurrentLocationSetting();

    interface getLocationSettingResponseCallback {
        void onSatisfyLocationSetting();

        void onNotSatisfyLocationSetting(Exception e);
    }
}
