package persistence;

import models.Ticket;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Demonstrates object serialization for tickets alongside your text I/O.
 */
public class TicketSerializer {

    public static void saveSerialized(List<Ticket> tickets, String path) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(path))) {
            oos.writeObject(tickets);
        } catch (IOException e) {
            System.err.println("[Serialize] saveSerialized error: " + e.getMessage());
        }
    }

    @SuppressWarnings("unchecked")
    public static List<Ticket> loadSerialized(String path) {
        File f = new File(path);
        if (!f.exists()) return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            Object obj = ois.readObject();
            if (obj instanceof List) return (List<Ticket>) obj;
        } catch (IOException | ClassNotFoundException e) {
            System.err.println("[Serialize] loadSerialized error: " + e.getMessage());
        }
        return new ArrayList<>();
    }
}
