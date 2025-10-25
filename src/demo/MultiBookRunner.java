package demo;

import models.Passenger;
import models.Train;
import services.BookingService;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.concurrent.CountDownLatch;

/**
 * Spawns multiple threads that try to book seats on the same train to prove synchronization.
 */
public class MultiBookRunner {

    public static void run(BookingService bookingService, Train train, int threads, int seatsPerThread, double pricePerSeat) {
        System.out.println("[Concurrency] Start — Train " + train.getTrainName() + " available=" + train.getAvailableSeats());
        CountDownLatch latch = new CountDownLatch(threads);
        NumberFormat inr = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

        for (int i = 0; i < threads; i++) {
            final int idx = i + 1;
            new Thread(() -> {
                try {
                    Passenger p = new Passenger(10_000 + idx, "T" + idx, 0.0);
                    bookingService.bookTicket(p, train, seatsPerThread, pricePerSeat);
                    System.out.println("[Concurrency] T" + idx + " booked " + seatsPerThread + " seats at " + inr.format(pricePerSeat));
                } catch (Exception ex) {
                    System.out.println("[Concurrency] T" + idx + " failed: " + ex.getMessage());
                } finally {
                    latch.countDown();
                }
            }, "Booker-" + idx).start();
        }

        try { latch.await(); } catch (InterruptedException ignored) {}
        System.out.println("[Concurrency] End — Train " + train.getTrainName() + " available=" + train.getAvailableSeats());
    }
}
