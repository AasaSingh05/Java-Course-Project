package models;

import java.io.Serializable;

public class Train implements Serializable {
    private int trainId;
    private String trainName;
    private int totalSeats;
    private int availableSeats;
    private double pricePerSeat; 

    public Train(int trainId, String trainName, int totalSeats) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.pricePerSeat = 100.0; // default if legacy constructor used
    }

    // New convenience constructor
    public Train(int trainId, String trainName, int totalSeats, double pricePerSeat) {
        this.trainId = trainId;
        this.trainName = trainName;
        this.totalSeats = totalSeats;
        this.availableSeats = totalSeats;
        this.pricePerSeat = pricePerSeat;
    }

    public int getTrainId() { return trainId; }
    public String getTrainName() { return trainName; }
    public int getTotalSeats() { return totalSeats; }
    public int getAvailableSeats() { return availableSeats; }
    public double getPricePerSeat() { return pricePerSeat; } // NEW

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
