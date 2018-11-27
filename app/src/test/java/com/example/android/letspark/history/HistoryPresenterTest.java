package com.example.android.letspark.history;

import com.example.android.letspark.data.DataSource;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.data.model.History;
import com.example.android.letspark.service.Service;
import com.example.android.letspark.service.SharedPreferenceService;
import com.google.common.collect.Lists;

import org.junit.Before;
import org.junit.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.List;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;

/**
 * Unit tests for the implementation of HistoryPresenter.
 */
public class HistoryPresenterTest {

    private static List<History> historyList;
    @Mock
    private HistoryContract.View historyView;
    @Mock
    private RemoteDataSource remoteDataSource;
    @Mock
    private SharedPreferenceService sharedPreferenceService;
    @Captor
    private ArgumentCaptor<DataSource.LoadUserHistoriesCallBack>
            loadUserHistoriesCallBackArgumentCaptor;
    @Captor
    private ArgumentCaptor<Service.SharedPreferenceService.GetCurrentUserUidCallback>
            getCurrentUserUidCallbackArgumentCaptor;
    private HistoryPresenter historyPresenter;
    private String uid = "xxxxxxxx";

    @Before
    public void setup() {
        // To inject the mocks in the test the initMocks method needs to be called.
        MockitoAnnotations.initMocks(this);

        historyList = Lists.newArrayList(new History("WWW1234",
                "Kuantan",
                Long.valueOf("1542022506879"),
                "-sdsad",
                0.45,
                1));

        historyPresenter = new HistoryPresenter(historyView, remoteDataSource,
                sharedPreferenceService);
    }

    @Test
    public void createPresenter_setsThePresenterToView() {
        // Get a reference to the class under test.
        historyPresenter = new HistoryPresenter(historyView, remoteDataSource,
                sharedPreferenceService);

        // Then the presenter is set to the view.
        verify(historyView).setPresenter(historyPresenter);
    }

    @Test
    public void loadUserHistories_onUserHistoriesLoaded_showProgressBarTrueAndShowHistories() {
        historyPresenter.loadUserHistories();

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(historyView).showProgressBar(true);

        verify(remoteDataSource).getUserHistory(anyString(),
                loadUserHistoriesCallBackArgumentCaptor.capture());

        loadUserHistoriesCallBackArgumentCaptor.getValue().onUserHistoriesLoaded(historyList);

        verify(historyView).showProgressBar(false);
        verify(historyView).showHistories(historyList);
    }

    @Test
    public void loadUserHistories_onCancelled_showProgressBarFalseAndshowRemoteDbErrMsg() {
        String errMsg = "error";

        historyPresenter.loadUserHistories();

        verify(sharedPreferenceService)
                .getCurrentUserUid(getCurrentUserUidCallbackArgumentCaptor.capture());

        getCurrentUserUidCallbackArgumentCaptor.getValue().onGetUid(uid);

        verify(historyView).showProgressBar(true);

        verify(remoteDataSource).getUserHistory(anyString(),
                loadUserHistoriesCallBackArgumentCaptor.capture());

        loadUserHistoriesCallBackArgumentCaptor.getValue().onCancelled(errMsg);

        verify(historyView).showProgressBar(false);
        verify(historyView).showRemoteDbErrMsg(errMsg);
    }
}
