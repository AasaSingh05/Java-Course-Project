package persistence;

import models.Train;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Handles SQLite database operations for trains and passengers.
 */
public class DatabaseHandler {

    private static final String DB_URL = "jdbc:sqlite:resources/trains.db";

    public static void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            System.err.println("SQLite JDBC driver not found: " + e.getMessage());
        }
        
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement()) {
            
            // Create trains table
            st.executeUpdate("CREATE TABLE IF NOT EXISTS trains(" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT, " +
                    "totalSeats INTEGER, " +
                    "price REAL)");
            
            // Create passengers table
            st.executeUpdate("CREATE TABLE IF NOT EXISTS passengers(" +
                    "id INTEGER PRIMARY KEY, " +
                    "name TEXT)");
            
            System.out.println("[JDBC] Database initialized successfully");
        } catch (SQLException e) {
            System.err.println("[JDBC] Error initializing database: " + e.getMessage());
        }
    }

    public static void insertPassenger(int id, String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT OR REPLACE INTO passengers(id, name) VALUES (?,?)")) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.executeUpdate();
            System.out.println("[JDBC] Inserted passenger " + id + ": " + name);
        } catch (SQLException e) {
            System.err.println("[JDBC] insertPassenger error: " + e.getMessage());
        }
    }

    public static List<Train> loadTrains() {
        List<Train> list = new ArrayList<>();
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id, name, totalSeats, price FROM trains")) {
            
            while (rs.next()) {
                list.add(new Train(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getInt("totalSeats"),
                        rs.getDouble("price")
                ));
            }
            System.out.println("[JDBC] Loaded " + list.size() + " trains from DB");
        } catch (SQLException e) {
            System.err.println("[JDBC] loadTrains error: " + e.getMessage());
        }
        return list;
    }

    public static void insertTrain(Train train) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement(
                     "INSERT OR REPLACE INTO trains(id, name, totalSeats, price) VALUES (?,?,?,?)")) {
            ps.setInt(1, train.getTrainId());
            ps.setString(2, train.getTrainName());
            ps.setInt(3, train.getTotalSeats());
            ps.setDouble(4, train.getPricePerSeat());
            ps.executeUpdate();
            System.out.println("[JDBC] Inserted train " + train.getTrainId() + ": " + train.getTrainName());
        } catch (SQLException e) {
            System.err.println("[JDBC] insertTrain error: " + e.getMessage());
        }
    }
}
