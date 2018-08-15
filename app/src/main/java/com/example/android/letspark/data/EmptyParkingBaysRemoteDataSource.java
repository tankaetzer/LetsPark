package com.example.android.letspark.data;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of remote data source.
 */
public class EmptyParkingBaysRemoteDataSource implements EmptyParkingBaysDataSource {

    private static EmptyParkingBaysRemoteDataSource INSTANCE = null;

    private List<EmptyParkingBay> emptyParkingBayList;

    private EmptyParkingBaysRemoteDataSource() {
        // Prevent direct instantiation.
    }

    public static EmptyParkingBaysRemoteDataSource getInstance() {

        if (INSTANCE == null) {
            INSTANCE = new EmptyParkingBaysRemoteDataSource();
        }
        return INSTANCE;
    }

    /**
     * load empty parking bays from remote data source.
     */
    @Override
    public void getEmptyParkingBays(@NonNull final LoadEmptyParkingBaysCallBack callBack) {
        checkNotNull(callBack);

        emptyParkingBayList = new ArrayList<>();

        FirebaseDatabase.getInstance().getReference().child("empty-parking-bay")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            EmptyParkingBay emptyParkingBay = postSnapshot.getValue(EmptyParkingBay.class);
                            emptyParkingBayList.add(new EmptyParkingBay(emptyParkingBay.getLat(),
                                    emptyParkingBay.getLng(), emptyParkingBay.getSnippet()));
                        }
                        callBack.onEmptyParkingBaysLoaded(emptyParkingBayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callBack.onDataNotAvailable();
                    }
                });
    }

}
