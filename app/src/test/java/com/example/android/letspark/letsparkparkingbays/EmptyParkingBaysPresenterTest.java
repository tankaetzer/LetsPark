package com.example.android.letspark.letsparkparkingbays;

import android.test.suitebuilder.annotation.SmallTest;

import com.example.android.letspark.data.EmptyParkingBay;
import com.example.android.letspark.data.EmptyParkingBaysDataSource;
import com.example.android.letspark.data.EmptyParkingBaysRemoteDataSource;
import com.example.android.letspark.service.ConnectivityService;
import com.example.android.letspark.service.DistanceMatrixService;
import com.example.android.letspark.service.LocationService;
import com.example.android.letspark.service.Service;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of EmptyParkingBaysPresenter.
 */
@SmallTest
public class EmptyParkingBaysPresenterTest {

    private static List<EmptyParkingBay> emptyParkingBayList;

    @Mock
    private EmptyParkingBaysContract.View emptyParkingBaysView;

    @Mock
    private EmptyParkingBaysRemoteDataSource emptyParkingBaysRemoteDataSource;

    @Mock
    private LocationService locationService;

    @Mock
    private DistanceMatrixService distanceMatrixService;

    @Mock
    private ConnectivityService connectivityService;

    private EmptyParkingBaysPresenter emptyParkingBaysPresenter;

    @Captor
    private ArgumentCaptor<EmptyParkingBaysDataSource.LoadEmptyParkingBaysCallBack>
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

    private Exception e;

    private double rate = 0.80;

    @Before
    public void setup() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test.
        emptyParkingBaysPresenter = new EmptyParkingBaysPresenter(emptyParkingBaysRemoteDataSource,
                emptyParkingBaysView, locationService, distanceMatrixService, connectivityService);

