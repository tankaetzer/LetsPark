package com.example.android.letspark.letsparkparkingbays;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.letspark.R;
import com.example.android.letspark.data.EmptyParkingBay;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.List;

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
                    emptyParkingBaysPresenter.loadEmptyParkingBays();
                }
            });
        }
        // R.id.map is a FrameLayout, not a Fragment
        getChildFragmentManager().beginTransaction().replace(R.id.map, mapFragment).commit();

        return root;
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
        getMap().moveCamera(CameraUpdateFactory.newLatLngZoom(emptyParkingBayList.get(0).getPosition(), 16));
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
        showErrorMessage("Error while loading empty parking bays");
    }

    @Override
    public GoogleMap getMap() {
        return googleMap;
    }

    private void showErrorMessage(String errorMessage) {
        Snackbar.make(root, errorMessage, Snackbar.LENGTH_LONG).show();
    }
}
