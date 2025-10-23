import gui.BookingApp;
import persistence.DatabaseHandler;
import persistence.FileHandler;
import models.Passenger;
import models.Ticket;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Application entry point that:
 * - Initializes the SQLite database (embedded, no server required)
 * - Loads passengers and tickets from text files
 * - Registers a shutdown hook to persist data
 * - Launches the modular BookingApp UI
 */
public class Main {

    // Shared state persisted on shutdown
    private static final List<Passenger> passengers = new ArrayList<>();
    private static List<Ticket> tickets = new ArrayList<>();

    public static void main(String[] args) {
        // Initialize embedded SQLite schema if needed
        DatabaseHandler.initializeDatabase();

        // Load passengers from text
        FileHandler.loadPassengers(passengers, "output/passengers.txt");

        // Load tickets from text (returns empty list if file missing in your updated FileHandler)
        List<Ticket> loaded = FileHandler.loadTickets("output/tickets.txt");
        if (loaded != null) {
            tickets = loaded;
        }

        // Persist on graceful shutdown
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            FileHandler.savePassengers(passengers, "output/passengers.txt");
            FileHandler.saveTickets(tickets, "output/tickets.txt");
            System.out.println("Data saved successfully on exit.");
        }));

        // Launch the new split UI
        Application.launch(BookingApp.class, args);
    }
}
