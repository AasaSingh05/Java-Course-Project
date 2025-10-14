import gui.BookingUI;
import persistence.DatabaseHandler;
import persistence.FileHandler;
import models.Passenger;
import models.Ticket;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {

        // --- Initialize Database ---
        DatabaseHandler.initializeDatabase();

        // --- Load passengers from file ---
        List<Passenger> passengers = new ArrayList<>();
        FileHandler.loadPassengers(passengers, "output/passengers.txt");

        // --- Load tickets from file ---
        List<Ticket> tickets = FileHandler.loadTickets("output/tickets.ser");
        if (tickets == null) tickets = new ArrayList<>();

        // --- Launch JavaFX GUI ---
        Application.launch(BookingUI.class, args);

        // --- On exit: save passengers and tickets ---
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            FileHandler.savePassengers(passengers, "output/passengers.txt");
            FileHandler.saveTickets(tickets, "output/tickets.ser");
            System.out.println("Data saved successfully on exit.");
        }));
    }
}
