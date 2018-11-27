package com.example.android.letspark.home;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.data.model.EmptyParkingBay;
import com.example.android.letspark.service.ConnectivityService;
import com.example.android.letspark.service.DistanceMatrixService;
import com.example.android.letspark.service.LocationService;
import com.example.android.letspark.service.Service;
import com.example.android.letspark.service.SharedPreferenceService;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static android.app.Activity.RESULT_OK;
import static com.example.android.letspark.addremovecar.AddRemoveCarActivity.REQUEST_ADD_REMOVE_CAR;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of HomePresenter.
 */
public class HomePresenterTest {

    private static List<EmptyParkingBay> emptyParkingBayList;

    private static List<EmptyParkingBay> filterEmptyParkingBayList;

    @Mock
    private HomeContract.View homeView;

    @Mock
    private RemoteDataSource remoteDataSource;

    @Mock
    private LocationService locationService;

    @Mock
    private DistanceMatrixService distanceMatrixService;

    @Mock
    private ConnectivityService connectivityService;

    @Mock
    private SharedPreferenceService sharedPreferenceService;

    @Captor
    private ArgumentCaptor<DataSource.LoadEmptyParkingBaysCallBack>
            loadEmptyParkingBaysCallBackArgumentCaptor;

    @Captor
    private ArgumentCaptor<Service.LocationService.GetLocationSettingResponseCallback>
            getLocationSettingResponseCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<Service.LocationService.GetLastKnownLocationResponseCallback>
            getLastKnownLocationResponseCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<Service.ConnectivityService.GetConnectivityStatusResponseCallback>
            getConnectivityStatusResponseCallbackArgumentCaptor;

    @Captor
    private ArgumentCaptor<Service.DistanceMatrixService.GetDistanceMatrixResponseCallback>
            getDistanceMatrixResponseCallbackArgumentCaptor;

    private HomePresenter homePresenter;

    private double rate = 0.80;

    private Exception e;

    @Before
    public void setup() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test.
        homePresenter = new HomePresenter(remoteDataSource, homeView, locationService,
                distanceMatrixService, connectivityService, sharedPreferenceService);

        // Add 2 empty parking bays into list.
        emptyParkingBayList = Lists.newArrayList(
                new EmptyParkingBay(1, 1, "KK3-1", 0.7, true),
                new EmptyParkingBay(2, 2, "KK3-2", 0.6, true));

