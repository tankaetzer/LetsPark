package com.example.android.letspark.letsparkparkingbays;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.letspark.LetsParkApp;
import com.example.android.letspark.R;
import com.example.android.letspark.data.EmptyParkingBaysRemoteDataSource;
import com.example.android.letspark.service.ConnectivityService;
import com.example.android.letspark.service.DistanceMatrixService;
import com.example.android.letspark.service.LocationService;
import com.example.android.letspark.utility.ActivityUtils;

import javax.inject.Inject;

import static com.example.android.letspark.addremovecar.AddRemoveCarActivity.REQUEST_ADD_REMOVE_CAR;

public class EmptyParkingBaysActivity extends AppCompatActivity {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static final int REQUEST_CHECK_SETTINGS = 2;

    public static String EXTRA_UID = "QWERTYUIOPASDFGHJKLZXCVBNM";

    public static String EXTRA_CAR_NUMBER_PLATE = "WWW1234";

    @Inject
    EmptyParkingBaysRemoteDataSource emptyParkingBaysRemoteDataSource;

    @Inject
    LocationService locationService;

    @Inject
    DistanceMatrixService distanceMatrixService;

    @Inject
    ConnectivityService connectivityService;

    private EmptyParkingBaysFragment emptyParkingBaysFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_parking_bays);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("LetsPark");

        // Get the current user uid.
        String uid = getIntent().getStringExtra(EXTRA_UID);

        emptyParkingBaysFragment =
                (EmptyParkingBaysFragment) getSupportFragmentManager().findFragmentById
                        (R.id.contentFrame);
        if (emptyParkingBaysFragment == null) {
            // Create the fragment.
            emptyParkingBaysFragment = EmptyParkingBaysFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), emptyParkingBaysFragment, R.id.contentFrame);
        }

        // Get the instance of LetsParkAppComponent to connect between our dependency provider
        // and dependency consumer.
        ((LetsParkApp) getApplication()).getLetsParkAppComponent()
                .inject(this);

        // TODO: Improve code by injecting dependency using Dagger 2
        // Create the presenter.
        new EmptyParkingBaysPresenter(uid, emptyParkingBaysRemoteDataSource,
                emptyParkingBaysFragment, locationService, distanceMatrixService,
                connectivityService);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            emptyParkingBaysFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == REQUEST_ADD_REMOVE_CAR) {
            emptyParkingBaysFragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            emptyParkingBaysFragment.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
