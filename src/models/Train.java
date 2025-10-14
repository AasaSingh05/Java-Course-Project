package models;

import java.io.Serializable;

public class Train implements Serializable {
    private int trainId;
    private String trainName;
    private int totalSeats;
    private int availableSeats;

    public Train(int trainId, String trainName, int totalSeats) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
    }

    public int getTrainId() {
        return trainId;
    }

    public String getTrainName() {
        return trainName;
    }

    public int getTotalSeats() {
        return totalSeats;
    }

    public int getAvailableSeats() {
        return availableSeats;
    }

    // Synchronized booking to prevent overbooking
    public synchronized boolean bookSeats(int numSeats) {
        if (numSeats <= 0) return false;
        if (numSeats <= availableSeats) {
            availableSeats -= numSeats;
            return true;
        }
        return false;
    }

    public synchronized void cancelSeats(int numSeats) {
        availableSeats += numSeats;
        if (availableSeats > totalSeats) availableSeats = totalSeats;
    }

    @Override
    public String toString() {
        return "Train [ID=" + trainId + ", Name=" + trainName + ", TotalSeats=" + totalSeats + ", AvailableSeats=" + availableSeats + "]";
    }
}
