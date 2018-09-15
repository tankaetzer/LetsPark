package com.example.android.letspark.data;

import android.support.annotation.NonNull;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of remote data source.
 */
public class EmptyParkingBaysRemoteDataSource implements EmptyParkingBaysDataSource {

    private List<EmptyParkingBay> emptyParkingBayList;

    private DatabaseReference databaseReference;

    @Inject
    EmptyParkingBaysRemoteDataSource(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
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
                            EmptyParkingBay emptyParkingBay = postSnapshot
                                    .getValue(EmptyParkingBay.class);
                            emptyParkingBayList.add(new EmptyParkingBay(emptyParkingBay.getLat(),
                                    emptyParkingBay.getLng(), emptyParkingBay.getSnippet(),
                                    emptyParkingBay.getRate(), emptyParkingBay.getVacancy()));
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
