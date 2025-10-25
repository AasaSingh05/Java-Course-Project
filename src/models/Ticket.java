package models;

import java.io.Serializable;

public class Ticket implements Serializable {
    private static final long serialVersionUID = 1L;
    
    private int ticketId;
    private Passenger passenger;
    private Train train;
    private int numberOfSeats;

    public Ticket(int ticketId, Passenger passenger, Train train, int numberOfSeats) {
        this.ticketId = ticketId;
        this.passenger = passenger;
        this.train = train;
        this.numberOfSeats = numberOfSeats;
    }

    public int getTicketId() { return ticketId; }
    public Passenger getPassenger() { return passenger; }
    public Train getTrain() { return train; }
    public int getNumberOfSeats() { return numberOfSeats; }

    @Override
    public String toString() {
        return "Ticket[" + ticketId + "] " + passenger.getName() 
                + " on " + train.getTrainName() + " (" + numberOfSeats + " seats)";
    }
}
