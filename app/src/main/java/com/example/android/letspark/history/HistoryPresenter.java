package com.example.android.letspark.history;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.data.model.History;
import com.example.android.letspark.service.Service;
import com.example.android.letspark.service.SharedPreferenceService;

import java.util.Collections;
import java.util.List;

import static com.example.android.letspark.utility.DateUtils.convertAndFormatUnixTimeToDateTime;
import static com.example.android.letspark.utility.NumberUtils.formatAndDisplayMalaysiaCurrency;
import static com.google.common.base.Preconditions.checkNotNull;

public class HistoryPresenter implements HistoryContract.Presenter {

    private HistoryContract.View historyView;

    private RemoteDataSource remoteDataSource;

    private SharedPreferenceService sharedPreferenceService;

    public HistoryPresenter(HistoryContract.View historyView,
                            RemoteDataSource remoteDataSource,
                            SharedPreferenceService sharedPreferenceService) {
        this.historyView = checkNotNull(historyView);
        this.remoteDataSource = checkNotNull(remoteDataSource);
        this.sharedPreferenceService = checkNotNull(sharedPreferenceService);
        historyView.setPresenter(this);
    }

    @Override
    public void start() {
        loadUserHistories();
    }

    @Override
    public void loadUserHistories() {
        sharedPreferenceService.getCurrentUserUid(new Service
                .SharedPreferenceService.GetCurrentUserUidCallback() {
            @Override
            public void onGetUid(String uid) {
                historyView.showProgressBar(true);

                remoteDataSource.getUserHistory(uid,
                        new DataSource.LoadUserHistoriesCallBack() {
                            @Override
                            public void onUserHistoriesLoaded(List<History> historyList) {
                                historyView.showProgressBar(false);
                                // Show the latest history at first position of ListView.
                                Collections.reverse(historyList);
                                historyView.showHistories(historyList);
                            }

                            @Override
                            public void onCancelled(String errMsg) {
                                historyView.showProgressBar(false);
                                historyView.showRemoteDbErrMsg(errMsg);
                            }
                        });
            }
        });
    }

    @Override
    public String convertUnixTimeToDateTime(long startTime) {
        return convertAndFormatUnixTimeToDateTime(startTime);
    }

    @Override
    public String formatPaymentInMalaysiaCurrency(double payment) {
        return formatAndDisplayMalaysiaCurrency(payment);
    }
}
