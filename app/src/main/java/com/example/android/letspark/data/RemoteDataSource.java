package com.example.android.letspark.data;

import android.support.annotation.NonNull;

import com.example.android.letspark.data.model.Car;
import com.example.android.letspark.data.model.EmptyParkingBay;
import com.example.android.letspark.data.model.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of remote data source.
 */
public class RemoteDataSource implements DataSource {

    private List<EmptyParkingBay> emptyParkingBayList;

    private List<Car> carList;

    private DatabaseReference databaseReference;

    @Inject
    RemoteDataSource(DatabaseReference databaseReference) {
        this.databaseReference = databaseReference;
    }

    /**
     * load empty parking bays from remote data source.
     */
    @Override
    public void getEmptyParkingBays(@NonNull final LoadEmptyParkingBaysCallBack callBack) {
        checkNotNull(callBack);
        emptyParkingBayList = new ArrayList<>();

        databaseReference.child("empty-parking-bay")
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

    @Override
    public void writeNewUser(String uid, String email) {
        User user = new User(email);
        databaseReference.child("users").child(uid).setValue(user);
    }

    @Override
    public void writeCarNumberPlate(String carNumberPlate, final String uid,
                                    final LoadUserCarsCallBack callBack) {
        String key = databaseReference.child("user-cars").push().getKey();
        Car car = new Car(carNumberPlate, key);
        databaseReference.child("user-cars").child(uid).child(key).setValue(car)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        getUserCars(uid, callBack);
                    }
                });
    }

    @Override
    public void getUserCars(String uid, final LoadUserCarsCallBack callBack) {
        checkNotNull(callBack);

        carList = new ArrayList<>();

        databaseReference.child("user-cars").child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Car car = postSnapshot.getValue(Car.class);
                    carList.add(new Car(car.getCarNumberPlate(), car.getKey()));
                }
                callBack.onUserCarsLoaded(carList);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack.onCancelled(databaseError.getMessage());
            }
        });
    }

    @Override
    public void deleteCar(final String uid, String key, final LoadUserCarsCallBack callBack) {
        if (uid != null && !uid.isEmpty() && key != null && !key.isEmpty()) {
            databaseReference.child("user-cars").child(uid).child(key).removeValue()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            getUserCars(uid, callBack);
                        }
                    });

        }
    }
}
