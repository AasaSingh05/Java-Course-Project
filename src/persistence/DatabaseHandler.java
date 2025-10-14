package persistence;

import models.Passenger;
import models.Train;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

// Handles JDBC database operations
public class DatabaseHandler {
    private static final String DB_URL = "jdbc:sqlite:resources/trains.db";

    public static void initializeDatabase() {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement()) {

            // Create train table
            String trainTable = "CREATE TABLE IF NOT EXISTS trains (" +
                    "trainId INTEGER PRIMARY KEY," +
                    "trainName TEXT NOT NULL," +
                    "totalSeats INTEGER NOT NULL," +
                    "availableSeats INTEGER NOT NULL)";
            stmt.execute(trainTable);

            // Create passenger table
            String passengerTable = "CREATE TABLE IF NOT EXISTS passengers (" +
                    "passengerId INTEGER PRIMARY KEY," +
                    "name TEXT NOT NULL," +
                    "balance REAL NOT NULL)";
            stmt.execute(passengerTable);

            System.out.println("Database initialized successfully.");
        } catch (SQLException e) {
            System.out.println("Error initializing database: " + e.getMessage());
        }
    }

    // Insert train
    public static void addTrain(Train train) {
        String sql = "INSERT OR REPLACE INTO trains(trainId, trainName, totalSeats, availableSeats) VALUES(?,?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, train.getTrainId());
            pstmt.setString(2, train.getTrainName());
            pstmt.setInt(3, train.getTotalSeats());
            pstmt.setInt(4, train.getAvailableSeats());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding train: " + e.getMessage());
        }
    }

    // Insert passenger
    public static void addPassenger(Passenger passenger) {
        String sql = "INSERT OR REPLACE INTO passengers(passengerId, name, balance) VALUES(?,?,?)";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, passenger.getPassengerId());
            pstmt.setString(2, passenger.getName());
            pstmt.setDouble(3, passenger.getBalance());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error adding passenger: " + e.getMessage());
        }
    }

    // Fetch all trains
    public static List<Train> getAllTrains() {
        List<Train> trains = new ArrayList<>();
        String sql = "SELECT * FROM trains";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                trains.add(new Train(rs.getInt("trainId"),
                        rs.getString("trainName"),
                        rs.getInt("totalSeats")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching trains: " + e.getMessage());
        }
        return trains;
    }

    // Fetch all passengers
    public static List<Passenger> getAllPassengers() {
        List<Passenger> passengers = new ArrayList<>();
        String sql = "SELECT * FROM passengers";
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                passengers.add(new Passenger(rs.getInt("passengerId"),
                        rs.getString("name"),
                        rs.getDouble("balance")));
            }
        } catch (SQLException e) {
            System.out.println("Error fetching passengers: " + e.getMessage());
        }
        return passengers;
    }
}
