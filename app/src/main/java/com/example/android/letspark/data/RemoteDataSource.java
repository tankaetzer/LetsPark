package com.example.android.letspark.data;

import android.support.annotation.NonNull;

import com.example.android.letspark.data.model.ActiveParking;
import com.example.android.letspark.data.model.Car;
import com.example.android.letspark.data.model.EmptyParkingBay;
import com.example.android.letspark.data.model.History;
import com.example.android.letspark.data.model.UnixTime;
import com.example.android.letspark.data.model.User;
import com.example.android.letspark.idlingresource.SimpleIdlingResource;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

    private List<EmptyParkingBay> violatedParkingBayList;

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
                            emptyParkingBay.getVacancy(),
                            emptyParkingBay.getEndTime()));
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

    @Override
    public void writeNewTransaction(String carNumberPlate, String uid, String parking,
                                    int duration, double payment,
                                    final GetStartTimeAndTransactionIdCallback callback) {
        String key = databaseReference.push().getKey();
        Map<String, Object> value = new HashMap<>();
        value.put("carNumberPlate", carNumberPlate);
        value.put("parking", parking);
        value.put("duration", duration);
        value.put("payment", payment);
        value.put("startTime", ServerValue.TIMESTAMP);
        value.put("transactionId", key);

        final SimpleIdlingResource idlingResource = new SimpleIdlingResource();

        // The IdlingResource is null in production.
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        DatabaseReference historyReference
                = databaseReference.child("history").child(uid).child(key);

        historyReference.setValue(value);

        historyReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                History history = dataSnapshot.getValue(History.class);
                Long startTime = history.getStartTime();
                String transactionId = history.getTransactionId();
                callback.onGetStartTime(startTime, transactionId);
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                callback.onCancelled(databaseError.getMessage());
                if (idlingResource != null) {
                    idlingResource.setIdleState(true);
                }
            }
        });
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
                                    history.getParking(),
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

    @Override
    public void getCurrentUnixTime(final GetCurrentUnixTimeCallback callBack) {
        Map<String, Object> value = new HashMap<>();
        value.put("currentTime", ServerValue.TIMESTAMP);
        databaseReference.child("unix-time").setValue(value);

        final SimpleIdlingResource idlingResource = new SimpleIdlingResource();

        // The IdlingResource is null in production.
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        DatabaseReference unixTimeReference = databaseReference.child("unix-time");
        unixTimeReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                UnixTime unixTime = dataSnapshot.getValue(UnixTime.class);
                callBack.onGetCurrentUnixTime(unixTime.getCurrentTime());
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
    public void writeNewActiveParking(String uid, String carNumberPlate, String parking,
                                      long startTime, long duration, long endTime,
                                      String transactionId, double payment, final WriteActiveParkingCallback callback) {
        ActiveParking activeParking = new ActiveParking(carNumberPlate, parking, startTime,
                duration, endTime, 0, true, transactionId, payment);

        DatabaseReference activeParkingReference
                = databaseReference.child("active-parking").child(uid);

        final SimpleIdlingResource idlingResource = new SimpleIdlingResource();

        // The IdlingResource is null in production.
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        activeParkingReference.setValue(activeParking)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callback.onSuccess();
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                });

        DatabaseReference parkingReference
                = databaseReference.child("empty-parking-bay").child(parking);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/endTime/", endTime);
        parkingReference.updateChildren(childUpdates);
    }

    @Override
    public void getActiveParking(String uid, final GetActiveParkingCallback callback) {
        checkNotNull(callback);

        final SimpleIdlingResource idlingResource = new SimpleIdlingResource();

        // The IdlingResource is null in production.
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        databaseReference.child("active-parking").child(uid)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        ActiveParking activeParking = dataSnapshot.getValue(ActiveParking.class);
                        callback.onGetActiveParking(activeParking);
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callback.onCancelled(databaseError.getMessage());
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                });
    }

    @Override
    public void updateTimeLeftTimerRunningEndTime(String uid, long timeLeft, boolean timerRunning,
                                                  final UpdateTimeLeftTimerRunningEndTimeCallback callback) {
        DatabaseReference activeParkingReference
                = databaseReference.child("active-parking").child(uid);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/timeLeft/", timeLeft);
        childUpdates.put("/timerRunning/", timerRunning);

        final SimpleIdlingResource idlingResource = new SimpleIdlingResource();

        // The IdlingResource is null in production.
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        activeParkingReference.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess();
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                });
    }

    @Override
    public void updateTimerRunning(String uid, boolean timerRunning,
                                   final UpdateTimerRunningCallback callback) {
        DatabaseReference activeParkingReference
                = databaseReference.child("active-parking").child(uid);

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/timerRunning/", timerRunning);

        final SimpleIdlingResource idlingResource = new SimpleIdlingResource();

        // The IdlingResource is null in production.
        if (idlingResource != null) {
            idlingResource.setIdleState(false);
        }

        activeParkingReference.updateChildren(childUpdates)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callback.onSuccess();
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callback.onFailure(e.getMessage());
                        if (idlingResource != null) {
                            idlingResource.setIdleState(true);
                        }
                    }
                });
    }

    @Override
    public void updateExistTransaction(String uid, String transactionId, int duration, double payment) {
        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/duration/", duration);
        childUpdates.put("/payment/", payment);

        databaseReference.child("history").child(uid).child(transactionId).updateChildren(childUpdates);
    }

    @Override
    public void getViolatedParkingBays(final LoadViolatedParkingBaysCallBack callBack) {
        checkNotNull(callBack);
        violatedParkingBayList = new ArrayList<>();

        databaseReference.child("empty-parking-bay")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        violatedParkingBayList.clear();
                        for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                            EmptyParkingBay emptyParkingBay
                                    = postSnapshot.getValue(EmptyParkingBay.class);
                            violatedParkingBayList.add(new EmptyParkingBay(emptyParkingBay.getLat(),
                                    emptyParkingBay.getLng(),
                                    emptyParkingBay.getSnippet(),
                                    emptyParkingBay.getRate(),
                                    emptyParkingBay.getVacancy(),
                                    emptyParkingBay.getEndTime()));
                        }


                        callBack.onViolatedParkingBaysLoaded(violatedParkingBayList);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        callBack.onDataNotAvailable();
                    }
                });
    }
}
