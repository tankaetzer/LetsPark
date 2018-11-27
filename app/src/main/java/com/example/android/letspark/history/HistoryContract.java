package com.example.android.letspark.history;

import com.example.android.letspark.BasePresenter;
import com.example.android.letspark.BaseView;
import com.example.android.letspark.data.model.History;

import java.util.List;

public interface HistoryContract {

    interface View extends BaseView<HistoryContract.Presenter> {
        void showHistories(List<History> historyList);

        void showProgressBar(boolean show);

        void showRemoteDbErrMsg(String errMsg);
    }

    interface Presenter extends BasePresenter {
        void loadUserHistories();

        String convertUnixTimeToDateTime(long startTime);

        String formatPaymentInMalaysiaCurrency(double payment);
    }
}
