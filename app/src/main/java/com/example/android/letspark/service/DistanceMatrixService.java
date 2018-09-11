package com.example.android.letspark.service;

import javax.annotation.ParametersAreNonnullByDefault;
import javax.inject.Inject;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of Distance Matrix API service.
 */
public class DistanceMatrixService implements Service.DistanceMatrixService {

    private Api api;

    @Inject
    public DistanceMatrixService(Api api) {
        this.api = checkNotNull(api);
    }

    @Override
    public void getDistanceMatrixResponse(String originLatLng, String destinationLatLng,
                                          final GetDistanceMatrixResponseCallback callback) {
        Call<DistanceMatrix> call = api.getDistance(originLatLng,
                destinationLatLng, "AIzaSyAiS4KWFjm4lSdwB-7P1v_z_HezzF2_a2U");
        call.enqueue(new Callback<DistanceMatrix>() {
            @Override
            @ParametersAreNonnullByDefault
            public void onResponse(Call<DistanceMatrix> call, Response<DistanceMatrix> response) {

                DistanceMatrix resultDistance = response.body();

                if ("OK".equalsIgnoreCase(resultDistance.status)) {
                    DistanceMatrix.DistanceMatrixInfo infoDistanceMatrix;
                    DistanceMatrix.DistanceMatrixInfo.DistanceElement distanceElement;
                    infoDistanceMatrix = resultDistance.rows.get(0);
                    distanceElement = infoDistanceMatrix.elements.get(0);
                    if ("OK".equalsIgnoreCase(distanceElement.status)) {
                        DistanceMatrix.DistanceMatrixInfo.ValueItem itemDuration;
                        DistanceMatrix.DistanceMatrixInfo.ValueItem itemDistance;
                        itemDuration = distanceElement.duration;
                        itemDistance = distanceElement.distance;
                        String distance = String.valueOf(itemDistance.text);
                        String duration = String.valueOf(itemDuration.text);

                        callback.onDistanceAndDurationReceived(distance, duration);
                    }
                }
            }

            @Override
            @ParametersAreNonnullByDefault
            public void onFailure(Call<DistanceMatrix> call, Throwable t) {
                call.cancel();
                callback.onNoInternet();
            }
        });
    }
}