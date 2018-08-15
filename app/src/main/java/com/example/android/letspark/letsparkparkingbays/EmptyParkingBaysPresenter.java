package com.example.android.letspark.letsparkparkingbays;

import com.example.android.letspark.data.EmptyParkingBay;
import com.example.android.letspark.data.EmptyParkingBaysDataSource;

import java.util.List;

/**
 * Listens to user actions from the UI (EmptyParkingBaysFragment), retrieves the data and updates
 * the UI as required.
 */
public class EmptyParkingBaysPresenter implements EmptyParkingBaysContract.Presenter {

    private EmptyParkingBaysDataSource emptyParkingBaysRemoteDataSource;

    private EmptyParkingBaysContract.View emptyParkingBaysView;

    public EmptyParkingBaysPresenter(EmptyParkingBaysDataSource emptyParkingBaysRemoteDataSource,
                                     EmptyParkingBaysContract.View emptyParkingBaysView) {
        this.emptyParkingBaysRemoteDataSource = emptyParkingBaysRemoteDataSource;
        this.emptyParkingBaysView = emptyParkingBaysView;

        emptyParkingBaysView.setPresenter(this);
    }

    @Override
    public void loadEmptyParkingBays() {
        emptyParkingBaysRemoteDataSource.getEmptyParkingBays(new EmptyParkingBaysDataSource.LoadEmptyParkingBaysCallBack() {
            @Override
            public void onEmptyParkingBaysLoaded(List<EmptyParkingBay> emptyParkingBayList) {
                emptyParkingBaysView.showEmptyParkingBays(emptyParkingBayList);
            }

            @Override
            public void onDataNotAvailable() {
                emptyParkingBaysView.showLoadingEmptyParkingBaysError();
            }
        });
    }

    @Override
    public void start() {
        loadEmptyParkingBays();
    }
}
