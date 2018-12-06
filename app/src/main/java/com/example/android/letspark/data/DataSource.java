package com.example.android.letspark.data;

import com.example.android.letspark.data.model.ActiveParking;
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

    void writeNewTransaction(String carNumberPlate, String uid, String parking, int duration,
                             double payment, GetStartTimeAndTransactionIdCallback callback);

    void getUserHistory(String uid, LoadUserHistoriesCallBack callBack);

    void getCurrentUnixTime(GetCurrentUnixTimeCallback callBack);

    void writeNewActiveParking(String uid, String carNumberPlate, String parking, long startTime,
                               long duration, long endTime, String transactionId, double payment, WriteActiveParkingCallback callback);

    void getActiveParking(String uid, GetActiveParkingCallback callback);

    void updateTimeLeftTimerRunningEndTime(String uid, long timeLeft, boolean timerRunning,
                                           UpdateTimeLeftTimerRunningEndTimeCallback callback);

    void updateTimerRunning(String uid, boolean timerRunning, UpdateTimerRunningCallback callback);

    void updateExistTransaction(String uid, String transactionId, int duration, double payment);

    void getViolatedParkingBays(LoadViolatedParkingBaysCallBack callBack);

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

    interface GetStartTimeAndTransactionIdCallback {
        void onGetStartTime(Long startTime, String transactionId);

        void onCancelled(String errMsg);
    }

    interface GetCurrentUnixTimeCallback {
        void onGetCurrentUnixTime(Long currentUnixTime);

        void onCancelled(String errMsg);
    }

    interface WriteActiveParkingCallback {
        void onSuccess();

        void onFailure(String errMsg);
    }

    interface GetActiveParkingCallback {
        void onGetActiveParking(ActiveParking activeParking);

        void onCancelled(String errMsg);
    }

    interface UpdateTimeLeftTimerRunningEndTimeCallback {
        void onSuccess();

        void onFailure(String errMsg);
    }

    interface UpdateTimerRunningCallback {
        void onSuccess();

        void onFailure(String errMsg);
    }

    interface LoadViolatedParkingBaysCallBack {
        void onViolatedParkingBaysLoaded(List<EmptyParkingBay> violatedParkingBayList);

        void onDataNotAvailable();
    }
}
