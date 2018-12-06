package com.example.android.letspark.home;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

import com.braintreepayments.api.dropin.DropInRequest;
import com.example.android.letspark.LetsParkApp;
import com.example.android.letspark.R;
import com.example.android.letspark.data.RemoteDataSource;
import com.example.android.letspark.history.HistoryActivity;
import com.example.android.letspark.service.ConnectivityService;
import com.example.android.letspark.service.DistanceMatrixService;
import com.example.android.letspark.service.LocationService;
import com.example.android.letspark.service.SharedPreferenceService;
import com.example.android.letspark.signinsignup.SignInSignUpActivity;
import com.example.android.letspark.utility.ActivityUtils;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import javax.inject.Inject;

import static com.example.android.letspark.addremovecar.AddRemoveCarActivity.REQUEST_ADD_REMOVE_CAR;
import static com.example.android.letspark.payment.PaymentActivity.BRAIN_TREE_TOKENIZATION_KEY;

public class HomeActivity extends AppCompatActivity {

    public static final int LOCATION_PERMISSION_REQUEST_CODE = 1;

    public static final int REQUEST_CHECK_SETTINGS = 2;

    public static final int REQUEST_ADD_PAYMENT = 3;

    public static String EXTRA_CAR_NUMBER_PLATE = "WWW1234";

    @Inject
    RemoteDataSource remoteDataSource;

    @Inject
    LocationService locationService;

    @Inject
    DistanceMatrixService distanceMatrixService;

    @Inject
    ConnectivityService connectivityService;

    @Inject
    SharedPreferenceService sharedPreferenceService;

    private HomeFragment homeFragment;

    private DrawerLayout drawerLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Set up the toolbar.
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar ab = getSupportActionBar();
        ab.setHomeAsUpIndicator(R.drawable.ic_menu_white);
        ab.setDisplayHomeAsUpEnabled(true);
        ab.setTitle(R.string.title_activity_motorist_home);

        // Set up the navigation drawer.
        drawerLayout = findViewById(R.id.drawer_layout);
        drawerLayout.setStatusBarBackground(R.color.colorPrimaryDark);
        NavigationView navigationView = findViewById(R.id.nav_view);
        if (navigationView != null) {
            setupDrawerContent(navigationView);
        }

        homeFragment = (HomeFragment) getSupportFragmentManager()
                .findFragmentById(R.id.contentFrame);
        if (homeFragment == null) {
            // Create the fragment.
            homeFragment = HomeFragment.newInstance();
            ActivityUtils.addFragmentToActivity(
                    getSupportFragmentManager(), homeFragment, R.id.contentFrame);
        }

        // Get the instance of LetsParkAppComponent to connect between our dependency provider
        // and dependency consumer.
        ((LetsParkApp) getApplication()).getLetsParkAppComponent()
                .inject(this);

        // TODO: Improve code by injecting dependency using Dagger 2
        // Create the presenter.
        new HomePresenter(remoteDataSource, homeFragment, locationService,
                distanceMatrixService, connectivityService, sharedPreferenceService);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            homeFragment.onActivityResult(requestCode, resultCode, data);
        } else if (requestCode == REQUEST_ADD_REMOVE_CAR) {
            homeFragment.onActivityResult(requestCode, resultCode, data);
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            homeFragment.onRequestPermissionsResult(requestCode, permissions,
                    grantResults);
        } else {
            super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                // Open the navigation drawer when the home icon is selected from the toolbar.
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                        switch (menuItem.getItemId()) {
                            case R.id.drawer_history:
                                Intent intent = new Intent(HomeActivity.this,
                                        HistoryActivity.class);
                                startActivity(intent);
                                break;
                            case R.id.drawer_sign_out:
                                AuthUI.getInstance()
                                        .signOut(HomeActivity.this)
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            public void onComplete(@NonNull Task<Void> task) {
                                                // Navigate user to sign in screen after
                                                // successfully sign out.
                                                Intent intent
                                                        = new Intent(HomeActivity.this,
                                                        SignInSignUpActivity.class);
                                                startActivity(intent);
                                            }
                                        });
                                break;
                            case R.id.drawer_payment_method:
                                DropInRequest dropInRequest = new DropInRequest()
                                        .clientToken(BRAIN_TREE_TOKENIZATION_KEY);
                                intent = dropInRequest.getIntent(HomeActivity.this);
                                startActivityForResult(intent, REQUEST_ADD_PAYMENT);
                                break;
                            default:
                                break;
                        }
                        // Close the navigation drawer when an item is selected.
                        menuItem.setChecked(true);
                        drawerLayout.closeDrawers();
                        return true;
                    }
                });
    }
}
