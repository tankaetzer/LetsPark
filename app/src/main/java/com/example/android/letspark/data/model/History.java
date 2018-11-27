package com.example.android.letspark.data.model;

/**
 * Immutable model class for a History.
 */
public class History {

    private String carNumberPlate;

    private String location;

    private long startTime;

    private String transactionId;

    private double payment;

    private int duration;

    public History() {
        // Default constructor required for calls to DataSnapshot.getValue(History.class).
    }

    public History(String carNumberPlate,
                   String location,
                   long startTime,
                   String transactionId,
                   double payment,
                   int duration) {
        this.carNumberPlate = carNumberPlate;
        this.location = location;
        this.startTime = startTime;
        this.transactionId = transactionId;
        this.payment = payment;
        this.duration = duration;
    }

    public String getCarNumberPlate() {
        return carNumberPlate;
    }

    public void setCarNumberPlate(String carNumberPlate) {
        this.carNumberPlate = carNumberPlate;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public double getPayment() {
        return payment;
    }

    public void setPayment(double payment) {
        this.payment = payment;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}
