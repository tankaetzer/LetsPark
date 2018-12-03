package com.example.android.letspark.data.model;

/**
 * Immutable model class for a ActiveParking.
 */
public class ActiveParking {

    private String carNumberPlate;

    private String location;

    private long startTime;

    private long duration;

    private long endTime;

    private long timeLeft;

    private boolean timerRunning;

    public ActiveParking() {
        // Default constructor required for calls to DataSnapshot.getValue(ActiveParking.class).
    }

    public ActiveParking(String carNumberPlate, String location, long startTime, long duration,
                         long endTime, long timeLeft, boolean timerRunning) {
        this.carNumberPlate = carNumberPlate;
        this.location = location;
        this.startTime = startTime;
        this.duration = duration;
        this.endTime = endTime;
        this.timeLeft = timeLeft;
        this.timerRunning = timerRunning;
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
}