        // Add 2 empty parking bays into list.
        emptyParkingBayList = Lists.newArrayList(
                new EmptyParkingBay(1, 1, "KK3-1", 0.7),
                new EmptyParkingBay(2, 2, "KK3-2", 0.6));
    }

    @Test
    public void createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test.
        emptyParkingBaysPresenter = new EmptyParkingBaysPresenter(emptyParkingBaysRemoteDataSource,
                emptyParkingBaysView, locationService, distanceMatrixService, connectivityService);

        // Then the presenter is set to the view.
        verify(emptyParkingBaysView).setPresenter(emptyParkingBaysPresenter);
    }

    @Test
    public void loadAllTasksFromRemoteDataSourceAndLoadIntoView_firesOnEmptyParkingBaysLoaded() {

        emptyParkingBaysPresenter.loadEmptyParkingBays();

        // Callback is captured and invoked with stubbed emptyParkingBay.
        verify(emptyParkingBaysRemoteDataSource).getEmptyParkingBays(
                loadEmptyParkingBaysCallBackArgumentCaptor.capture());
        loadEmptyParkingBaysCallBackArgumentCaptor.getValue()
                .onEmptyParkingBaysLoaded(emptyParkingBayList);

        // Check whether showEmptyParkingBays is called.
        verify(emptyParkingBaysView).showEmptyParkingBays(emptyParkingBayList);
    }

    @Test
    public void loadAllTasksFromRemoteDataSourceAndLoadIntoView_firesOnDataNotAvailable() {

        emptyParkingBaysPresenter.loadEmptyParkingBays();

        // Callback is captured.
        verify(emptyParkingBaysRemoteDataSource).getEmptyParkingBays(
                loadEmptyParkingBaysCallBackArgumentCaptor.capture());
        loadEmptyParkingBaysCallBackArgumentCaptor.getValue().onDataNotAvailable();

        // Check whether showEmptyParkingBays is called.
        verify(emptyParkingBaysView).showLoadingEmptyParkingBaysError();
    }

    @Test
    public void askLocationPermission_notGrantedIsTrue_showRequestPermissionRationaleIsTrue_showLocationErrMsgWithAction() {
        emptyParkingBaysPresenter.askLocationPermission(true,
                true);

        // Check whether showLocationErrMsgWithAction is called.
        verify(emptyParkingBaysView).showLocationErrMsgWithAction();
    }

    @Test
    public void askLocationPermission_notGrantedIsTrue_showRequestPermissionRationaleIsFalse_requestLocationPermission() {
        emptyParkingBaysPresenter.askLocationPermission(true,
                false);

        // Check whether requestLocationPermissions is called.
        verify(emptyParkingBaysView).requestLocationPermissions();
    }

    @Test
    public void askLocationPermission_notGrantedIsFalse_getEmptyParkingBays() {
        emptyParkingBaysPresenter.askLocationPermission(false,
                false);

        // Check whether loadEmptyParkingBays is called.
        verify(emptyParkingBaysRemoteDataSource)
                .getEmptyParkingBays(loadEmptyParkingBaysCallBackArgumentCaptor.capture());
    }

    /**
     * TODO: Verify askLocationPermission() is being called.
     */
    @Test
    public void askLocationSetting_onSatisfyLocationSetting_askLocationPermission() {
        LocationSettingsResponse locationSettingsResponse = new LocationSettingsResponse();

        emptyParkingBaysPresenter.askLocationSetting();

        // Callback is captured.
        verify(locationService).getLocationSettingResponse(
                getLocationSettingResponseCallbackArgumentCaptor.capture());
        getLocationSettingResponseCallbackArgumentCaptor.getValue()
                .onSatisfyLocationSetting(locationSettingsResponse);
    }

    @Test
    public void askLocationSetting_onNotSatisfyLocationSetting_showLocationSettingDialog() {
        emptyParkingBaysPresenter.askLocationSetting();

        // Callback is captured and invoked with exception.
        verify(locationService).getLocationSettingResponse(
                getLocationSettingResponseCallbackArgumentCaptor.capture());
        getLocationSettingResponseCallbackArgumentCaptor.getValue().onNotSatisfyLocationSetting(e);

        // Check whether showLocationSettingDialog is called.
        verify(emptyParkingBaysView).showLocationSettingDialog(e);
    }

    @Test
    public void requestDistanceMatrix_lastKnownLocationIsReceived_getDistanceMatrixResponse() {

        String originLatLng = "1,1";
        String destinationLatLng = "2,2";
        double rate = 0.80;

        emptyParkingBaysPresenter.requestDistanceMatrix(destinationLatLng, rate);

        // Check whether showProgressBar with true value is called.
        verify(emptyParkingBaysView).showProgressBar(true);

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

        emptyParkingBaysPresenter.getDistanceMatrixResponse(originLatLng, destinationLatLng, rate);

        // Callback is captured and invoked with stubbed distance and duration.
        verify(distanceMatrixService).getDistanceMatrixResponse(anyString(),
                anyString(),
                getDistanceMatrixResponseCallbackArgumentCaptor.capture());
        getDistanceMatrixResponseCallbackArgumentCaptor.getValue()
                .onDistanceAndDurationReceived(distance, duration);

        // Check whether showProgressBar with false value, setDistanceDurationAndRate and
        // showProgressBar with true value is called.
        verify(emptyParkingBaysView).showProgressBar(false);
        verify(emptyParkingBaysView).setDistanceDurationAndRate(distance, duration, rate);
        verify(emptyParkingBaysView).showDistanceDurationAndRate(true);
    }

    @Test
    public void getDistanceMatrixResponse_onNoInternet_hideProgressbarAndHideDistanceDurationRateButShowDistanceDurationCalculationErrMsg() {
        String originLatLng = "1,1";
        String destinationLatLng = "2,2";

        emptyParkingBaysPresenter.getDistanceMatrixResponse(originLatLng, destinationLatLng, rate);

        // Callback is captured.
        verify(distanceMatrixService).getDistanceMatrixResponse(anyString(),
                anyString(),
                getDistanceMatrixResponseCallbackArgumentCaptor.capture());
        getDistanceMatrixResponseCallbackArgumentCaptor.getValue().onNoInternet();

        // Check whether showProgressBar with false value, setDistanceDurationAndRate with false
        // value and showDistanceDurationCalculationErrMsg is called.
        verify(emptyParkingBaysView).showProgressBar(false);
        verify(emptyParkingBaysView).showDistanceDurationAndRate(false);
        verify(emptyParkingBaysView).showDistanceDurationCalculationErrMsg();
    }

    @Test
    public void requestDistanceMatrix_lastKnownLocationIsNull_processOnLastKnowLocationIsNullView() {

        String destinationLatLng = "2,2";

        emptyParkingBaysPresenter.requestDistanceMatrix(destinationLatLng, rate);

        verify(emptyParkingBaysView).showProgressBar(true);

        verify(locationService).getLastKnownLocationResponse
                (getLastKnownLocationResponseCallbackArgumentCaptor.capture());
        getLastKnownLocationResponseCallbackArgumentCaptor.getValue().onLastKnowLocationIsNull();
    }

    @Test
    public void createLocationCallback_newLocationCallback() {
        emptyParkingBaysPresenter.createLocationCallback();
        verify(locationService).newLocationCallback();
    }

    @Test
    public void startLocationUpdate_requestLocationUpdates() {
        emptyParkingBaysPresenter.startLocationUpdate();
        verify(locationService).requestLocationUpdates();
    }

    @Test
    public void stopLocationUpdate_removeLocationUpdates() {
        emptyParkingBaysPresenter.stopLocationUpdate();
        verify(locationService).removeLocationUpdates();
    }

    @Test
    public void getConnectivityStatus_isConnected() {
        emptyParkingBaysPresenter.getConnectivityStatus();
        verify(connectivityService).isConnected();
    }

    /**
     * TODO: Verify askLocationSetting is being called.
     */
    @Test
    public void getConnectivityStatusResponse_onInternetAvailableReceived_askLocationSetting() {


        emptyParkingBaysPresenter.checkConnectivity();

        // Callback is captured.
        verify(connectivityService).getConnectivityStatusResponse
                (getConnectivityStatusResponseCallbackArgumentCaptor.capture());
        getConnectivityStatusResponseCallbackArgumentCaptor.getValue().onInternetAvailableReceived();


    }

    @Test
    public void getConnectivityStatusResponse_onInternetUnavailable_showConnectivityErrMsg() {

        emptyParkingBaysPresenter.checkConnectivity();

        // Callback is captured.
        verify(connectivityService).getConnectivityStatusResponse
                (getConnectivityStatusResponseCallbackArgumentCaptor.capture());
        getConnectivityStatusResponseCallbackArgumentCaptor.getValue().onInternetUnavailable();

        emptyParkingBaysView.showConnectivityErrMsg();
    }

    @Test
    public void hideDistanceDurationRateTextviewAndProgressbar_showProgressBarAndshowDistanceDurationAndRateValueIsFalse() {
        emptyParkingBaysPresenter.hideDistanceDurationRateTextviewAndProgressbar();

        verify(emptyParkingBaysView).showProgressBar(false);
        verify(emptyParkingBaysView).showDistanceDurationAndRate(false);
    }

    @Test
    public void processOnLastKnowLocationIsNullView_internetIsConnected_showProgressbarSetRateAndDefaultDistanceDurationShowDistanceDurationAndRateShowGettingLocationMsg() {
        emptyParkingBaysPresenter.processOnLastKnowLocationIsNullView(true, rate);
        verify(emptyParkingBaysView).showProgressBar(false);
        verify(emptyParkingBaysView).setRateAndDefaultDistanceDuration(rate);
        verify(emptyParkingBaysView).showDistanceDurationAndRate(true);
        verify(emptyParkingBaysView).showGettingLocationMsg();
    }

    @Test
    public void processOnLastKnowLocationIsNullView_noInternet_showProgressbarHideDistanceDurationAndRateshowConnectivityAndLocationErrMsg() {
        emptyParkingBaysPresenter.processOnLastKnowLocationIsNullView(false, rate);
        verify(emptyParkingBaysView).showProgressBar(false);
        verify(emptyParkingBaysView).showDistanceDurationAndRate(false);
        emptyParkingBaysView.showConnectivityAndLocationErrMsg();
    }
}
