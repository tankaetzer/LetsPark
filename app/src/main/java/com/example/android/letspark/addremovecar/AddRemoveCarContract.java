package com.example.android.letspark.addremovecar;

import com.example.android.letspark.BasePresenter;
import com.example.android.letspark.BaseView;
import com.example.android.letspark.data.Car;

import java.util.List;

public interface AddRemoveCarContract {

    interface View extends BaseView<Presenter> {
        void showCarsAfterAddingOrRemoving(List<Car> cars);

        void showAddCarAlertDialog();

        void showEmptyCarErr();

        void showRemoteDbErrMsg(String errMsg);

        void showSuccessfullySavedCarMsg();

        void showNoCarsView(boolean show);

        void showCarExistErrMsg();

        void showSuccessfullyDeletedCarMsg();

        void showProgressBar(boolean show);

        void showEmptyParkingBaysUi(String carNumberPlate);
    }

    interface Presenter extends BasePresenter {
        void addCar(String carNumberPlate, List<Car> carList);

        void loadUserCars();

        void removeCar(Car currentCar);

        void processAndShowCars(List<Car> carList);

        boolean isCarExist(String carNumberPlate, List<Car> carList);

        void selectCar(String carNumberPlate);
    }
}
