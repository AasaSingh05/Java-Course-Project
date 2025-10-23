import gui.BookingUI;
import persistence.DatabaseHandler;
import persistence.FileHandler;
import models.Passenger;
import models.Ticket;
import javafx.application.Application;

import java.util.ArrayList;
import java.util.List;

public class Main {

    // Promote state to fields so lambdas/shutdown hook can access without "effectively final" issues
    private static final List<Passenger> passengers = new ArrayList<>(); // instance/static fields can be captured and mutated by lambdas [web:23][web:42]
    private static List<Ticket> tickets = new ArrayList<>(); // list reference stays as a field; contents can change freely [web:23][web:42]

    public static void main(String[] args) {
        // --- Initialize Database ---
        DatabaseHandler.initializeDatabase(); // SQLite is embedded; no server needed when using jdbc:sqlite:... URLs [web:31][web:27][web:33]

        // --- Load passengers from file ---
        FileHandler.loadPassengers(passengers, "output/passengers.txt"); // loads into field list to avoid local capture [web:23][web:42]

        // --- Load tickets from file ---
        List<Ticket> loaded = FileHandler.loadTickets("output/tickets.ser"); // load into temp, then assign once to field [web:23]
        if (loaded != null) {
            tickets = loaded; // fields are not subject to "effectively final" restriction for lambdas [web:42]
        }

        // --- Register shutdown hook before launching UI ---
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            FileHandler.savePassengers(passengers, "output/passengers.txt"); // capturing fields is allowed in lambdas [web:23][web:42]
            FileHandler.saveTickets(tickets, "output/tickets.ser"); // persists current state on JVM shutdown [web:35][web:36]
            System.out.println("Data saved successfully on exit.");
        })); // shutdown hooks should be quick and robust; they run on normal JVM termination [web:35][web:36]

        // --- Launch JavaFX GUI ---
        Application.launch(BookingUI.class, args); // run JavaFX; state lives in fields accessible to controllers if needed [web:11][web:15]
    }
}
