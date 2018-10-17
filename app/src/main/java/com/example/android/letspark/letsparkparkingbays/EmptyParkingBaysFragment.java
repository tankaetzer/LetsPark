package com.example.android.letspark.letsparkparkingbays;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.TextView;

import com.example.android.letspark.R;
import com.example.android.letspark.addremovecar.AddRemoveCarActivity;
import com.example.android.letspark.data.EmptyParkingBay;
import com.example.android.letspark.utility.NumberUtils;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

import static com.example.android.letspark.addremovecar.AddRemoveCarActivity.REQUEST_ADD_REMOVE_CAR;
import static com.example.android.letspark.letsparkparkingbays.EmptyParkingBaysActivity.EXTRA_CAR_NUMBER_PLATE;
import static com.example.android.letspark.letsparkparkingbays.EmptyParkingBaysActivity.LOCATION_PERMISSION_REQUEST_CODE;
import static com.example.android.letspark.letsparkparkingbays.EmptyParkingBaysActivity.REQUEST_CHECK_SETTINGS;

/**
 * Display markers on Google map. Each marker is an empty parking bay.
 */
public class EmptyParkingBaysFragment extends Fragment implements EmptyParkingBaysContract.View {

    private EmptyParkingBaysContract.Presenter emptyParkingBaysPresenter;

    private SupportMapFragment mapFragment;

    private GoogleMap googleMap;

    private View root;

    private TextView text_distance;

    private TextView text_duration;

    private TextView text_rate;

    private TextView text_select_car;

    private TextView text_select_duration;

    private ProgressBar progressBar;

    private View constraintLayout_distance_price_duration;

    private String carNumberPlate;

    private RadioButton radio_one_hour;

    private RadioButton radio_two_hours;

    private RadioButton radio_thirty_days;

    public EmptyParkingBaysFragment() {
        // Require empty constructor so it can be instantiated when restoring Activity's state.
    }

    public static EmptyParkingBaysFragment newInstance() {
        return new EmptyParkingBaysFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        emptyParkingBaysPresenter.createLocationCallback();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_empty_parking_bays, container, false);

