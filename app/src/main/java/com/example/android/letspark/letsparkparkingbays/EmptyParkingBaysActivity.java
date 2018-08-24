package com.example.android.letspark.letsparkparkingbays;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.letspark.LetsParkApplication;
import com.example.android.letspark.R;
import com.example.android.letspark.data.EmptyParkingBaysRemoteDataSource;
import com.example.android.letspark.service.LocationService;
import com.example.android.letspark.utility.ActivityUtils;

import javax.inject.Inject;

public class EmptyParkingBaysActivity extends AppCompatActivity {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static final int REQUEST_CHECK_SETTINGS = 2;

    private EmptyParkingBaysFragment emptyParkingBaysFragment;

    @Inject
    LocationService locationService;

    @Inject
    EmptyParkingBaysRemoteDataSource emptyParkingBaysRemoteDataSource;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_empty_parking_bays);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle("LetsPark");

        emptyParkingBaysFragment =
                (EmptyParkingBaysFragment) getSupportFragmentManager().findFragmentById
                        (R.id.contentFrame);
        if (emptyParkingBaysFragment == null) {
            // Create the fragment.
            emptyParkingBaysFragment = EmptyParkingBaysFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), emptyParkingBaysFragment, R.id.contentFrame);
        }


        // Get the instance of LetsParkComponent to connect between our dependency provider
        // and dependency consumer.
        ((LetsParkApplication) getApplication()).getLetsParkComponent()
                .inject(this);

        // TODO: Improve code by injecting dependency using Dagger 2
        // Create the presenter.
        new EmptyParkingBaysPresenter(emptyParkingBaysRemoteDataSource,
                emptyParkingBaysFragment, locationService);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
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
