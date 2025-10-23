package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import models.Passenger;
import models.Ticket;
import models.Train;
import services.BookingService;
import services.PassengerService;
import services.TrainService;
import persistence.FileHandler;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * JavaFX UI for browsing trains, creating passengers from user input,
 * booking tickets at train-specific prices, and viewing booking history.
 */
public class BookingUI extends Application {

    // Services provide in-memory data and booking operations.
    private final TrainService trainService = new TrainService();
    private final PassengerService passengerService = new PassengerService();
    private final BookingService bookingService = new BookingService();

    // Simple local ID generator for ad-hoc passengers created from the form.
    private final AtomicInteger passengerIdSeq = new AtomicInteger(1000);

    // UI nodes shared across methods.
    private ListView<String> trainListView;
    private ListView<String> passengerListView;
    private TextArea bookingHistoryArea;

    // Currency formatting for price rendering and total cost display.
    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(Locale.getDefault());

    @Override
    public void start(Stage primaryStage) {
        // Root layout and spacing.
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        // Side-by-side lists: trains and current passengers.
        HBox listsBox = new HBox(10);

        trainListView = new ListView<>();
        updateTrainList();
        VBox trainBox = new VBox(5, new Label("Available Trains:"), trainListView);

        passengerListView = new ListView<>();
        updatePassengerList();
        VBox passengerBox = new VBox(5, new Label("Existing Passengers:"), passengerListView);

        listsBox.getChildren().addAll(trainBox, passengerBox);

        // Booking form for creating a passenger and booking seats.
        GridPane bookingGrid = new GridPane();
        bookingGrid.setHgap(10);
        bookingGrid.setVgap(8);

        TextField nameField = new TextField();
        nameField.setPromptText("Passenger name");

        TextField balanceField = new TextField();
        balanceField.setPromptText("Balance (e.g., 500.00)");

        TextField seatsField = new TextField();
        seatsField.setPromptText("Seats to Book");

        Button bookButton = new Button("Book Ticket");

        bookingGrid.addRow(0, new Label("Name:"), nameField);
        bookingGrid.addRow(1, new Label("Balance:"), balanceField);
        bookingGrid.addRow(2, new Label("Tickets:"), seatsField);
        bookingGrid.add(bookButton, 1, 3);

        // Area to print successful bookings line-by-line.
        bookingHistoryArea = new TextArea();
        bookingHistoryArea.setEditable(false);
        bookingHistoryArea.setPrefHeight(200);

        root.getChildren().addAll(
                listsBox,
                new Label("New Booking:"),
                bookingGrid,
                new Label("Booking History:"),
                bookingHistoryArea
        );

        // Booking action:
        // 1) Validate inputs.
        // 2) Create a Passenger from the form.
        // 3) Use the train's price-per-seat for billing.
        // 4) Persist lists to text files for durability between runs.
        bookButton.setOnAction(e -> {
            String selectedTrain = trainListView.getSelectionModel().getSelectedItem();
            if (selectedTrain == null) {
                showAlert("Error", "Please select a train.");
                return;
            }

            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String balanceText = balanceField.getText() == null ? "" : balanceField.getText().trim();
            String seatsText = seatsField.getText() == null ? "" : seatsField.getText().trim();

            if (name.isEmpty()) {
                showAlert("Error", "Name is required.");
                return;
            }

            double balance;
            int seats;
            try {
                balance = Double.parseDouble(balanceText);
            } catch (NumberFormatException nfe) {
                showAlert("Error", "Balance must be a valid number.");
                return;
            }
            try {
                seats = Integer.parseInt(seatsText);
            } catch (NumberFormatException nfe) {
                showAlert("Error", "Tickets must be a whole number.");
                return;
            }
            if (balance < 0) {
                showAlert("Error", "Balance cannot be negative.");
                return;
            }
            if (seats <= 0) {
                showAlert("Error", "Tickets must be greater than zero.");
                return;
            }

            try {
                // Parse selected train id from list cell prefix "id: name ..."
                int trainId = Integer.parseInt(selectedTrain.split(":")[0]);

                Train train = trainService.getTrainById(trainId);

                // Create and add a new passenger to the in-memory store.
                int newId = passengerIdSeq.getAndIncrement();
                Passenger newPassenger = new Passenger(newId, name, balance);
                passengerService.addPassenger(newPassenger);

                // Bill using the train-specific price.
                double costPerSeat = train.getPricePerSeat();
                Ticket ticket = bookingService.bookTicket(newPassenger, train, seats, costPerSeat);

                double total = seats * costPerSeat;
                showAlert("Success", "Ticket booked successfully!\n"
                        + ticket.toString() + "\nTotal: " + currencyFmt.format(total));

                // Refresh UI lists and history after booking.
                updateTrainList();
                updatePassengerList();
                updateBookingHistory();

                // Persist to text files for durability.
                FileHandler.savePassengers(passengerService.getAllPassengers(), "output/passengers.txt");
                FileHandler.saveTickets(bookingService.getBookingHistory(), "output/tickets.txt");

                // Clear form fields after success.
                nameField.clear();
                balanceField.clear();
                seatsField.clear();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        Scene scene = new Scene(root, 720, 480);
        primaryStage.setTitle("Railway Reservation System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Rebuilds the train list with live availability and formatted price per seat.
     */
    private void updateTrainList() {
        trainListView.getItems().clear();
        for (Train t : trainService.getAllTrains()) {
            String price = currencyFmt.format(t.getPricePerSeat());
            trainListView.getItems().add(
                t.getTrainId() + ": " + t.getTrainName()
                + " (Available: " + t.getAvailableSeats() + ")"
                + " — Price: " + price
            );
        }
    }

    /**
     * Shows the current passengers for visibility and quick checks.
     */
    private void updatePassengerList() {
        passengerListView.getItems().clear();
        for (Passenger p : passengerService.getAllPassengers()) {
            passengerListView.getItems().add(
                p.getPassengerId() + ": " + p.getName() + " (Balance: " + p.getBalance() + ")"
            );
        }
    }

    /**
     * Prints each booked ticket on a separate line.
     */
    private void updateBookingHistory() {
        bookingHistoryArea.clear();
        List<Ticket> tickets = bookingService.getBookingHistory();
        for (Ticket t : tickets) {
            bookingHistoryArea.appendText(t.toString() + "\n");
        }
    }

    /**
     * Uniform alert helper for success and error messages.
     */
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
