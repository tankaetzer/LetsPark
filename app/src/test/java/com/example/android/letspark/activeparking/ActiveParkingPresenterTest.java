package com.example.android.letspark.activeparking;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.data.model.ActiveParking;
import com.example.android.letspark.service.CountDownTimerService;
import com.example.android.letspark.service.Service;
import com.example.android.letspark.service.SharedPreferenceService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.HashMap;
import java.util.Map;

import static com.example.android.letspark.activeparking.ActiveParkingFragment.TYPE_EXPIRED;
import static com.example.android.letspark.activeparking.ActiveParkingFragment.TYPE_EXPIRING;
import static com.example.android.letspark.utility.TimeUnitUtils.MAP_HOUR;
import static com.example.android.letspark.utility.TimeUnitUtils.MAP_MINUTE;
import static com.example.android.letspark.utility.TimeUnitUtils.MAP_SECOND;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of ActiveParkingPresenter.
 */
public class ActiveParkingPresenterTest {

    String errMsg = "error";
    @Mock
    private ActiveParkingContract.View activeParkingView;
    @Mock
    private RemoteDataSource remoteDataSource;
    @Mock
    private SharedPreferenceService sharedPreferenceService;
    @Mock
    private CountDownTimerService countDownTimerService;
    @Captor
    private ArgumentCaptor<Service.SharedPreferenceService.GetCurrentUserUidCallback>
            getCurrentUserUidCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<DataSource.GetCurrentUnixTimeCallback>
            getCurrentUnixTimeCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<DataSource.GetActiveParkingCallback>
            getActiveParkingCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<Service.CountDownTimerService.GetServiceCallback>
            getServiceCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<DataSource.UpdateTimerRunningCallback>
            updateTimerRunningCallbackArgumentCaptor;
    @Captor
    private ArgumentCaptor<DataSource.UpdateTimeLeftTimerRunningEndTimeCallback>
            updateTimeLeftTimerRunningEndTimeCallbackArgumentCaptor;
    private ActiveParkingPresenter activeParkingPresenter;
    private String uid = "qwer";
    private String carNumberPlate = "QWER1234";
    private ActiveParking activeParking;
    boolean activeParkingExist = false;
    private double madePayment = 0;
    private Map<String, Object> time;

    @Before
    public void setup() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        activeParkingPresenter = new ActiveParkingPresenter(madePayment, activeParkingExist,
                activeParkingView, remoteDataSource, sharedPreferenceService, countDownTimerService);

