package models;

import java.io.Serializable;

public class Ticket implements Serializable {
    private int ticketId;
    private Passenger passenger;
    private Train train;
    private int bookedSeats;

    public Ticket(int ticketId, Passenger passenger, Train train, int bookedSeats) {
        this.ticketId = ticketId;
        this.passenger = passenger;
        this.train = train;
        this.bookedSeats = bookedSeats;
    }

    public int getTicketId() {
        return ticketId;
    }

    public Passenger getPassenger() {
        return passenger;
    }

    public Train getTrain() {
        return train;
    }

    public int getBookedSeats() {
        return bookedSeats;
    }

    @Override
    public String toString() {
        return "Ticket [ID=" + ticketId + ", Passenger=" + passenger.getName() +
               ", Train=" + train.getTrainName() + ", SeatsBooked=" + bookedSeats + "]";
    }
}
