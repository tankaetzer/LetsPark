package com.example.android.letspark.service;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * API interface to Google Distance Matrix service.
 */
public interface Api {
    /**
     * Request Google Distance Matrix API by calling URL form as below:
     * https://maps.googleapis.com/maps/api/distancematrix/json?origins=a,b&destinations=c,d&key=YOUR_API_KEY
     *
     * @param key          Google API key
     * @param origins      current location in a,b coordinate form where a is latitude, b is longitude
     * @param destinations destination in a,b coordinate form where c is latitude, d is longitude
     * @return JSON Result
     */
    @GET("distancematrix/json")
    Call<DistanceMatrix> getDistance(// origins/destinations:  LatLng as string
                                     @Query("origins") String origins,
                                     @Query("destinations") String destinations,
                                     @Query("key") String key);
}
