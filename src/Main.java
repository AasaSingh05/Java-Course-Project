import gui.BookingApp;
import persistence.DatabaseHandler;
import persistence.FileHandler;
import persistence.TicketSerializer;
import models.Passenger;
import models.Ticket;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.List;

/**
 * Application entry point with serialization demo and JDBC initialization.
 */
public class Main {

    private static final List<Passenger> passengers = new ArrayList<>();
    private static List<Ticket> tickets = new ArrayList<>();

    public static void main(String[] args) {
        // Initialize SQLite database
        DatabaseHandler.initializeDatabase();

        // Load passengers from text file
        FileHandler.loadPassengers(passengers, "output/passengers.txt");

        // Load tickets from text file
        List<Ticket> loaded = FileHandler.loadTickets("output/tickets.txt");
        if (loaded != null) {
            tickets = loaded;
        }

        // Demonstrate deserialization
        List<Ticket> serializedTickets = TicketSerializer.loadSerialized("output/tickets.ser");
        System.out.println("[Serialization] Previously saved tickets: " + serializedTickets.size());

        // Register shutdown hook for persistence
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            // Save to text files
            FileHandler.savePassengers(passengers, "output/passengers.txt");
            FileHandler.saveTickets(tickets, "output/tickets.txt");
            
            // Demonstrate serialization
            TicketSerializer.saveSerialized(tickets, "output/tickets.ser");
            
            System.out.println("[Shutdown] Data saved successfully (text + serialized).");
        }));

        // Launch JavaFX UI
        Application.launch(BookingApp.class, args);
    }
}
