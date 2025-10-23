package models;

import java.io.Serializable;

public class Train implements Serializable {
    private int trainId;
    private String trainName;
    private int totalSeats;
    private int availableSeats;
    private double pricePerSeat; // NEW

    // Legacy constructor keeps compatibility with existing code; assigns a default price
    public Train(int trainId, String trainName, int totalSeats) {
        this(trainId, trainName, totalSeats, 100.0);
    }

    // New constructor with price
    public Train(int trainId, String trainName, int totalSeats, double pricePerSeat) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.pricePerSeat = pricePerSeat;
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

    public double getPricePerSeat() { // NEW
        return pricePerSeat;
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
        return "Train [ID=" + trainId + ", Name=" + trainName + ", TotalSeats=" + totalSeats
                + ", AvailableSeats=" + availableSeats + ", PricePerSeat=" + pricePerSeat + "]";
    }
}
