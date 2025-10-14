package services;

import models.Train;
import models.Passenger;
import models.Ticket;
import exceptions.InvalidBookingException;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingService {
    private static AtomicInteger ticketCounter = new AtomicInteger(1);
    private List<Ticket> bookingHistory;

    public BookingService() {
        bookingHistory = Collections.synchronizedList(new ArrayList<>());
    }

    // Book tickets with exception handling
    public synchronized Ticket bookTicket(Passenger passenger, Train train, int seats, double costPerSeat) throws InvalidBookingException {
        if (seats <= 0) {
            throw new InvalidBookingException("Cannot book zero or negative seats!");
        }

        double totalCost = seats * costPerSeat;
        if (passenger.getBalance() < totalCost) {
            throw new InvalidBookingException("Insufficient balance to book tickets!");
        }

        boolean booked = train.bookSeats(seats);
        if (!booked) {
            throw new InvalidBookingException("Not enough available seats!");
        }

        passenger.deductBalance(totalCost);
        Ticket ticket = new Ticket(ticketCounter.getAndIncrement(), passenger, train, seats);
        bookingHistory.add(ticket);
        return ticket;
    }

    public List<Ticket> getBookingHistory() {
        return bookingHistory;
    }

    // Deadlock simulation
    public void simulateDeadlock(Passenger passenger1, Train train1, Passenger passenger2, Train train2, int seats, double costPerSeat) {
        Thread t1 = new Thread(() -> {
            synchronized (train1) {
                System.out.println("Thread1 locked " + train1.getTrainName());
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                synchronized (passenger1) {
                    System.out.println("Thread1 locked " + passenger1.getName());
                }
            }
        });

        Thread t2 = new Thread(() -> {
            synchronized (passenger2) {
                System.out.println("Thread2 locked " + passenger2.getName());
                try { Thread.sleep(100); } catch (InterruptedException e) {}
                synchronized (train2) {
                    System.out.println("Thread2 locked " + train2.getTrainName());
                }
            }
        });

        t1.start();
        t2.start();
    }
}
