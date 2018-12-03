package com.example.android.letspark.activeparking;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.data.model.ActiveParking;
import com.example.android.letspark.service.CountDownTimerService;
import com.example.android.letspark.service.Service;
import com.example.android.letspark.service.SharedPreferenceService;

import java.util.Map;

import static com.example.android.letspark.activeparking.ActiveParkingFragment.TYPE_EXPIRED;
import static com.example.android.letspark.activeparking.ActiveParkingFragment.TYPE_EXPIRING;
import static com.example.android.letspark.utility.DateUtils.convertAndFormatUnixTimeToTime;
import static com.example.android.letspark.utility.TimeUnitUtils.getHourMinuteSecond;
import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Listens to user actions from the UI (ActiveParkingFragment), retrieves the data and updates
 * the UI as required.
 */
public class ActiveParkingPresenter implements ActiveParkingContract.Presenter {

    private ActiveParkingContract.View activeParkingView;

    private RemoteDataSource remoteDataSource;

    private SharedPreferenceService sharedPreferenceService;

    private CountDownTimerService countDownTimerService;

    private boolean timerRunning;

    private long timeLeft;

    private long endTime;

    private long startTime;

    private String location;

    private String carNumberPlate = "QWER1234";

    public ActiveParkingPresenter(ActiveParkingContract.View activeParkingView,
                                  RemoteDataSource remoteDataSource,
                                  SharedPreferenceService sharedPreferenceService,
                                  CountDownTimerService countDownTimerService) {
        this.activeParkingView = checkNotNull(activeParkingView);
        this.remoteDataSource = checkNotNull(remoteDataSource);
        this.sharedPreferenceService = checkNotNull(sharedPreferenceService);
        this.countDownTimerService = checkNotNull(countDownTimerService);
        activeParkingView.setPresenter(this);
    }

    @Override
    public void start() {
        activeParkingView.showProgressBar(true);
        sharedPreferenceService.getCurrentUserUid(new Service.SharedPreferenceService
                .GetCurrentUserUidCallback() {
            @Override
            public void onGetUid(String uid) {
                remoteDataSource.getActiveParking(uid, new DataSource.GetActiveParkingCallback() {
                    @Override
                    public void onGetActiveParking(ActiveParking activeParking) {
                        activeParkingView.showProgressBar(false);
                        timeLeft = activeParking.getDuration();
                        startTime = activeParking.getStartTime();
                        endTime = activeParking.getEndTime();
                        timerRunning = activeParking.getTimerRunning();
                        location = activeParking.getLocation();
                        carNumberPlate = activeParking.getCarNumberPlate();
                        Map<String, Object> time = getHourMinuteSecond(timeLeft);
                        String strStartTime = convertAndFormatUnixTimeToTime(startTime);
                        String strEndTime = convertAndFormatUnixTimeToTime(endTime);
                        String period = strStartTime + " - " + strEndTime;

                        activeParkingView.showNoActiveParkingView(false);
                        activeParkingView.showCountDownTime(time);
                        activeParkingView.showActiveParkingDetail(period, location, carNumberPlate);

                        remoteDataSource.getCurrentUnixTime(new DataSource.GetCurrentUnixTimeCallback() {
                            @Override
                            public void onGetCurrentUnixTime(Long currentUnixTime) {
                                if (timerRunning) {
                                    timeLeft = endTime - currentUnixTime;
                                    if (timeLeft < 0) {
                                        timeLeft = 0;
                                        timerRunning = false;
                                        Map<String, Object> time = getHourMinuteSecond(timeLeft);
                                        activeParkingView.showNoActiveParkingView(false);
                                        activeParkingView.showCountDownTime(time);
                                    } else {
                                        startTimer();
                                    }
                                }
                            }

                            @Override
                            public void onCancelled(String errMsg) {
                                activeParkingView.showDbErrMsg(errMsg);
                                activeParkingView.showNoActiveParkingView(true);
                            }
                        });
                    }

                    @Override
                    public void onCancelled(String errMsg) {
                        activeParkingView.showDbErrMsg(errMsg);
                        activeParkingView.showNoActiveParkingView(true);
                    }
                });
            }
        });
    }

    @Override
    public void startTimer() {
        countDownTimerService.startService(timeLeft, new Service.CountDownTimerService
                .GetServiceCallback() {
            @Override
            public void onTick(long millisUntilFinished) {
                // Show parking expiring notification when remaining time is 10 second.
                if (millisUntilFinished < 11000 && millisUntilFinished > 10000) {
                    activeParkingView.showExpiringOrExpiredNotification(carNumberPlate, TYPE_EXPIRING);
                }

                // Show parking expired notification when remaining time is 1 second.
                if (millisUntilFinished < 2000 && millisUntilFinished > 1000) {
                    activeParkingView.showExpiringOrExpiredNotification(carNumberPlate, TYPE_EXPIRED);
                }

                timeLeft = millisUntilFinished;
                Map<String, Object> time = getHourMinuteSecond(timeLeft);
                activeParkingView.showNoActiveParkingView(false);
                activeParkingView.showCountDownTime(time);
            }

            @Override
            public void onFinish() {
                timerRunning = false;
                activeParkingView.showProgressBar(true);
                sharedPreferenceService.getCurrentUserUid(new Service.
                        SharedPreferenceService.GetCurrentUserUidCallback() {
                    @Override
                    public void onGetUid(String uid) {
                        remoteDataSource.updateTimerRunning(uid, timerRunning,
                                new DataSource.UpdateTimerRunningCallback() {
                                    @Override
                                    public void onSuccess() {
                                        activeParkingView.showProgressBar(false);
                                        activeParkingView.showNoActiveParkingView(true);
                                    }

                                    @Override
                                    public void onFailure(String errMsg) {
                                        activeParkingView.showProgressBar(false);
                                        activeParkingView.showNoActiveParkingView(true);
                                        activeParkingView.showDbErrMsg(errMsg);
                                    }
                                });
                    }
                });
            }
        });

        timerRunning = true;
    }

    @Override
    public void stop() {
        sharedPreferenceService.getCurrentUserUid(new Service.SharedPreferenceService
                .GetCurrentUserUidCallback() {
            @Override
            public void onGetUid(String uid) {
                remoteDataSource.updateTimeLeftTimerRunningEndTime(uid, timeLeft, timerRunning,
                        new DataSource.UpdateTimeLeftTimerRunningEndTimeCallback() {
                            @Override
                            public void onSuccess() {
                                countDownTimerService.stopService();
                            }

                            @Override
                            public void onFailure(String errMsg) {
                                activeParkingView.showDbErrMsg(errMsg);
                            }
                        });
            }
        });
    }

    @Override
    public void checkActiveParkingExist() {
        sharedPreferenceService.getCurrentUserUid(new Service.SharedPreferenceService
                .GetCurrentUserUidCallback() {
            @Override
            public void onGetUid(String uid) {
                remoteDataSource.getActiveParking(uid, new DataSource.GetActiveParkingCallback() {
                    @Override
                    public void onGetActiveParking(ActiveParking activeParking) {
                        boolean running = activeParking.getTimerRunning();
                        if (running) {
                            // Active parking exist, show Active Parking screen.
                            start();
                        } else {
                            activeParkingView.showNoActiveParkingView(true);
                        }
                    }

                    @Override
                    public void onCancelled(String errMsg) {
                        activeParkingView.showDbErrMsg(errMsg);
                    }
                });
            }
        });
    }
}