        // Add 1 empty parking bays into list.
        filterEmptyParkingBayList = Lists.newArrayList(
                new EmptyParkingBay(1, 1, "KK3-1", 0.7, false),
                new EmptyParkingBay(2, 2, "KK3-2", 0.6, true));
    }

    @Test
    public void createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test.
        homePresenter = new HomePresenter(remoteDataSource, homeView, locationService,
                distanceMatrixService, connectivityService, sharedPreferenceService);

        // Then the presenter is set to the view.
        verify(homeView).setPresenter(homePresenter);
    }

    @Test
    public void loadAllTasksFromRemoteDataSourceAndLoadIntoView_firesOnEmptyParkingBaysLoaded() {

        homePresenter.loadEmptyParkingBays();

        // Callback is captured and invoked with stubbed emptyParkingBay.
        verify(remoteDataSource).getEmptyParkingBays(
                loadEmptyParkingBaysCallBackArgumentCaptor.capture());
        loadEmptyParkingBaysCallBackArgumentCaptor.getValue()
                .onEmptyParkingBaysLoaded(emptyParkingBayList);

        // Check whether showEmptyParkingBays is called.
        verify(homeView).showEmptyParkingBays(emptyParkingBayList);
    }

    @Test
    public void loadAllTasksFromRemoteDataSourceAndLoadIntoView_firesOnDataNotAvailable() {

        homePresenter.loadEmptyParkingBays();

        // Callback is captured.
        verify(remoteDataSource).getEmptyParkingBays(
                loadEmptyParkingBaysCallBackArgumentCaptor.capture());
        loadEmptyParkingBaysCallBackArgumentCaptor.getValue().onDataNotAvailable();

        // Check whether showEmptyParkingBays is called.
        verify(homeView).showLoadingEmptyParkingBaysError();
    }

    @Test
    public void askLocationPermission_notGrantedIsTrue_showRequestPermissionRationaleIsTrue_showLocationErrMsgWithAction() {
        homePresenter.askLocationPermission(true,
                true);

        // Check whether showLocationErrMsgWithAction is called.
        verify(homeView).showLocationErrMsgWithAction();
    }

    @Test
    public void askLocationPermission_notGrantedIsTrue_showRequestPermissionRationaleIsFalse_requestLocationPermission() {
        homePresenter.askLocationPermission(true,
                false);

        // Check whether requestLocationPermissions is called.
        verify(homeView).requestLocationPermissions();
    }

    @Test
    public void askLocationPermission_notGrantedIsFalse_getEmptyParkingBays() {
        homePresenter.askLocationPermission(false,
                false);

        // Check whether loadEmptyParkingBays is called.
        verify(remoteDataSource)
                .getEmptyParkingBays(loadEmptyParkingBaysCallBackArgumentCaptor.capture());
    }

    /**
     * TODO: Verify askLocationPermission() is being called.
     */
    @Test
    public void askLocationSetting_onSatisfyLocationSetting_askLocationPermission() {
        LocationSettingsResponse locationSettingsResponse = new LocationSettingsResponse();

        homePresenter.askLocationSetting();

        // Callback is captured.
        verify(locationService).getLocationSettingResponse(
                getLocationSettingResponseCallbackArgumentCaptor.capture());
        getLocationSettingResponseCallbackArgumentCaptor.getValue()
                .onSatisfyLocationSetting(locationSettingsResponse);
    }

    @Test
    public void askLocationSetting_onNotSatisfyLocationSetting_showLocationSettingDialog() {
        homePresenter.askLocationSetting();

        // Callback is captured and invoked with exception.
        verify(locationService).getLocationSettingResponse(
                getLocationSettingResponseCallbackArgumentCaptor.capture());
        getLocationSettingResponseCallbackArgumentCaptor.getValue().onNotSatisfyLocationSetting(e);

        // Check whether showLocationSettingDialog is called.
        verify(homeView).showLocationSettingDialog(e);
    }

    @Test
    public void requestDistanceMatrix_lastKnownLocationIsReceived_getDistanceMatrixResponse() {

        String originLatLng = "1,1";
        String destinationLatLng = "2,2";
        double rate = 0.80;

        homePresenter.requestDistanceMatrix(destinationLatLng, rate);

        // Check whether showProgressBar with true value is called.
        verify(homeView).showProgressBar(true);

        // Callback is captured and invoked with stubbed originLatLng.
        verify(locationService).getLastKnownLocationResponse
                (getLastKnownLocationResponseCallbackArgumentCaptor.capture());
        getLastKnownLocationResponseCallbackArgumentCaptor.getValue()
                .onLastKnownLocationReceived(originLatLng);

        // Check whether getDistanceMatrixResponse is called.
        verify(distanceMatrixService)
                .getDistanceMatrixResponse(anyString(),
                        anyString(),
                        getDistanceMatrixResponseCallbackArgumentCaptor.capture());
    }

    @Test
    public void getDistanceMatrixResponse_onDistanceAndDurationReceived_hideProgressbarSetAndShowDistanceDurationRate() {
        String originLatLng = "1,1";
        String destinationLatLng = "2,2";
        String distance = "3.6 km";
        String duration = "6 mins";

        homePresenter.getDistanceMatrixResponse(originLatLng, destinationLatLng, rate);

        // Callback is captured and invoked with stubbed distance and duration.
        verify(distanceMatrixService).getDistanceMatrixResponse(anyString(),
                anyString(),
                getDistanceMatrixResponseCallbackArgumentCaptor.capture());
        getDistanceMatrixResponseCallbackArgumentCaptor.getValue()
                .onDistanceAndDurationReceived(distance, duration);

        // Check whether showProgressBar with false value, setDistanceDurationAndRate and
        // showProgressBar with true value is called.
        verify(homeView).showProgressBar(false);
        verify(homeView).setDistanceDurationAndRate(distance, duration, rate);
        verify(homeView).showDistanceDurationAndRate(true);
    }

    @Test
    public void getDistanceMatrixResponse_onNoInternet_hideProgressbarAndHideDistanceDurationRateButShowDistanceDurationCalculationErrMsg() {
        String originLatLng = "1,1";
        String destinationLatLng = "2,2";

        homePresenter.getDistanceMatrixResponse(originLatLng, destinationLatLng, rate);

        // Callback is captured.
        verify(distanceMatrixService).getDistanceMatrixResponse(anyString(),
                anyString(),
                getDistanceMatrixResponseCallbackArgumentCaptor.capture());
        getDistanceMatrixResponseCallbackArgumentCaptor.getValue().onNoInternet();

        // Check whether showProgressBar with false value, setDistanceDurationAndRate with false
        // value and showDistanceDurationCalculationErrMsg is called.
        verify(homeView).showProgressBar(false);
        verify(homeView).showDistanceDurationAndRate(false);
        verify(homeView).showDistanceDurationCalculationErrMsg();
    }

    @Test
    public void requestDistanceMatrix_lastKnownLocationIsNull_processOnLastKnowLocationIsNullView() {

        String destinationLatLng = "2,2";

        homePresenter.requestDistanceMatrix(destinationLatLng, rate);

        verify(homeView).showProgressBar(true);

        verify(locationService).getLastKnownLocationResponse
                (getLastKnownLocationResponseCallbackArgumentCaptor.capture());
        getLastKnownLocationResponseCallbackArgumentCaptor.getValue().onLastKnowLocationIsNull();
    }

    @Test
    public void createLocationCallback_newLocationCallback() {
        homePresenter.createLocationCallback();
        verify(locationService).newLocationCallback();
    }

    @Test
    public void startLocationUpdate_requestLocationUpdates() {
        homePresenter.startLocationUpdate();
        verify(locationService).requestLocationUpdates();
    }

    @Test
    public void stopLocationUpdate_removeLocationUpdates() {
        homePresenter.stopLocationUpdate();
        verify(locationService).removeLocationUpdates();
    }

    @Test
    public void getConnectivityStatus_isConnected() {
        homePresenter.getConnectivityStatus();
        verify(connectivityService).isConnected();
    }

    /**
     * TODO: Verify askLocationSetting is being called.
     */
    @Test
    public void getConnectivityStatusResponse_onInternetAvailableReceived_askLocationSetting() {


        homePresenter.checkConnectivity();

        // Callback is captured.
        verify(connectivityService).getConnectivityStatusResponse
                (getConnectivityStatusResponseCallbackArgumentCaptor.capture());
        getConnectivityStatusResponseCallbackArgumentCaptor.getValue().onInternetAvailableReceived();


    }

    @Test
    public void getConnectivityStatusResponse_onInternetUnavailable_showConnectivityErrMsg() {

        homePresenter.checkConnectivity();

        // Callback is captured.
        verify(connectivityService).getConnectivityStatusResponse
                (getConnectivityStatusResponseCallbackArgumentCaptor.capture());
        getConnectivityStatusResponseCallbackArgumentCaptor.getValue().onInternetUnavailable();

        homeView.showConnectivityErrMsg();
    }

    @Test
    public void hideDistanceDurationRateTextviewAndProgressbar_showProgressBarAndshowDistanceDurationAndRateValueIsFalse() {
        homePresenter.hideDistanceDurationRateTextviewAndProgressbar();

        verify(homeView).showProgressBar(false);
        verify(homeView).showDistanceDurationAndRate(false);
    }

    @Test
    public void processOnLastKnowLocationIsNullView_internetIsConnected_showProgressbarSetRateAndDefaultDistanceDurationShowDistanceDurationAndRateShowGettingLocationMsg() {
        homePresenter.processOnLastKnowLocationIsNullView(true, rate);
        verify(homeView).showProgressBar(false);
        verify(homeView).setRateAndDefaultDistanceDuration(rate);
        verify(homeView).showDistanceDurationAndRate(true);
        verify(homeView).showGettingLocationMsg();
    }

    @Test
    public void processOnLastKnowLocationIsNullView_noInternet_showProgressbarHideDistanceDurationAndRateshowConnectivityAndLocationErrMsg() {
        homePresenter.processOnLastKnowLocationIsNullView(false, rate);
        verify(homeView).showProgressBar(false);
        verify(homeView).showDistanceDurationAndRate(false);
        homeView.showConnectivityAndLocationErrMsg();
    }

    @Test
    public void filterEmptyParkingBays_oneOfTwoParkingBaysIsEmpty_returnEmptyParkingBaysSizeIsOne() {
        List<EmptyParkingBay> temp = homePresenter.filterEmptyParkingBays(filterEmptyParkingBayList);
        assertThat(temp.size(), is(1));
    }

    @Test
    public void selectCar_showAddRemoveCarUi() {
        homePresenter.selectCar();

        verify(homeView).showAddRemoveCarUi();
    }

    @Test
    public void result_REQUEST_ADD_REMOVE_CARAndRESULT_OK_showSelectedCar() {
        homePresenter.result(REQUEST_ADD_REMOVE_CAR, RESULT_OK);
        verify(homeView).showSelectedCar();
    }

    @Test
    public void selectDuration_showDurationOptionDialog() {
        homePresenter.selectDuration();

        verify(homeView).showDurationOptionDialog();
    }

    @Test
    public void checkValidCarNumberPlateAndDuration_emptyCarNumberPlate_showCarNumberPlateErrMsgAndReturnFalse() {
        // Empty carNumberPlate
        String carNumberPlate = "";
        int duration = 1;
        boolean valid;

        valid = homePresenter.checkValidCarNumberPlateAndDuration(carNumberPlate, duration);

        verify(homeView).showCarNumberPlateErrMsg();

        assertThat(false, is(valid));
    }

    @Test
    public void checkValidCarNumberPlateAndDuration_durationIsZero_showDurationErrMsgAndReturnFalse() {
        String carNumberPlate = "QWE1234";
        int duration = 0;
        boolean valid;

        valid = homePresenter.checkValidCarNumberPlateAndDuration(carNumberPlate, duration);

        verify(homeView).showDurationErrMsg();

        assertThat(false, is(valid));
    }

    @Test
    public void checkValidCarNumberPlateAndDuration_filledCarNumberPlateAndDuration_returnTrue() {
        String carNumberPlate = "QWE1234";
        int duration = 1;
        boolean valid;

        valid = homePresenter.checkValidCarNumberPlateAndDuration(carNumberPlate, duration);

        assertThat(true, is(valid));
    }

    @Test
    public void determinePayment_oneHour_returnZeroPointFourFive() {
        int duration = 1;
        double payment;

        payment = homePresenter.determinePayment(duration);

        assertThat(0.45, is(payment));
    }

    @Test
    public void determinePayment_oneDay_returnThreePointFive() {
        int duration = 9;
        double payment;

        payment = homePresenter.determinePayment(duration);

        assertThat(3.50, is(payment));
    }

    @Test
    public void determinePayment_zero_returnZero() {
        int duration = 0;
        double payment;

        payment = homePresenter.determinePayment(duration);

        assertThat(0.0, is(payment));
    }
}