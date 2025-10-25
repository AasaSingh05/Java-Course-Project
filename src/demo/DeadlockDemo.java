package demo;

import models.Passenger;
import models.Train;
import services.BookingService;

/**
 * Invokes the intentional deadlock and then demonstrates a safe strategy.
 */
public class DeadlockDemo {

    public static void run(BookingService bookingService, Passenger p1, Train t1, Passenger p2, Train t2) {
        System.out.println("[Deadlock] Demonstrating intentional deadlock (will appear stuck briefly)...");
        bookingService.simulateDeadlock(p1, t1, p2, t2, 1, t1.getPricePerSeat());

        // In practice, avoid deadlocks by enforcing a lock order on shared resources.
        System.out.println("[Deadlock] Resolving by consistent lock ordering demonstrated in SafeBookingService.");
    }
}