        // Add Google Map fragment to current fragment.
        if (mapFragment == null) {
            mapFragment = SupportMapFragment.newInstance();
            mapFragment.getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    setGoogleMap(googleMap);
                    emptyParkingBaysPresenter.start();
                }
            });
        }
        // R.id.map is a FrameLayout, not a Fragment.
        getFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        // Set up TextView for  distance, duration and rate.
        // Set up constraintLayoutDistancePriceDuration for grouping all text_distance, text_duration,
        // text_rate.
        text_distance = root.findViewById(R.id.text_distance);
        text_duration = root.findViewById(R.id.text_duration);
        text_rate = root.findViewById(R.id.text_rate);
        constraintLayout_distance_price_duration = root
                .findViewById(R.id.constraintLayout_distance_price_duration);

        // Set up horizontal progressBar.
        progressBar = root.findViewById(R.id.progressBar);

        // Set up floating action button
        FloatingActionButton fab =
                getActivity().findViewById(R.id.fab_pay);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });

        // Set up TextView for select car and select duration.
        text_select_car = root.findViewById(R.id.text_select_car);
        text_select_duration = root.findViewById(R.id.text_select_duration);

        text_select_car.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyParkingBaysPresenter.selectCar();
            }
        });

        text_select_duration.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                emptyParkingBaysPresenter.selectDuration();
            }
        });

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();
        emptyParkingBaysPresenter.createLocationCallback();
        emptyParkingBaysPresenter.startLocationUpdate();
    }

    @Override
    public void onPause() {
        super.onPause();
        emptyParkingBaysPresenter.stopLocationUpdate();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_ADD_REMOVE_CAR) {
            if (data != null) {
                carNumberPlate = data.getStringExtra(EXTRA_CAR_NUMBER_PLATE);
            }
        }
        emptyParkingBaysPresenter.result(requestCode, resultCode);
    }

    /**
     * TODO: Refactor presentation logic to presenter class.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Request for access fine location permission.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                showMessage(getString(R.string.permission_location_granted));
                emptyParkingBaysPresenter.loadEmptyParkingBays();
            } else if (!shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessage(getString(R.string.permission_location_never_show_again));
            } else {
                // Permission request was denied.
                showMessage(getString(R.string.permission_location_denied));
            }
        }
    }

    @Override
    public void showEmptyParkingBays(final List<EmptyParkingBay> emptyParkingBayList) {
        getMap().clear();
        for (int index = 0; index < emptyParkingBayList.size(); index++) {
            getMap().addMarker(new MarkerOptions()
                    .position(emptyParkingBayList.get(index).getPosition())
                    .title(emptyParkingBayList.get(index).getSnippet())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }

        // TODO: To be delete once search parking bays feature is complete.
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(3.721885, 103.120707), 21));

        getMap().setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                double rate = 0.0;
                for (int index = 0; index < emptyParkingBayList.size(); index++) {
                    if (marker.getPosition().equals(emptyParkingBayList.get(index).getPosition())) {
                        rate = emptyParkingBayList.get(index).getRate();
                        break;
                    }
                }
                String destinationLatLng = Double.toString(marker.getPosition().latitude) + "," +
                        Double.toString(marker.getPosition().longitude);
                emptyParkingBaysPresenter.requestDistanceMatrix(destinationLatLng, rate);
                return false;
            }
        });

        // Hide ProgressBar and TextView for duration, rate, distance.
        getMap().setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                emptyParkingBaysPresenter.hideDistanceDurationRateTextviewAndProgressbar();
            }
        });
    }

    @Override
    public void setPresenter(EmptyParkingBaysContract.Presenter presenter) {
        emptyParkingBaysPresenter = presenter;
    }

    @Override
    public void setGoogleMap(GoogleMap googleMap) {
        this.googleMap = googleMap;
    }

    @Override
    public void showLoadingEmptyParkingBaysError() {
        showMessage(getString(R.string.marker_loading_empty_parking_bays_error));
    }

    @Override
    public GoogleMap getMap() {
        return googleMap;
    }

    @Override
    public void showLocationErrMsgWithAction() {
        Snackbar.make(root, R.string.permission_rationale_location,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_ok,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
                    }
                }).show();
    }

    /**
     * Check if the access fine location permission has been granted.
     */
    @Override
    public boolean checkSelfPermission() {
        return ContextCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED;
    }

    /**
     * True if permission is not granted since user has previously denied the request.
     */
    @Override
    public boolean shouldShowRequestPermissionRationale() {
        return ActivityCompat.shouldShowRequestPermissionRationale(getActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION);
    }

    @Override
    public void requestLocationPermissions() {
        ActivityCompat.requestPermissions(getActivity(),
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                LOCATION_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void showLocationSettingDialog(Exception e) {
        try {
            // Show the dialog by calling startResolutionForResult(),
            // and check the result in onActivityResult().
            ResolvableApiException resolvable = (ResolvableApiException) e;
            resolvable.startResolutionForResult(getActivity(),
                    REQUEST_CHECK_SETTINGS);
        } catch (IntentSender.SendIntentException sendEx) {
            // Ignore the error.
        }
    }

    @Override
    public void setDistanceDurationAndRate(String distance, String duration, double rate) {
        text_distance.setText(distance);
        text_duration.setText(duration);
        String stringRate = NumberUtils.formatAndDisplayMalaysiaCurrency(rate);
        text_rate.setText(stringRate);
    }

    @Override
    public void showDistanceDurationAndRate(boolean show) {
        if (show) {
            constraintLayout_distance_price_duration.setVisibility(View.VISIBLE);
        } else {
            constraintLayout_distance_price_duration.setVisibility(View.GONE);
        }
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
    public void showDistanceDurationCalculationErrMsg() {
        showMessage(getString(R.string.marker_distance_duration_calculation_error));
    }

    @Override
    public void showGettingLocationMsg() {
        showMessage(getString(R.string.marker_getting_location_msg));
    }

    @Override
    public void showConnectivityAndLocationErrMsg() {
        showMessage(getString(R.string.marker_enable_connectivity_and_location_error));
    }

    @Override
    public void showConnectivityErrMsg() {
        Snackbar.make(root, R.string.marker_enable_connectivity_error,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.action_ok,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        emptyParkingBaysPresenter.checkConnectivity();
                    }
                }).show();
    }

    @Override
    public void setRateAndDefaultDistanceDuration(double rate) {
        text_distance.setText(getString(R.string.marker_default_distance_msg));
        text_duration.setText(getString(R.string.marker_default_duration_msg));
        String stringRate = NumberUtils.formatAndDisplayMalaysiaCurrency(rate);
        text_rate.setText(stringRate);
    }

    @Override
    public void showAddRemoveCarUi(String uid) {
        Intent intent = new Intent(getContext(), AddRemoveCarActivity.class);
        intent.putExtra(AddRemoveCarActivity.EXTRA_UID, uid);
        startActivityForResult(intent, REQUEST_ADD_REMOVE_CAR);
    }

    @Override
    public void showSelectedCar() {
        text_select_car.setText(carNumberPlate);
        text_select_car.setTextColor(getResources().getColor(R.color.colorPrimary));
    }

    @Override
    public void showDurationOptionDialog() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        // Get the layout inflater
        LayoutInflater inflater = getActivity().getLayoutInflater();

        // Inflate and set the layout for the dialog.
        // Pass null as the parent view because its going in the dialog layout.
        View dialogView = inflater.inflate(R.layout.dialog_select_duration, null);

        radio_one_hour = dialogView.findViewById(R.id.radio_one_hour);
        radio_two_hours = dialogView.findViewById(R.id.radio_two_hours);
        radio_thirty_days = dialogView.findViewById(R.id.radio_thirty_days);

        builder.setView(dialogView)
                // Add action buttons
                .setTitle(getString(R.string.home_dialog_title_select_duration))
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (radio_one_hour.isChecked()) {
                            text_select_duration.setText(R.string.home_radio_one_hour);
                        } else if (radio_two_hours.isChecked()) {
                            text_select_duration.setText(R.string.home_radio_two_hours);
                        } else if (radio_thirty_days.isChecked()) {
                            text_select_duration.setText(R.string.home_radio_thirty_days);
                        }
                        text_select_duration.setTextColor(getResources().getColor(R.color.colorPrimary));
                    }
                })
                .setNegativeButton("CANCEL", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                })
                .create();

        builder.show();
    }

    private void showMessage(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
    }
}
