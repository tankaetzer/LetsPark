package com.example.android.letspark.addremovecar;

import com.example.android.letspark.data.model.Car;
import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.RemoteDataSource;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddRemoveCarPresenter implements AddRemoveCarContract.Presenter {

    private AddRemoveCarContract.View addRemoveCarView;

    private RemoteDataSource remoteDataSource;

    private String uid;

    public AddRemoveCarPresenter(String uid, AddRemoveCarContract.View addRemoveCarView,
                                 RemoteDataSource remoteDataSource) {
        this.uid = uid;
        this.addRemoveCarView = checkNotNull(addRemoveCarView);
        this.remoteDataSource = checkNotNull(remoteDataSource);
        addRemoveCarView.setPresenter(this);
    }

    @Override
    public void start() {
        loadUserCars();
    }

    @Override
    public void addCar(String carNumberPlate, List<Car> carList) {
        if (carNumberPlate.isEmpty()) {
            addRemoveCarView.showEmptyCarErr();
        } else if (isCarExist(carNumberPlate, carList)) {
            addRemoveCarView.showCarExistErrMsg();
        } else {
            addRemoveCarView.showProgressBar(true);
            remoteDataSource.writeCarNumberPlate(
                    carNumberPlate.toUpperCase(), uid,
                    new DataSource.LoadUserCarsCallBack() {
                        @Override
                        public void onUserCarsLoaded(List<Car> carList) {
                            addRemoveCarView.showProgressBar(false);
                            addRemoveCarView.showNoCarsView(false);
                            addRemoveCarView.showCarsAfterAddingOrRemoving(carList);
                            addRemoveCarView.showSuccessfullySavedCarMsg();
                        }

                        @Override
                        public void onCancelled(String errMsg) {
                            addRemoveCarView.showProgressBar(false);
                            addRemoveCarView.showRemoteDbErrMsg(errMsg);
                        }
                    });
        }
    }

    @Override
    public void loadUserCars() {
        addRemoveCarView.showProgressBar(true);
        remoteDataSource.getUserCars(uid,
                new DataSource.LoadUserCarsCallBack() {
                    @Override
                    public void onUserCarsLoaded(List<Car> carList) {
                        addRemoveCarView.showProgressBar(false);
                        processAndShowCars(carList);
                    }

                    @Override
                    public void onCancelled(String errMsg) {
                        addRemoveCarView.showProgressBar(false);
                        addRemoveCarView.showRemoteDbErrMsg(errMsg);
                    }
                });
    }

    @Override
    public void removeCar(Car currentCar) {
        addRemoveCarView.showProgressBar(true);
        remoteDataSource.deleteCar(uid, currentCar.getKey(),
                new DataSource.LoadUserCarsCallBack() {
                    @Override
                    public void onUserCarsLoaded(List<Car> carList) {
                        addRemoveCarView.showProgressBar(false);
                        processAndShowCars(carList);
                        addRemoveCarView.showSuccessfullyDeletedCarMsg();
                    }

                    @Override
                    public void onCancelled(String errMsg) {
                        addRemoveCarView.showProgressBar(false);
                        addRemoveCarView.showRemoteDbErrMsg(errMsg);
                    }
                });
    }

    @Override
    public void processAndShowCars(List<Car> carList) {
        if (carList.size() > 0) {
            addRemoveCarView.showNoCarsView(false);
        } else {
            addRemoveCarView.showNoCarsView(true);
        }
        addRemoveCarView.showCarsAfterAddingOrRemoving(carList);
    }

    @Override
    public boolean isCarExist(String carNumberPlate, List<Car> carList) {
        boolean carExist = false;
        for (int i = 0; i < carList.size(); i++) {
            carExist = carList.get(i).getCarNumberPlate().equalsIgnoreCase(carNumberPlate);
            if (carExist)
                break;
        }
        return carExist;
    }

    @Override
    public void selectCar(String carNumberPlate) {
        addRemoveCarView.showEmptyParkingBaysUi(carNumberPlate);
    }
}
