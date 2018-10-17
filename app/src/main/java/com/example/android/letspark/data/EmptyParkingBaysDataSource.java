package com.example.android.letspark.data;

import java.util.List;

/**
 * Main entry point for accessing empty parking bays data.
 */
// TODO: rename to remote data source
public interface EmptyParkingBaysDataSource {

    void getEmptyParkingBays(LoadEmptyParkingBaysCallBack callBack);

    interface LoadEmptyParkingBaysCallBack {
        void onEmptyParkingBaysLoaded(List<EmptyParkingBay> emptyParkingBayList);

        void onDataNotAvailable();
    }

    void writeNewUser(String uid, String email);

    void writeCarNumberPlate(String carNumberPlate, String uid, LoadUserCarsCallBack callBack);

    void getUserCars(String uid, LoadUserCarsCallBack callBack);

    void deleteCar(String uid, String key, LoadUserCarsCallBack callBack);

    interface LoadUserCarsCallBack {
        void onUserCarsLoaded(List<Car> carList);

        void onCancelled(String errMsg);
    }
}
