package com.example.android.letspark.service;

import android.support.test.filters.SmallTest;
import android.support.test.runner.AndroidJUnit4;

import org.junit.Before;
import org.junit.runner.RunWith;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Unit tests for the implementation of DistanceMatrixService.
 */
@SmallTest
@RunWith(AndroidJUnit4.class)
public class DistanceMatrixServiceTest {

    private HttpLoggingInterceptor httpLoggingInterceptor;

    private OkHttpClient okHttpClient;

    private Retrofit retrofit;

    private Api api;

    private String baseUrl = "https://maps.googleapis.com/maps/api/";

    private DistanceMatrixService distanceMatrixService;

    @Before
    public void setup() {
        httpLoggingInterceptor = new HttpLoggingInterceptor()
                .setLevel(HttpLoggingInterceptor.Level.BODY);

        okHttpClient = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).
                writeTimeout(30, TimeUnit.SECONDS).addInterceptor(httpLoggingInterceptor)
                .build();

        retrofit = new Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .baseUrl(baseUrl)
                .client(okHttpClient)
                .build();

        api = retrofit.create(Api.class);

        distanceMatrixService = new DistanceMatrixService(api);
    }


//      TODO: Make the test fail.
//    @Test
//    public void getDistanceMatrixResponse_originAndDestinationAvailable_firesOnDistanceAndDurationReceived() {
//
//        String originLatLng = "4.007394,101.034353";
//        String destinationLatLng = "4.017991,101.044003";
//
//        distanceMatrixService.getDistanceMatrixResponse(originLatLng, destinationLatLng, new Service.DistanceMatrixService.GetDistanceMatrixResponseCallback() {
//            @Override
//            public void onDistanceAndDurationReceived(String distance, String duration) {
//                assertNull(distance);
//            }
//
//            @Override
//            public void onNoInternet() {
//                fail("No internet");
//            }
//        });
//    }
}
