package com.example.android.letspark.letsparkparkingbays;

import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.letspark.R;
import com.example.android.letspark.data.EmptyParkingBay;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

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

    public EmptyParkingBaysFragment() {
        // Require empty constructor
    }

    public static EmptyParkingBaysFragment newInstance() {
        return new EmptyParkingBaysFragment();
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
        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        return root;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        emptyParkingBaysPresenter.askLocationSetting();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void showEmptyParkingBays(List<EmptyParkingBay> emptyParkingBayList) {
        for (int index = 0; index < emptyParkingBayList.size(); index++) {
            getMap().addMarker(new MarkerOptions()
                    .position(emptyParkingBayList.get(index).getPosition())
                    .title(emptyParkingBayList.get(index).getSnippet())
                    .snippet(emptyParkingBayList.get(index).getSnippet())
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)));
        }
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(emptyParkingBayList.get(0).
                getPosition(), 16));
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_CHECK_SETTINGS) {
            emptyParkingBaysPresenter.askLocationPermission(checkSelfPermission(),
                    shouldShowRequestPermissionRationale());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            // Request for access fine location permission.
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted.
                showMessage(getString(R.string.permission_location_granted));
            } else if (!shouldShowRequestPermissionRationale(
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                showMessage(getString(R.string.permission_location_never_show_again));
            } else {
                // Permission request was denied.
                showMessage(getString(R.string.permission_location_denied));
            }
            return;
        }
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
        showMessage(getString(R.string.error_loading_empty_parking_bays));
    }

    @Override
    public GoogleMap getMap() {
        return googleMap;
    }

    @Override
    public void showErrorMessageWithAction() {
        Snackbar.make(root, R.string.permission_rationale_location,
                Snackbar.LENGTH_INDEFINITE).setAction(R.string.ok,
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        // Request the permission
                        requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                LOCATION_PERMISSION_REQUEST_CODE);
                    }
                }).show();
    }

    private void showMessage(String message) {
        Snackbar.make(root, message, Snackbar.LENGTH_LONG).show();
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
}
