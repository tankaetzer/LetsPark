package com.example.android.letspark.activeparking;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.data.model.ActiveParking;
import com.example.android.letspark.service.CountDownTimerService;
import com.example.android.letspark.service.Service;
import com.example.android.letspark.service.SharedPreferenceService;

import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.joda.time.Duration;
import org.joda.time.Instant;

import java.util.Map;

import static com.example.android.letspark.activeparking.ActiveParkingFragment.TYPE_EXPIRED;
import static com.example.android.letspark.activeparking.ActiveParkingFragment.TYPE_EXPIRING;
import static com.example.android.letspark.home.HomePresenter.ZONE_ID;
import static com.example.android.letspark.utility.DateUtils.convertAndFormatUnixTimeToTime;
import static com.example.android.letspark.utility.NumberUtils.formatAndDisplayMalaysiaCurrency;
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

    private long durationInMillisecond;

    private int durationInHour;

    private String parking;

    private String carNumberPlate = "QWER1234";

    private String uid;

    private String transactionId;

    private double payment;

    private double madePayment;

    private boolean activeParkingExist;

    private boolean isBeforeFivePm;

    public ActiveParkingPresenter(double madePayment, boolean activeParkingExist,
                                  ActiveParkingContract.View activeParkingView,
                                  RemoteDataSource remoteDataSource,
                                  SharedPreferenceService sharedPreferenceService,
                                  CountDownTimerService countDownTimerService) {
        this.madePayment = madePayment;
        this.activeParkingExist = activeParkingExist;
        this.activeParkingView = checkNotNull(activeParkingView);
        this.remoteDataSource = checkNotNull(remoteDataSource);
        this.sharedPreferenceService = checkNotNull(sharedPreferenceService);
        this.countDownTimerService = checkNotNull(countDownTimerService);
        activeParkingView.setPresenter(this);
    }

    private void setUid(String uid) {
        this.uid = uid;
    }

    @Override
    public void start() {
        activeParkingView.showProgressBar(true);
        sharedPreferenceService.getCurrentUserUid(new Service.SharedPreferenceService
                .GetCurrentUserUidCallback() {
            @Override
            public void onGetUid(String uid) {
                setUid(uid);
                remoteDataSource.getActiveParking(uid, new DataSource.GetActiveParkingCallback() {
                    @Override
                    public void onGetActiveParking(ActiveParking activeParking) {
                        activeParkingView.showProgressBar(false);
                        timeLeft = activeParking.getDuration();
                        durationInMillisecond = activeParking.getDuration();
                        startTime = activeParking.getStartTime();
                        endTime = activeParking.getEndTime();
                        timerRunning = activeParking.getTimerRunning();
                        parking = activeParking.getParking();
                        carNumberPlate = activeParking.getCarNumberPlate();
                        transactionId = activeParking.getTransactionId();
                        payment = activeParking.getPayment();
                        Map<String, Object> time = getHourMinuteSecond(timeLeft);

                        String strStartTime = convertAndFormatUnixTimeToTime(startTime);
                        String strEndTime = convertAndFormatUnixTimeToTime(endTime);
                        String period = strStartTime + " - " + strEndTime;

                        activeParkingView.showNoActiveParkingView(false);
                        activeParkingView.showCountDownTime(time);
                        activeParkingView.showActiveParkingDetail(period, parking, carNumberPlate);

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
                        boolean running = false;
                        if (activeParking != null) {
                            running = activeParking.getTimerRunning();
                        }
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

    @Override
    public void selectDuration() {
        activeParkingView.showExtendDurationOptionDialog();
    }

    @Override
    public void extend(int duration) {
        long hourInMillisecond = convertHourToMilliseconds(duration);
        durationInMillisecond = durationInMillisecond + hourInMillisecond;
        durationInHour = (int) durationInMillisecond / 60 / 60 / 1000;
        endTime = startTime + durationInMillisecond;
        double extendPayment = determinePayment(duration);
        final double newPayment = payment + extendPayment;

        remoteDataSource.writeNewActiveParking(uid, carNumberPlate, parking,
                startTime, this.durationInMillisecond, endTime, transactionId, newPayment,
                new DataSource.WriteActiveParkingCallback() {
                    @Override
                    public void onSuccess() {
                        activeParkingView.showExtendSuccessfullyMsg();
                        remoteDataSource.updateExistTransaction(uid, transactionId, durationInHour, newPayment);
                        start();
                    }

                    @Override
                    public void onFailure(String errMsg) {
                        activeParkingView.showDbErrMsg(errMsg);
                    }
                });
    }

    @Override
    public double determinePayment(int duration) {
        if (duration >= 1 && duration <= 7) {
            return duration * 0.45;
        } else if (duration == 8 || duration == 9) {
            return 3.50;
        } else {
            return 0;
        }
    }

    @Override
    public long durationBetweenTwoDateTime(long endTime) {
        Instant instantEndTime = Instant.ofEpochMilli(endTime);
        DateTimeZone zone = DateTimeZone.forID(ZONE_ID);
        DateTime dtEndTime = new DateTime(instantEndTime, zone);

        DateTime today = new DateTime(zone);
        DateTime todayFivePm = today.withHourOfDay(17).withMinuteOfHour(0).withSecondOfMinute(0);

        Duration duration = new Duration(todayFivePm, dtEndTime);
        return duration.getStandardMinutes();
    }

    @Override
    public void formatAndShowPayment() {
        if (madePayment != 0) {
            String formattedPayment = formatAndDisplayMalaysiaCurrency(madePayment);
            activeParkingView.showPaymentMade(formattedPayment + " has been deducted from your " +
                    "card.");
        }
        // Show this message only once by set madePayment to 0 so whenever user check active
        // parking directly, the payment message will not show again.
        madePayment = 0;
    }

    @Override
    public void showActiveParkingExistMsg() {
        if (activeParkingExist) {
            activeParkingView.showActiveParkingExistMsg();
        }
        // Show this message only once by set activeParkingExist to false since this method will be
        // fired on onStart() every time.
        activeParkingExist = false;
    }

    @Override
    public long convertHourToMilliseconds(int hour) {
        return hour * 60 * 60 * 1000;
    }

    @Override
    public void checkEndTimeIsBeforeFivePm(final int duration) {
        activeParkingView.showProgressBar(true);
        final long hourInMilliseconds = convertHourToMilliseconds(duration);
        remoteDataSource.getCurrentUnixTime(new DataSource.GetCurrentUnixTimeCallback() {
            @Override
            public void onGetCurrentUnixTime(Long currentUnixTime) {
                activeParkingView.showProgressBar(false);

                long tempEndTime = endTime;
                tempEndTime = tempEndTime + hourInMilliseconds;
                Instant instantEndTime = Instant.ofEpochMilli(tempEndTime);
                DateTimeZone zone = DateTimeZone.forID(ZONE_ID);
                DateTime dtEndTime = new DateTime(instantEndTime, zone);

                DateTime today = new DateTime(zone);
                DateTime todayFivePm
                        = today.withHourOfDay(17).withMinuteOfHour(0).withSecondOfMinute(0);
                isBeforeFivePm = dtEndTime.isBefore(todayFivePm);

                if (isBeforeFivePm) {
                    extend(duration);
                } else {
                    long timeDueAfterFivePmInMinute = durationBetweenTwoDateTime(tempEndTime);
                    int suggestedDeductDuration = suggestDeductDuration(timeDueAfterFivePmInMinute);
                    int requireDuration = duration - suggestedDeductDuration;
                    if (requireDuration > 0) {
                        extend(requireDuration);
                    } else {
                        activeParkingView.showEndTimeNotInParkingEnforcementPeriodMsg();
                    }
                }
                // Set tempEndTime to 0 to reset this variable every time after perform summation.
                tempEndTime = 0;
            }

            @Override
            public void onCancelled(String errMsg) {
                activeParkingView.showProgressBar(false);
                activeParkingView.showDbErrMsg(errMsg);
            }
        });
    }

    @Override
    public int suggestDeductDuration(long timeDueAfterFivePmInMinute) {
        int suggestDeductDuration;

        // Within 1 hour
        if (timeDueAfterFivePmInMinute > 0 && timeDueAfterFivePmInMinute <= 60) {
            suggestDeductDuration = 0;
        }// Within 1 and 2 hour
        else if (timeDueAfterFivePmInMinute > 60 && timeDueAfterFivePmInMinute <= 120) {
            suggestDeductDuration = 1;
        }// Within 2 and 3 hour
        else if (timeDueAfterFivePmInMinute > 120 && timeDueAfterFivePmInMinute <= 180) {
            suggestDeductDuration = 2;
        }// Within 3 and 4 hour
        else if (timeDueAfterFivePmInMinute > 180 && timeDueAfterFivePmInMinute <= 240) {
            suggestDeductDuration = 3;
        }// Within 4 and 5 hour
        else if (timeDueAfterFivePmInMinute > 240 && timeDueAfterFivePmInMinute <= 300) {
            suggestDeductDuration = 4;
        }// Within 5 and 6 hour
        else if (timeDueAfterFivePmInMinute > 300 && timeDueAfterFivePmInMinute <= 360) {
            suggestDeductDuration = 5;
        }// Within 6 and 7 hour
        else if (timeDueAfterFivePmInMinute > 360 && timeDueAfterFivePmInMinute <= 420) {
            suggestDeductDuration = 6;
        }// Within 7 and 8 hour
        else if (timeDueAfterFivePmInMinute > 420 && timeDueAfterFivePmInMinute <= 480) {
            suggestDeductDuration = 7;
        } // Within 8 and 9 hour
        else if (timeDueAfterFivePmInMinute > 480 && timeDueAfterFivePmInMinute <= 540) {
            suggestDeductDuration = 8;
        } else if (timeDueAfterFivePmInMinute > 540 && timeDueAfterFivePmInMinute <= 600) {
            suggestDeductDuration = 9;
        } else {
            suggestDeductDuration = 0;
        }
        return suggestDeductDuration;
    }
}
