package com.example.android.letspark.activeparking;

import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import com.example.android.letspark.LetsParkApp;
import com.example.android.letspark.R;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.idlingresource.SimpleIdlingResource;
import com.example.android.letspark.service.CountDownTimerService;
import com.example.android.letspark.service.SharedPreferenceService;
import com.example.android.letspark.utility.ActivityUtils;

import javax.inject.Inject;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.VisibleForTesting;
import androidx.test.espresso.IdlingResource;

public class ActiveParkingActivity extends AppCompatActivity {

    @Inject
    public RemoteDataSource remoteDataSource;
    @Inject
    public SharedPreferenceService sharedPreferenceService;
    @Inject
    public CountDownTimerService countDownTimerService;
    private ActiveParkingFragment activeParkingFragment;
    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource idlingResource;

    public static final String EXTRA_PAYMENT = "PAYMENT";

    public static final String EXTRA_ACTIVE_PARKING_EXIST = "EXIST_ACTIVE_PARKING";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_active_parking);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.active_parking_title);

        // Enable the Up button.
        ab.setDisplayHomeAsUpEnabled(true);

        // Get the requested payment.
        double madePayment = getIntent().getDoubleExtra(EXTRA_PAYMENT, 0);
        boolean activeParkingExist = getIntent().getBooleanExtra(EXTRA_ACTIVE_PARKING_EXIST,
                false);

        activeParkingFragment = (ActiveParkingFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (activeParkingFragment == null) {
            // Create the fragment.
            activeParkingFragment = ActiveParkingFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), activeParkingFragment, R.id.contentFrame);
        }

        // Get the instance of LetsParkAppComponent to connect between our dependency provider
        // and dependency consumer.
        ((LetsParkApp) getApplication()).getLetsParkAppComponent().inject(this);

        // TODO: Improve code by injecting dependency using Dagger 2
        // Create the presenter.
        new ActiveParkingPresenter(madePayment, activeParkingExist, activeParkingFragment,
                remoteDataSource, sharedPreferenceService, countDownTimerService);
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
