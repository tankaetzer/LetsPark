package com.example.android.letspark.service;

import android.os.CountDownTimer;

/**
 * Implementation of CountDownTimer service.
 */
public class CountDownTimerService implements Service.CountDownTimerService {

    private CountDownTimer countDownTimer;

    @Override
    public void startService(long timeLeft, final GetServiceCallback callback) {
        countDownTimer = new CountDownTimer(timeLeft, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                callback.onTick(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                cancel();
                callback.onFinish();
            }
        }.start();
    }

    @Override
    public void stopService() {
        if (countDownTimer != null) {
            countDownTimer.cancel();
        }
    }
}
