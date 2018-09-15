package com.example.android.letspark.data;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;

/**
 * Immutable model class for an EmptyParkingBay.
 */
public class EmptyParkingBay implements ClusterItem {

    private LatLng position;

    private double lat;

    private double lng;

    private String snippet;

    private Double rate;

    private boolean vacancy;

    public EmptyParkingBay() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class).
    }

    public EmptyParkingBay(double lat, double lng, String snippet, Double rate, boolean vacancy) {
        setLat(lat);
        setLng(lng);
        setSnippet(snippet);
        setRate(rate);
        setPosition(new LatLng(lat, lng));
        setVacancy(vacancy);
    }

    //    @Override
    public LatLng getPosition() {
        return position;
    }

    public void setPosition(LatLng position) {
        this.position = position;
    }

    //    @Override
    public String getTitle() {
        return null;
    }

    //    @Override
    public String getSnippet() {
        return snippet;
    }

    public void setSnippet(String snippet) {
        this.snippet = snippet;
    }

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Double getRate() {
        return rate;
    }

    public void setRate(Double rate) {
        this.rate = rate;
    }

    public boolean getVacancy() {
        return vacancy;
    }

    public void setVacancy(boolean vacancy) {
        this.vacancy = vacancy;
    }
}
