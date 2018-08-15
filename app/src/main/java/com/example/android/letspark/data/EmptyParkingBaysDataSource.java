package com.example.android.letspark.data;

import java.util.List;

/**
 * Main entry point for accessing empty parking bays data.
 */
public interface EmptyParkingBaysDataSource {

    void getEmptyParkingBays(LoadEmptyParkingBaysCallBack callBack);

    interface LoadEmptyParkingBaysCallBack {
        void onEmptyParkingBaysLoaded(List<EmptyParkingBay> emptyParkingBayList);

        void onDataNotAvailable();
    }
}
