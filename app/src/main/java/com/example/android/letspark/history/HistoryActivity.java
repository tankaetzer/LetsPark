package com.example.android.letspark.history;

import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.letspark.LetsParkApp;
import com.example.android.letspark.R;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.idlingresource.SimpleIdlingResource;
import com.example.android.letspark.service.SharedPreferenceService;
import com.example.android.letspark.utility.ActivityUtils;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;

public class HistoryActivity extends AppCompatActivity {

    @Inject
    public RemoteDataSource remoteDataSource;

    @Inject
    public SharedPreferenceService sharedPreferenceService;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource idlingResource;

    private HistoryFragment historyFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.title_activity_history);

        // Enable the Up button
        ab.setDisplayHomeAsUpEnabled(true);

        historyFragment = (HistoryFragment) getSupportFragmentManager().findFragmentById
                (R.id.contentFrame);
        if (historyFragment == null) {
            // Create the fragment.
            historyFragment = HistoryFragment.newInstance();
            ActivityUtils.addFragmentToActivity(getSupportFragmentManager(),
                    historyFragment, R.id.contentFrame);
        }

        // Get the instance of LetsParkAppComponent to connect between our dependency provider
        // and dependency consumer.
        ((LetsParkApp) getApplication()).getLetsParkAppComponent().inject(this);

        // TODO: Improve code by injecting dependency using Dagger 2
        // Create the presenter.
        new HistoryPresenter(historyFragment, remoteDataSource, sharedPreferenceService);
    }

    /**
     * Only called from test, creates and returns a new {@link SimpleIdlingResource}.
     */
    @NonNull
    @VisibleForTesting
    public IdlingResource getIdlingResource() {
        if (idlingResource == null) {
            idlingResource = new SimpleIdlingResource();
        }
        return idlingResource;
    }
}
