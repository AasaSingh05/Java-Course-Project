// Add imports at top if missing:
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import models.Train;

public class DatabaseHandler {

    private static final String DB_URL = "jdbc:sqlite:resources/trains.db";

    public static void initializeDatabase() {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException ignored) {}
        try (Connection conn = DriverManager.getConnection(DB_URL);
             Statement st = conn.createStatement()) {
            st.executeUpdate("CREATE TABLE IF NOT EXISTS trains(id INTEGER PRIMARY KEY, name TEXT, totalSeats INTEGER, price REAL)");
            st.executeUpdate("CREATE TABLE IF NOT EXISTS passengers(id INTEGER PRIMARY KEY, name TEXT)");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void insertPassenger(int id, String name) {
        try (Connection conn = DriverManager.getConnection(DB_URL);
             PreparedStatement ps = conn.prepareStatement("INSERT OR REPLACE INTO passengers(id, name) VALUES (?,?)")) {
            ps.setInt(1, id);
            ps.setString(2, name);
            ps.executeUpdate();
            System.out.println("[JDBC] Inserted passenger " + id + " " + name);
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
                list.add(new Train(rs.getInt(1), rs.getString(2), rs.getInt(3), rs.getDouble(4)));
            }
            System.out.println("[JDBC] Loaded " + list.size() + " trains from DB");
        } catch (SQLException e) {
            System.err.println("[JDBC] loadTrains error: " + e.getMessage());
        }
        return list;
    }
}
