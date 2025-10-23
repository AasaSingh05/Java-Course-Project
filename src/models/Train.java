package models;

import java.io.Serializable;

/**
 * Domain model for a Train.
 * Holds identity, naming, seating capacity, live availability, and price per seat.
 */
public class Train implements Serializable {
    private int trainId;
    private String trainName;
    private int totalSeats;
    private int availableSeats;
    private double pricePerSeat;

    /**
     * Legacy constructor that keeps existing call sites working.
     * Assigns a sensible default price when not specified.
     */
    public Train(int trainId, String trainName, int totalSeats) {
        this(trainId, trainName, totalSeats, 100.0);
    }

    /**
     * Primary constructor that also sets the price per seat.
     */
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

    /**
     * Price used to compute booking charges.
     */
    public double getPricePerSeat() { return pricePerSeat; }

    /**
     * Attempts to reserve seats atomically.
     * Returns true if reservation succeeds, false if insufficient seats.
     */
    public synchronized boolean bookSeats(int numSeats) {
        if (numSeats <= 0) return false;
        if (numSeats <= availableSeats) {
            availableSeats -= numSeats;
            return true;
        }
        return false;
    }

    /**
     * Releases seats back to availability and clamps to total capacity.
     */
    public synchronized void cancelSeats(int numSeats) {
        availableSeats += numSeats;
        if (availableSeats > totalSeats) availableSeats = totalSeats;
    }

    @Override
    public String toString() {
        return "Train [ID=" + trainId
                + ", Name=" + trainName
                + ", TotalSeats=" + totalSeats
                + ", AvailableSeats=" + availableSeats
                + ", PricePerSeat=" + pricePerSeat + "]";
    }
}
