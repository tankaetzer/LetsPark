package com.example.android.letspark.history;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.android.letspark.R;
import com.example.android.letspark.data.model.History;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

public class HistoryFragment extends Fragment implements HistoryContract.View {

    private View root;

    private HistoryContract.Presenter historyPresenter;

    private HistoriesAdapter historiesAdapter;

    private ProgressBar progressBar;

    public HistoryFragment() {
        // Require empty constructor so it can be instantiated when restoring Activity's state.
    }

    public static HistoryFragment newInstance() {
        return new HistoryFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        historiesAdapter = new HistoriesAdapter(new ArrayList<History>(0));
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_history, container, false);

        // Set up histories view.
        ListView listView = root.findViewById(R.id.list_history);
        listView.setAdapter(historiesAdapter);

        // Set up horizontal progressBar.
        progressBar = root.findViewById(R.id.progressBar);

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        historyPresenter.start();
    }

    @Override
    public void setPresenter(HistoryContract.Presenter presenter) {
        historyPresenter = checkNotNull(presenter);
    }

    @Override
    public void showHistories(List<History> historyList) {
        historiesAdapter.replaceData(historyList);
    }

    @Override
    public void showProgressBar(boolean show) {
        if (show) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
    }

    @Override
    public void showRemoteDbErrMsg(String errMsg) {
        showMessage(errMsg);
    }

    private void showMessage(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }

    private class HistoriesAdapter extends BaseAdapter {

        private List<History> histories;

        private HistoriesAdapter(List<History> histories) {
            setList(histories);
        }

        private void replaceData(List<History> histories) {
            setList(histories);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return histories.size();
        }

        @Override
        public Object getItem(int position) {
            return histories.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            // Check if the existing view is being reused, otherwise inflate the view
            View rowView = convertView;
            if (rowView == null) {
                LayoutInflater inflater = LayoutInflater.from(parent.getContext());
                rowView = inflater.inflate(R.layout.history_item, parent, false);
            }

            // Get the car object located at this position in the list.
            final History currentHistory = (History) getItem(position);

            TextView text_location = rowView.findViewById(R.id.text_location);
            TextView text_number_plate = rowView.findViewById(R.id.text_number_plate);
            TextView text_date_time = rowView.findViewById(R.id.text_start_date_time);
            TextView text_duration = rowView.findViewById(R.id.text_duration);
            TextView text_payment = rowView.findViewById(R.id.text_payment);
            TextView text_receipt_id = rowView.findViewById(R.id.text_transaction_id);
            text_location.setText(currentHistory.getLocation());
            text_number_plate.setText(currentHistory.getCarNumberPlate());

            String startTime
                    = historyPresenter.convertUnixTimeToDateTime(currentHistory.getStartTime());

            text_date_time.setText(startTime);
            text_duration.setText(String.valueOf(currentHistory.getDuration()));

            String payment
                    = historyPresenter.formatPaymentInMalaysiaCurrency(currentHistory.getPayment());

            text_payment.setText(payment);
            text_receipt_id.setText(currentHistory.getTransactionId());
            return rowView;
        }

        private void setList(List<History> histories) {
            this.histories = checkNotNull(histories);
        }
    }
}
