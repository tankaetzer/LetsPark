package com.example.android.letspark.activeparking;

import com.example.android.letspark.BasePresenter;
import com.example.android.letspark.BaseView;

import java.util.Map;

/**
 * This specifies the contract between the view and the presenter.
 */
public interface ActiveParkingContract {

    interface View extends BaseView<ActiveParkingContract.Presenter> {
        void showCountDownTime(Map<String, Object> time);

        void showDbErrMsg(String errMsg);

        void showActiveParkingDetail(String period, String location, String carNumberPlate);

        void showNoActiveParkingView(boolean show);

        void showProgressBar(boolean show);

        void showExpiringOrExpiredNotification(String carNumberPlate, int notificationType);

        void showExtendDurationOptionDialog();

        void showExtendSuccessfullyMsg();

        void showPaymentMade(String payment);

        void showActiveParkingExistMsg();

        void showEndTimeNotInParkingEnforcementPeriodMsg();
    }

    interface Presenter extends BasePresenter {
        void startTimer();

        void stop();

        void checkActiveParkingExist();

        void selectDuration();

        void extend(int duration);

        void formatAndShowPayment();

        void showActiveParkingExistMsg();

        double determinePayment(int duration);

        long convertHourToMilliseconds(int hour);

        void checkEndTimeIsBeforeFivePm(int duration);

        long durationBetweenTwoDateTime(long currentTime);

        int suggestDeductDuration(long timeDueAfterFivePmInMinute);
    }
}
