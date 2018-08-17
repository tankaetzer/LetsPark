package com.example.android.letspark.letsparkparkingbays;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;

import com.example.android.letspark.R;
import com.example.android.letspark.data.EmptyParkingBaysRemoteDataSource;
import com.example.android.letspark.utility.ActivityUtils;

public class EmptyParkingBaysActivity extends AppCompatActivity {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    private EmptyParkingBaysFragment emptyParkingBaysFragment;

    private EmptyParkingBaysPresenter emptyParkingBaysPresenter;

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

        // Create the presenter.
        emptyParkingBaysPresenter = new EmptyParkingBaysPresenter(
                EmptyParkingBaysRemoteDataSource.getInstance(), emptyParkingBaysFragment);
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
