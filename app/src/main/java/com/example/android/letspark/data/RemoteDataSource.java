package com.example.android.letspark.data;

import android.support.annotation.NonNull;

import com.example.android.letspark.data.model.Car;
import com.example.android.letspark.data.model.EmptyParkingBay;
import com.example.android.letspark.data.model.History;
import com.example.android.letspark.data.model.User;
import com.example.android.letspark.idlingresource.SimpleIdlingResource;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;

import static com.google.common.base.Preconditions.checkNotNull;

/**
 * Implementation of remote data source.
 */
public class RemoteDataSource implements DataSource {

    private List<EmptyParkingBay> emptyParkingBayList;

    private List<Car> carList;

    private List<History> historyList;

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

        databaseReference.child("empty-parking-bay").addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        emptyParkingBayList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            EmptyParkingBay emptyParkingBay
                                    = postSnapshot.getValue(EmptyParkingBay.class);
                            emptyParkingBayList.add(new EmptyParkingBay(emptyParkingBay.getLat(),
                                    emptyParkingBay.getLng(),
                                    emptyParkingBay.getSnippet(),
                                    emptyParkingBay.getRate(),
                                    emptyParkingBay.getVacancy()));
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

        final SimpleIdlingResource idlingResource = new SimpleIdlingResource();

        // The IdlingResource is null in production.
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        carList = new ArrayList<>();

        databaseReference.child("user-cars")
                .child(uid).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Car car = postSnapshot.getValue(Car.class);
                    carList.add(new Car(car.getCarNumberPlate(), car.getKey()));
                }
                callBack.onUserCarsLoaded(carList);
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callBack.onCancelled(databaseError.getMessage());
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }
        });
    }

    @Override
    public void deleteCar(final String uid, String key,
                          final LoadUserCarsCallBack callBack) {
        checkNotNull(callBack);

        final SimpleIdlingResource idlingResource = new SimpleIdlingResource();

        // The IdlingResource is null in production.
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

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

    // TODO: delete once payment feature is done
    @Override
    public void writeNewTransaction(String uid, String carNumberPlate, int duration, double payment) {
        String key = databaseReference.push().getKey();
        Map<String, Object> value = new HashMap<>();
        value.put("carNumberPlate", carNumberPlate);
        value.put("location", "Kuantan");
        value.put("duration", duration);
        value.put("payment", payment);
        value.put("startTime", ServerValue.TIMESTAMP);
        value.put("transactionId", key);
        databaseReference.child("history").child(uid).child(key).setValue(value);
    }

    @Override
    public void getUserHistory(String uid,
                               final LoadUserHistoriesCallBack callBack) {
        checkNotNull(callBack);

        final SimpleIdlingResource idlingResource = new SimpleIdlingResource();

        // The IdlingResource is null in production.
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        historyList = new ArrayList<>();

        databaseReference
                .child("history")
                .child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        historyList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            History history = postSnapshot.getValue(History.class);
                            historyList.add(new History(history.getCarNumberPlate(),
                                    history.getLocation(),
                                    history.getStartTime(),
                                    history.getTransactionId(),
                                    history.getPayment(),
                                    history.getDuration()));
                        }
                        callBack.onUserHistoriesLoaded(historyList);
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callBack.onCancelled(databaseError.getMessage());
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                });
    }
}
