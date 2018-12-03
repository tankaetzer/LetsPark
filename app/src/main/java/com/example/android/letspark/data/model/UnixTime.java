package com.example.android.letspark.data.model;

/**
 * Immutable model class for a UnixTime.
 */
public class UnixTime {

    private Long currentTime;

    public UnixTime() {
        // Default constructor required for calls to DataSnapshot.getValue(UnixTime.class).
    }

    public UnixTime(Long currentTime) {
        this.currentTime = currentTime;
    }

    public Long getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(Long currentTime) {
        this.currentTime = currentTime;
    }
}
