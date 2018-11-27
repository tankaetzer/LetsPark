package com.example.android.letspark.addremovecar;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.data.model.Car;
import com.example.android.letspark.service.Service;
import com.example.android.letspark.service.SharedPreferenceService;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of AddRemoveCarPresenter.
 */
public class AddRemoveCarPresenterTest {

    private static List<Car> carList;

    @Mock
    private AddRemoveCarContract.View addRemoveCarView;

    @Mock
    private RemoteDataSource remoteDataSource;

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    @Captor
    private ArgumentCaptor<DataSource.LoadUserCarsCallBack>
            loadUserCarsCallBackArgumentCaptor;

    @Captor
    private ArgumentCaptor<Service.SharedPreferenceService.GetCurrentUserUidCallback>
            getCurrentUserUidCallbackArgumentCaptor;


    private AddRemoveCarPresenter addRemoveCarPresenter;

    private String uid = "xxxxxxxx";

    private String carNumberPlate = "QWE1234";

    private String errMsg = "error";

    @Before
    public void setup() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        carList = Lists.newArrayList(new Car("WWW1234", "WW"),
                new Car("ASD", "AS"), new Car("ZXC", "ZX"));

        addRemoveCarPresenter = new AddRemoveCarPresenter(addRemoveCarView, remoteDataSource,
                sharedPreferenceService);
    }

    @Test
    public void createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test.
        addRemoveCarPresenter = new AddRemoveCarPresenter(addRemoveCarView, remoteDataSource,
                sharedPreferenceService);

        // Then the presenter is set to the view.
        verify(addRemoveCarView).setPresenter(addRemoveCarPresenter);
    }

    @Test
    public void addCar_emptyCarNumberPlate_showEmptyCarErr() {
        // Empty car number plate.
        String carNumberPlate = "";

        addRemoveCarPresenter.addCar(carNumberPlate, carList);

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        // Verify showEmptyCarErr is called.
        verify(addRemoveCarView).showEmptyCarErr();
    }

    @Test
    public void addCar_carAlreadyExist_showCarExistErrMsg() {
        List<Car> carList = Lists.newArrayList(new Car("QWE1234", "QW"),
                new Car("ASD", "AS"), new Car("ZXC", "ZX"));

        addRemoveCarPresenter.addCar(carNumberPlate, carList);

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        // Verify showEmptyCarErr is called.
        verify(addRemoveCarView).showCarExistErrMsg();
    }

    @Test
    public void addCar_onUserCarsLoaded_hideProgressBarHideNoCarsViewShowCarListShowSuccessfullySavedCarMsg() {
        addRemoveCarPresenter.addCar(carNumberPlate, carList);

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(addRemoveCarView).showProgressBar(true);

        // Callback is captured and invoked with stubbed carList.
        verify(remoteDataSource).writeCarNumberPlate(anyString(),
                anyString(), loadUserCarsCallBackArgumentCaptor.capture());
        loadUserCarsCallBackArgumentCaptor.getValue().onUserCarsLoaded(carList);

        verify(addRemoveCarView).showProgressBar(false);
        verify(addRemoveCarView).showNoCarsView(false);
        verify(addRemoveCarView).showCarsAfterAddingOrRemoving(carList);
        verify(addRemoveCarView).showSuccessfullySavedCarMsg();
    }

    @Test
    public void addCar_onCancelled_showProgressBarAndShowRemoteDbErrMsg() {
        addRemoveCarPresenter.addCar(carNumberPlate, carList);

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(addRemoveCarView).showProgressBar(true);

        // Callback is captured and invoked with stubbed emptyParkingBay.
        verify(remoteDataSource).writeCarNumberPlate(anyString(),
                anyString(), loadUserCarsCallBackArgumentCaptor.capture());
        loadUserCarsCallBackArgumentCaptor.getValue().onCancelled(errMsg);

        verify(addRemoveCarView).showProgressBar(false);
        verify(addRemoveCarView).showRemoteDbErrMsg(errMsg);
    }

    @Test
    public void loadUserCars_onUserCarsLoaded_showProgressBarThenHideProgressBar() {
        addRemoveCarPresenter.loadUserCars();

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(addRemoveCarView).showProgressBar(true);
        verify(remoteDataSource).getUserCars(anyString(),
                loadUserCarsCallBackArgumentCaptor.capture());

        loadUserCarsCallBackArgumentCaptor.getValue().onUserCarsLoaded(carList);

        verify(addRemoveCarView).showProgressBar(false);
    }

    @Test
    public void loadUserCars_onCancelled_showRemoteDbErrMsg() {
        addRemoveCarPresenter.loadUserCars();

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(addRemoveCarView).showProgressBar(true);
        verify(remoteDataSource).getUserCars(anyString(),
                loadUserCarsCallBackArgumentCaptor.capture());

        loadUserCarsCallBackArgumentCaptor.getValue().onCancelled(errMsg);

        verify(addRemoveCarView).showProgressBar(false);
        verify(addRemoveCarView).showRemoteDbErrMsg(errMsg);
    }

    @Test
    public void removeCar_onUserCarsLoaded_hideProgressBarAndshowSuccessfullyDeletedCarMsg() {
        Car car = new Car("WWW1234", "XXX");

        addRemoveCarPresenter.removeCar(car);

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(addRemoveCarView).showProgressBar(true);
        verify(remoteDataSource).deleteCar(anyString(), anyString(),
                loadUserCarsCallBackArgumentCaptor.capture());
        loadUserCarsCallBackArgumentCaptor.getValue().onUserCarsLoaded(carList);
        verify(addRemoveCarView).showProgressBar(false);
        verify(addRemoveCarView).showSuccessfullyDeletedCarMsg();
    }

    @Test
    public void removeCar_onCancelled_hideProgressBarAndShowRemoteDbErrMsg() {
        Car car = new Car("WWW1234", "XXX");

        addRemoveCarPresenter.removeCar(car);

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(addRemoveCarView).showProgressBar(true);
        verify(remoteDataSource).deleteCar(anyString(), anyString(),
                loadUserCarsCallBackArgumentCaptor.capture());
        loadUserCarsCallBackArgumentCaptor.getValue().onCancelled(errMsg);
        verify(addRemoveCarView).showProgressBar(false);
        verify(addRemoveCarView).showRemoteDbErrMsg(errMsg);
    }

    @Test
    public void processAndShowCars_notEmptyCarList_hideNoCarsViewAndShowCar() {
        addRemoveCarPresenter.processAndShowCars(carList);

        verify(addRemoveCarView).showNoCarsView(false);
        verify(addRemoveCarView).showCarsAfterAddingOrRemoving(carList);
    }

    @Test
    public void processAndShowCars_emptyCarList_showNoCarsViewAndShowCar() {
        // Setup empty carList.
        List<Car> carList = new ArrayList<>();

        addRemoveCarPresenter.processAndShowCars(carList);
        verify(addRemoveCarView).showNoCarsView(true);
    }

    @Test
    public void isCarExist_carExist_returnTrue() {
        // Setup a new carList with same value as carNumberPlate.
        List<Car> carList = Lists.newArrayList(new Car("QWE1234", "WW"));

        boolean exist = addRemoveCarPresenter.isCarExist(carNumberPlate, carList);

        assertTrue(exist);
    }

    @Test
    public void isCarExist_carNotExist_returnFalse() {
        boolean exist = addRemoveCarPresenter.isCarExist(carNumberPlate, carList);
        assertFalse(exist);
    }

    @Test
    public void selectCar_showEmptyParkingBaysUi() {
        addRemoveCarPresenter.selectCar(carNumberPlate);
        verify(addRemoveCarView).showEmptyParkingBaysUi(carNumberPlate);
    }
}
