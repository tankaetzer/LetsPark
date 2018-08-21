package com.example.android.letspark.letsparkparkingbays;

import com.example.android.letspark.data.EmptyParkingBay;
import com.example.android.letspark.data.EmptyParkingBaysDataSource;
import com.example.android.letspark.data.EmptyParkingBaysRemoteDataSource;
import com.example.android.letspark.service.Service;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of EmptyParkingBaysPresenter.
 */
public class EmptyParkingBaysPresenterTest {

    private static List<EmptyParkingBay> emptyParkingBayList;

    @Mock
    private EmptyParkingBaysContract.View emptyParkingBaysView;

    @Mock
    private EmptyParkingBaysRemoteDataSource emptyParkingBaysRemoteDataSource;

    @Mock
    private Service locationService;

    private EmptyParkingBaysPresenter emptyParkingBaysPresenter;

    @Captor
    private ArgumentCaptor<EmptyParkingBaysDataSource.LoadEmptyParkingBaysCallBack>
            loadEmptyParkingBaysCallBackArgumentCaptor;

    @Captor
    private ArgumentCaptor<Service.getLocationSettingResponseCallback>
            getLocationSettingResponseCallbackArgumentCaptor;

    private Exception e;

    @Before
    public void setup() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        // Get a reference to the class under test.
        emptyParkingBaysPresenter = new EmptyParkingBaysPresenter(emptyParkingBaysRemoteDataSource,
                emptyParkingBaysView, locationService);

        // Add 2 empty parking bays into list.
        emptyParkingBayList = Lists.newArrayList(new EmptyParkingBay(1, 1, "KK3-1"),
                new EmptyParkingBay(2, 2, "KK3-2"));
    }

    @Test
    public void createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test.
        emptyParkingBaysPresenter = new EmptyParkingBaysPresenter(emptyParkingBaysRemoteDataSource,
                emptyParkingBaysView, locationService);

        // Then the presenter is set to the view.
        verify(emptyParkingBaysView).setPresenter(emptyParkingBaysPresenter);
    }

    @Test
    public void loadAllTasksFromRemoteDataSourceAndLoadIntoView_firesOnEmptyParkingBaysLoaded() {
        emptyParkingBaysPresenter.start();

        // Callback is captured and invoked with stubbed task.
        verify(emptyParkingBaysRemoteDataSource).getEmptyParkingBays(
                loadEmptyParkingBaysCallBackArgumentCaptor.capture());
        loadEmptyParkingBaysCallBackArgumentCaptor.getValue().onEmptyParkingBaysLoaded(emptyParkingBayList);

        // Check whether showEmptyParkingBays is called.
        verify(emptyParkingBaysView).showEmptyParkingBays(emptyParkingBayList);
    }

    @Test
    public void loadAllTasksFromRemoteDataSourceAndLoadIntoView_firesOnDataNotAvailable() {
        emptyParkingBaysPresenter.start();

        // Callback is captured and invoked with stubbed task.
        verify(emptyParkingBaysRemoteDataSource).getEmptyParkingBays(
                loadEmptyParkingBaysCallBackArgumentCaptor.capture());
        loadEmptyParkingBaysCallBackArgumentCaptor.getValue().onDataNotAvailable();

        // Check whether showEmptyParkingBays is called.
        verify(emptyParkingBaysView).showLoadingEmptyParkingBaysError();
    }

    @Test
    public void askLocationPermission_notGrantedIsTrue_showRequestPermissionRationaleIsTrue_showErrorMessageWithAction() {
        emptyParkingBaysPresenter.askLocationPermission(true,
                true);

        // Check whether showErrorMessageWithAction is called.
        verify(emptyParkingBaysView).showErrorMessageWithAction();
    }

    @Test
    public void askLocationPermission_notGrantedIsTrue_showRequestPermissionRationaleIsFalse_requestLocationPermission() {
        emptyParkingBaysPresenter.askLocationPermission(true,
                false);

        // Check whether requestLocationPermissions is called.
        verify(emptyParkingBaysView).requestLocationPermissions();
    }

    @Test
    public void askLocationSetting_onSatisfyLocationSetting_askLocationPermission() {

        emptyParkingBaysPresenter.askLocationSetting();

        verify(locationService).createLocationRequest();

        verify(locationService).setBuilder();

        verify(locationService).checkCurrentLocationSetting();

        // Callback is captured and invoked with stubbed task.
        verify(locationService).getLocationSettingResponse(
                getLocationSettingResponseCallbackArgumentCaptor.capture());
        getLocationSettingResponseCallbackArgumentCaptor.getValue().onSatisfyLocationSetting();
    }

    @Test
    public void askLocationSetting_onNotSatisfyLocationSetting_showLocationSettingDialog() {

        emptyParkingBaysPresenter.askLocationSetting();

        verify(locationService).createLocationRequest();

        verify(locationService).setBuilder();

        verify(locationService).checkCurrentLocationSetting();

        // Callback is captured and invoked with stubbed task.
        verify(locationService).getLocationSettingResponse(
                getLocationSettingResponseCallbackArgumentCaptor.capture());
        getLocationSettingResponseCallbackArgumentCaptor.getValue().onNotSatisfyLocationSetting(e);

        // Check whether showLocationSettingDialog is called.
        verify(emptyParkingBaysView).showLocationSettingDialog(e);
    }
}
