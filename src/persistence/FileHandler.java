package persistence;

import models.Passenger;
import models.Ticket;
import models.Train;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    public static void savePassengers(List<Passenger> passengers, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Passenger p : passengers) {
                writer.write(p.getPassengerId() + "," + p.getName() + "," + p.getBalance());
                writer.newLine();
            }
            System.out.println("[File] Passengers saved to " + filename);
        } catch (IOException e) {
            System.err.println("[File] savePassengers error: " + e.getMessage());
        }
    }

    public static void loadPassengers(List<Passenger> passengers, String filename) {
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("[File] " + filename + " not found, starting fresh.");
            return;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 3) {
                    int id = Integer.parseInt(parts[0]);
                    String name = parts[1];
                    double balance = Double.parseDouble(parts[2]);
                    passengers.add(new Passenger(id, name, balance));
                }
            }
            System.out.println("[File] Passengers loaded from " + filename);
        } catch (IOException | NumberFormatException e) {
            System.err.println("[File] loadPassengers error: " + e.getMessage());
        }
    }

    public static void saveTickets(List<Ticket> tickets, String filename) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (Ticket t : tickets) {
                writer.write(t.getTicketId() + "," 
                        + t.getPassenger().getPassengerId() + ","
                        + t.getTrain().getTrainId() + ","
                        + t.getNumberOfSeats());  // FIXED: was getBookedSeats()
                writer.newLine();
            }
            System.out.println("[File] Tickets saved to " + filename);
        } catch (IOException e) {
            System.err.println("[File] saveTickets error: " + e.getMessage());
        }
    }

    public static List<Ticket> loadTickets(String filename) {
        List<Ticket> tickets = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) {
            System.out.println("[File] " + filename + " not found, starting fresh.");
            return tickets;
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length >= 4) {
                    int ticketId = Integer.parseInt(parts[0]);
                    int passengerId = Integer.parseInt(parts[1]);
                    int trainId = Integer.parseInt(parts[2]);
                    int seats = Integer.parseInt(parts[3]);
                    
                    // Note: This creates placeholder objects; for production, 
                    // you'd look up actual Passenger/Train from services
                    Passenger p = new Passenger(passengerId, "Unknown", 0);
                    Train t = new Train(trainId, "Unknown", 0, 0);
                    tickets.add(new Ticket(ticketId, p, t, seats));
                }
            }
            System.out.println("[File] Tickets loaded from " + filename);
        } catch (IOException | NumberFormatException e) {
            System.err.println("[File] loadTickets error: " + e.getMessage());
        }
        return tickets;
    }
}
