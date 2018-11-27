package com.example.android.letspark.addremovecar;

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

public class AddRemoveCarActivity extends AppCompatActivity {

    public static final int REQUEST_ADD_REMOVE_CAR = 5;

    @Inject
    public RemoteDataSource remoteDataSource;

    @Inject
    public SharedPreferenceService sharedPreferenceService;

    // The Idling Resource which will be null in production.
    @Nullable
    private SimpleIdlingResource idlingResource;

    private AddRemoveCarFragment addRemoveCarFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_remove_car);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setTitle(R.string.title_activity_car);

        // Enable the Up button.
        ab.setDisplayHomeAsUpEnabled(true);

        addRemoveCarFragment = (AddRemoveCarFragment) getSupportFragmentManager().findFragmentById
                (R.id.contentFrame);
        if (addRemoveCarFragment == null) {
            // Create the fragment.
            addRemoveCarFragment = AddRemoveCarFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), addRemoveCarFragment, R.id.contentFrame);
        }

        // Get the instance of LetsParkAppComponent to connect between our dependency provider
        // and dependency consumer.
        ((LetsParkApp) getApplication()).getLetsParkAppComponent()
                .inject(this);

        // TODO: Improve code by injecting dependency using Dagger 2
        // Create the presenter.
        new AddRemoveCarPresenter(addRemoveCarFragment, remoteDataSource, sharedPreferenceService);
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
