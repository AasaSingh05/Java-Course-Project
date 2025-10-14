package persistence;

import models.Passenger;
import models.Ticket;

import java.io.*;
import java.util.List;

// Handles file operations and object serialization
public class FileHandler {

    // Save passenger list to a text file
    public static void savePassengers(List<Passenger> passengers, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Passenger p : passengers) {
                bw.write(p.getPassengerId() + "," + p.getName() + "," + p.getBalance());
                bw.newLine();
            }
            System.out.println("Passengers saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving passengers: " + e.getMessage());
        }
    }

    // Load passengers from file
    public static void loadPassengers(List<Passenger> passengers, String filename) {
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                Passenger p = new Passenger(Integer.parseInt(parts[0]), parts[1], Double.parseDouble(parts[2]));
                passengers.add(p);
            }
            System.out.println("Passengers loaded from " + filename);
        } catch (IOException e) {
            System.out.println("Error loading passengers: " + e.getMessage());
        }
    }

    // Serialize tickets to a file
    public static void saveTickets(List<Ticket> tickets, String filename) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(filename))) {
            oos.writeObject(tickets);
            System.out.println("Tickets saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving tickets: " + e.getMessage());
        }
    }

    // Deserialize tickets from a file
    @SuppressWarnings("unchecked")
    public static List<Ticket> loadTickets(String filename) {
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filename))) {
            return (List<Ticket>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Error loading tickets: " + e.getMessage());
            return null;
        }
    }
}
