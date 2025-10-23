package persistence;

import models.Passenger;
import models.Ticket;
import models.Train;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class FileHandler {

    // Save passenger list to a text file
    public static void savePassengers(List<Passenger> passengers, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Passenger p : passengers) {
                bw.write(p.getPassengerId() + "," + escape(p.getName()) + "," + p.getBalance());
                bw.newLine();
            }
            System.out.println("Passengers saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving passengers: " + e.getMessage());
        }
    }

    // Load passengers from file
    public static void loadPassengers(List<Passenger> passengers, String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            System.out.println("Passengers file not found: " + filename);
            return;
        }
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                String[] parts = splitCsv(line, 3);
                int id = Integer.parseInt(parts[0]);
                String name = unescape(parts[1]);
                double balance = Double.parseDouble(parts[2]);
                passengers.add(new Passenger(id, name, balance));
            }
            System.out.println("Passengers loaded from " + filename);
        } catch (IOException e) {
            System.out.println("Error loading passengers: " + e.getMessage());
        }
    }

    // Save tickets to a text file (CSV): ticketId,passengerId,passengerName,passengerBalance,trainId,trainName,seats
    public static void saveTickets(List<Ticket> tickets, String filename) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(filename))) {
            for (Ticket t : tickets) {
                int ticketId = t.getTicketId();
                int passengerId = t.getPassenger().getPassengerId();
                String passengerName = escape(t.getPassenger().getName());
                double passengerBalance = t.getPassenger().getBalance();
                int trainId = t.getTrain().getTrainId();
                String trainName = escape(t.getTrain().getTrainName());
                int seats = t.getBookedSeats();

                bw.write(ticketId + "," + passengerId + "," + passengerName + "," + passengerBalance + ","
                        + trainId + "," + trainName + "," + seats);
                bw.newLine();
            }
            System.out.println("Tickets saved to " + filename);
        } catch (IOException e) {
            System.out.println("Error saving tickets: " + e.getMessage());
        }
    }

    // Load tickets from a text file (CSV). Requires Train and Passenger reconstruction.
    // If you have services or maps to resolve passengers/trains, adapt accordingly.
    public static List<Ticket> loadTickets(String filename) {
        File f = new File(filename);
        if (!f.exists()) {
            System.out.println("Tickets file not found: " + filename);
            return new ArrayList<>();
        }
        List<Ticket> tickets = new ArrayList<>();
        try (BufferedReader br = new BufferedReader(new FileReader(filename))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.isBlank()) continue;
                // ticketId,passengerId,passengerName,passengerBalance,trainId,trainName,seats
                String[] parts = splitCsv(line, 7);
                int ticketId = Integer.parseInt(parts[0]);

                int passengerId = Integer.parseInt(parts[1]);
                String passengerName = unescape(parts[2]);
                double passengerBalance = Double.parseDouble(parts[3]);
                Passenger passenger = new Passenger(passengerId, passengerName, passengerBalance);

                int trainId = Integer.parseInt(parts[4]);
                String trainName = unescape(parts[5]);
                // Seats available/total are not stored here; reconstruct with a minimal Train
                // You can choose a default totalSeats; adjust if you persist more train data elsewhere.
                Train train = new Train(trainId, trainName, 1000); // placeholder totalSeats

                int seats = Integer.parseInt(parts[6]);

                tickets.add(new Ticket(ticketId, passenger, train, seats));
            }
            System.out.println("Tickets loaded from " + filename);
        } catch (IOException e) {
            System.out.println("Error loading tickets: " + e.getMessage());
        }
        return tickets;
    }

    // Simple CSV escaping for commas and quotes
    private static String escape(String s) {
        if (s == null) return "";
        boolean needsQuotes = s.contains(",") || s.contains("\"") || s.contains("\n") || s.contains("\r");
        String escaped = s.replace("\"", "\"\"");
        return needsQuotes ? "\"" + escaped + "\"" : escaped;
    }

    private static String unescape(String s) {
        String trimmed = s.trim();
        if (trimmed.startsWith("\"") && trimmed.endsWith("\"") && trimmed.length() >= 2) {
            String inner = trimmed.substring(1, trimmed.length() - 1);
            return inner.replace("\"\"", "\"");
        }
        return trimmed;
    }

    private static String[] splitCsv(String line, int expected) {
        List<String> parts = new ArrayList<>(expected);
        StringBuilder sb = new StringBuilder();
        boolean inQuotes = false;
        for (int i = 0; i < line.length(); i++) {
            char c = line.charAt(i);
            if (inQuotes) {
                if (c == '"') {
                    if (i + 1 < line.length() && line.charAt(i + 1) == '"') {
                        sb.append('"');
                        i++;
                    } else {
                        inQuotes = false;
                    }
                } else {
                    sb.append(c);
                }
            } else {
                if (c == '"') {
                    inQuotes = true;
                } else if (c == ',') {
                    parts.add(sb.toString());
                    sb.setLength(0);
                } else {
                    sb.append(c);
                }
            }
        }
        parts.add(sb.toString());
        return parts.toArray(new String[0]);
    }
}
