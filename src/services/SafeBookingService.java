package services;

import models.Passenger;
import models.Ticket;
import models.Train;

/**
 * Example of deadlock-free booking by locking in a consistent order
 * across two shared resources (Passenger and Train).
 */
public class SafeBookingService extends BookingService {

    public synchronized Ticket safeBook(Passenger passenger, Train train, int seats, double costPerSeat) throws Exception {
        Object first, second;
        // Order locks by identity hash to ensure all threads take locks in the same order
        if (System.identityHashCode(passenger) < System.identityHashCode(train)) {
            first = passenger; second = train;
        } else {
            first = train; second = passenger;
        }
        synchronized (first) {
            synchronized (second) {
                // Delegate to base booking which already checks seats and records ticket
                return super.bookTicket(passenger, train, seats, costPerSeat);
            }
        }
    }
}