        activeParking = new ActiveParking("QWER1234", "Kuantan",
                Long.parseLong("1543765156002"), Long.parseLong("3600000"),
                Long.parseLong("1543768756002"), Long.parseLong("3570121"), true,
                "xxxx", 0.45);
    }

    @Test
    public void createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test.
        activeParkingPresenter = new ActiveParkingPresenter(madePayment, activeParkingExist,
                activeParkingView, remoteDataSource, sharedPreferenceService, countDownTimerService);

        // Then the presenter is set to the view.
        verify(activeParkingView).setPresenter(activeParkingPresenter);
    }

    @Test
    public void start_onGetActiveParking_showProgressBarFalseAndshowNoActiveParkingViewFalseshowCountDownTime() {
        time = new HashMap<>();
        time.put(MAP_HOUR, 1L);
        time.put(MAP_MINUTE, 0L);
        time.put(MAP_SECOND, 0L);

        long currentUnixTime = 1543802311;

        activeParkingPresenter.start();

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(remoteDataSource).getActiveParking(anyString(), getActiveParkingCallbackArgumentCaptor.capture());

        getActiveParkingCallbackArgumentCaptor.getValue().onGetActiveParking(activeParking);

        verify(activeParkingView).showProgressBar(false);
        verify(activeParkingView).showNoActiveParkingView(false);
        verify(activeParkingView).showCountDownTime(time);

        verify(remoteDataSource).getCurrentUnixTime(getCurrentUnixTimeCallbackArgumentCaptor.capture());

        getCurrentUnixTimeCallbackArgumentCaptor.getValue().onGetCurrentUnixTime(currentUnixTime);

        verify(activeParkingView).showNoActiveParkingView(false);
        verify(activeParkingView).showCountDownTime(time);
    }

    @Test
    public void checkActiveParkingExist_onGetUidAndonGetActiveParkingAndTimerIsRunning_start() {
        activeParkingPresenter.checkActiveParkingExist();

        verify(sharedPreferenceService).getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(remoteDataSource).getActiveParking(anyString(), getActiveParkingCallbackArgumentCaptor.capture());

        getActiveParkingCallbackArgumentCaptor.getValue().onGetActiveParking(activeParking);
    }

    @Test
    public void checkActiveParkingExist_onGetUidonGetActiveParkingAndTimerIsNotRunning_showNoActiveParkingView() {
        activeParking = new ActiveParking("QWER1234", "Kuantan",
                Long.parseLong("1543765156002"), Long.parseLong("3600000"),
                Long.parseLong("1543768756002"), Long.parseLong("3570121"), false,
                "xxxx", 0.45);

        activeParkingPresenter.checkActiveParkingExist();

        verify(sharedPreferenceService).getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(remoteDataSource).getActiveParking(anyString(), getActiveParkingCallbackArgumentCaptor.capture());

        getActiveParkingCallbackArgumentCaptor.getValue().onGetActiveParking(activeParking);

        verify(activeParkingView).showNoActiveParkingView(true);
    }

    @Test
    public void checkActiveParkingExist_onCancelled_showDbErrMsg() {
        activeParkingPresenter.checkActiveParkingExist();

        verify(sharedPreferenceService).getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(remoteDataSource).getActiveParking(anyString(), getActiveParkingCallbackArgumentCaptor.capture());

        getActiveParkingCallbackArgumentCaptor.getValue().onCancelled(errMsg);

        verify(activeParkingView).showDbErrMsg(errMsg);
    }

    @Test
    public void stop_onSuccess_stopService() {
        activeParkingPresenter.stop();

        verify(sharedPreferenceService).getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(remoteDataSource).updateTimeLeftTimerRunningEndTime(anyString(),
                anyLong(),
                anyBoolean(),
                updateTimeLeftTimerRunningEndTimeCallbackArgumentCaptor.capture());

        updateTimeLeftTimerRunningEndTimeCallbackArgumentCaptor.getValue().onSuccess();

        verify(countDownTimerService).stopService();
    }

    @Test
    public void stop_onFailure_showDbErrMsg() {
        activeParkingPresenter.stop();

        verify(sharedPreferenceService).getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(remoteDataSource).updateTimeLeftTimerRunningEndTime(anyString(),
                anyLong(),
                anyBoolean(),
                updateTimeLeftTimerRunningEndTimeCallbackArgumentCaptor.capture());

        updateTimeLeftTimerRunningEndTimeCallbackArgumentCaptor.getValue().onFailure(errMsg);

        verify(activeParkingView).showDbErrMsg(errMsg);
    }

    @Test
    public void startTimer_onTick_MillisUntilFinishedIs10300AndshowExpiringNotificationAndshowNoActiveParkingViewAndshowCountDownTime() {
        long millisUntilFinished = 10300;

        time = new HashMap<>();
        time.put(MAP_HOUR, 0L);
        time.put(MAP_MINUTE, 0L);
        time.put(MAP_SECOND, 10L);

        activeParkingPresenter.startTimer();

        verify(countDownTimerService).startService(anyLong(), getServiceCallbackArgumentCaptor.capture());

        getServiceCallbackArgumentCaptor.getValue().onTick(millisUntilFinished);

        verify(activeParkingView).showExpiringOrExpiredNotification(carNumberPlate, TYPE_EXPIRING);

        verify(activeParkingView).showNoActiveParkingView(false);

        verify(activeParkingView).showCountDownTime(time);
    }

    @Test
    public void startTimer_onTick_MillisUntilFinishedIs1500AndshowExpiredNotificationAndshowNoActiveParkingViewAndshowCountDownTime() {
        long millisUntilFinished = 1500;

        time = new HashMap<>();
        time.put(MAP_HOUR, 0L);
        time.put(MAP_MINUTE, 0L);
        time.put(MAP_SECOND, 1L);

        activeParkingPresenter.startTimer();

        verify(countDownTimerService).startService(anyLong(), getServiceCallbackArgumentCaptor.capture());

        getServiceCallbackArgumentCaptor.getValue().onTick(millisUntilFinished);

        verify(activeParkingView).showExpiringOrExpiredNotification(carNumberPlate, TYPE_EXPIRED);

        verify(activeParkingView).showNoActiveParkingView(false);

        verify(activeParkingView).showCountDownTime(time);
    }

    @Test
    public void startTimer_onFinishAndOnSuccess_showProgressBarFalseAndshowNoActiveParkingViewTrue() {
        activeParkingPresenter.startTimer();

        verify(countDownTimerService).startService(anyLong(), getServiceCallbackArgumentCaptor.capture());

        getServiceCallbackArgumentCaptor.getValue().onFinish();

        verify(activeParkingView).showProgressBar(true);

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(remoteDataSource).updateTimerRunning(anyString(),
                anyBoolean(), updateTimerRunningCallbackArgumentCaptor.capture());

        updateTimerRunningCallbackArgumentCaptor.getValue().onSuccess();

        verify(activeParkingView).showProgressBar(false);

        verify(activeParkingView).showNoActiveParkingView(true);
    }

    @Test
    public void startTimer_onFinishAndOnFailure_showProgressBarFalseAndshowNoActiveParkingViewTrueAndShowDbErrMsg() {
        activeParkingPresenter.startTimer();

        verify(countDownTimerService).startService(anyLong(), getServiceCallbackArgumentCaptor.capture());

        getServiceCallbackArgumentCaptor.getValue().onFinish();

        verify(activeParkingView).showProgressBar(true);

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(remoteDataSource).updateTimerRunning(anyString(),
                anyBoolean(), updateTimerRunningCallbackArgumentCaptor.capture());

        updateTimerRunningCallbackArgumentCaptor.getValue().onFailure(errMsg);

        verify(activeParkingView).showProgressBar(false);

        verify(activeParkingView).showNoActiveParkingView(true);

        verify(activeParkingView).showDbErrMsg(errMsg);
    }
}
