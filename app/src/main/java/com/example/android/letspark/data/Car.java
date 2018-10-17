package com.example.android.letspark.data;

/**
 * Immutable model class for a Car.
 */
public class Car {

    private String carNumberPlate;

    private String key;

    public Car() {
        // Default constructor required for calls to DataSnapshot.getValue(Post.class).
    }

    public Car(String carNumberPlate, String key) {
        setCarNumberPlate(carNumberPlate);
        setKey(key);
    }

    public String getCarNumberPlate() {
        return carNumberPlate;
    }

    private void setCarNumberPlate(String carNumberPlate) {
        this.carNumberPlate = carNumberPlate;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
