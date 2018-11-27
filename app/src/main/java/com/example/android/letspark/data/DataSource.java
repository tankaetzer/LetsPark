package com.example.android.letspark.data;

import com.example.android.letspark.data.model.Car;
import com.example.android.letspark.data.model.EmptyParkingBay;
import com.example.android.letspark.data.model.History;

import java.util.List;

/**
 * Main entry point for accessing empty parking bays data.
 */
public interface DataSource {

    void getEmptyParkingBays(LoadEmptyParkingBaysCallBack callBack);

    void writeNewUser(String uid, String email);

    void writeCarNumberPlate(String carNumberPlate, String uid, LoadUserCarsCallBack callBack);

    void getUserCars(String uid, LoadUserCarsCallBack callBack);

    void deleteCar(String uid, String key, LoadUserCarsCallBack callBack);

    void writeNewTransaction(String uid, String carNumberPlate, int duration, double payment);

    void getUserHistory(String uid, LoadUserHistoriesCallBack callBack);

    interface LoadEmptyParkingBaysCallBack {
        void onEmptyParkingBaysLoaded(List<EmptyParkingBay> emptyParkingBayList);

        void onDataNotAvailable();
    }

    interface LoadUserCarsCallBack {
        void onUserCarsLoaded(List<Car> carList);

        void onCancelled(String errMsg);
    }

    interface LoadUserHistoriesCallBack {
        void onUserHistoriesLoaded(List<History> historyList);

        void onCancelled(String errMsg);
    }
}
