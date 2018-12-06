package com.example.android.letspark.data.model;

/**
 * Immutable model class for a ActiveParking.
 */
public class ActiveParking {

    private String carNumberPlate;

    private String parking;

    private String transactionId;

    private long startTime;

    private long duration;

    private long endTime;

    private long timeLeft;

    private boolean timerRunning;

    private double payment;

    public ActiveParking() {
        // Default constructor required for calls to DataSnapshot.getValue(ActiveParking.class).
    }

    public ActiveParking(String carNumberPlate, String parking, long startTime, long duration,
                         long endTime, long timeLeft, boolean timerRunning, String transactionId,
                         double payment) {
        this.carNumberPlate = carNumberPlate;
        this.parking = parking;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = endTime;
        this.timeLeft = timeLeft;
        this.timerRunning = timerRunning;
        this.transactionId = transactionId;
        this.payment = payment;
    }

    public String getCarNumberPlate() {
        return carNumberPlate;
    }

    public void setCarNumberPlate(String carNumberPlate) {
        this.carNumberPlate = carNumberPlate;
    }

    public String getParking() {
        return parking;
    }

    public void setParking(String parking) {
        this.parking = parking;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public long getTimeLeft() {
        return timeLeft;
    }

    public void setTimeLeft(long timeLeft) {
        this.timeLeft = timeLeft;
    }

    public boolean getTimerRunning() {
        return timerRunning;
    }

    public void setTimerRunning(boolean timerRunning) {
        this.timerRunning = timerRunning;
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
}
