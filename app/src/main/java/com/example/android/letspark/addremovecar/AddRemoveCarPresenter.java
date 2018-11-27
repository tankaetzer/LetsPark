package com.example.android.letspark.addremovecar;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.data.model.Car;
import com.example.android.letspark.service.Service;
import com.example.android.letspark.service.SharedPreferenceService;

import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class AddRemoveCarPresenter implements AddRemoveCarContract.Presenter {

    private AddRemoveCarContract.View addRemoveCarView;

    private RemoteDataSource remoteDataSource;

    private SharedPreferenceService sharedPreferenceService;

    public AddRemoveCarPresenter(AddRemoveCarContract.View addRemoveCarView,
                                 RemoteDataSource remoteDataSource,
                                 SharedPreferenceService sharedPreferenceService) {
        this.addRemoveCarView = checkNotNull(addRemoveCarView);
        this.remoteDataSource = checkNotNull(remoteDataSource);
        this.sharedPreferenceService = checkNotNull(sharedPreferenceService);
        addRemoveCarView.setPresenter(this);
    }

    @Override
    public void start() {
        loadUserCars();
    }

    @Override
    public void addCar(final String carNumberPlate, final List<Car> carList) {
        sharedPreferenceService.getCurrentUserUid(new Service
                .SharedPreferenceService.GetCurrentUserUidCallback() {
            @Override
            public void onGetUid(String uid) {
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
        });
    }

    @Override
    public void loadUserCars() {
        addRemoveCarView.showProgressBar(true);

        sharedPreferenceService.getCurrentUserUid(new Service
                .SharedPreferenceService.GetCurrentUserUidCallback() {
            @Override
            public void onGetUid(String uid) {
                remoteDataSource.getUserCars(uid, new DataSource.LoadUserCarsCallBack() {
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
        });
    }

    @Override
    public void removeCar(final Car currentCar) {
        addRemoveCarView.showProgressBar(true);

        sharedPreferenceService.getCurrentUserUid(new Service
                .SharedPreferenceService.GetCurrentUserUidCallback() {
            @Override
            public void onGetUid(String uid) {
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
