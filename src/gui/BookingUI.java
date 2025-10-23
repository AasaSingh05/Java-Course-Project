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

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class BookingUI extends Application {

    private final TrainService trainService = new TrainService();
    private final PassengerService passengerService = new PassengerService();
    private final BookingService bookingService = new BookingService();

    private final AtomicInteger passengerIdSeq = new AtomicInteger(1000); // simple id generator for new passengers

    private ListView<String> trainListView;
    private ListView<String> passengerListView; // kept for visibility of existing passengers
    private TextArea bookingHistoryArea;

    @Override
    public void start(Stage primaryStage) {
        // --- Layouts ---
        VBox root = new VBox(10);
        root.setPadding(new Insets(10));

        HBox listsBox = new HBox(10);

        // Train List
        trainListView = new ListView<>();
        updateTrainList();
        VBox trainBox = new VBox(5, new Label("Available Trains:"), trainListView);

        // Passenger List (shows existing passengers)
        passengerListView = new ListView<>();
        updatePassengerList();
        VBox passengerBox = new VBox(5, new Label("Existing Passengers:"), passengerListView);

        listsBox.getChildren().addAll(trainBox, passengerBox);

        // Booking Section with user inputs
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

        // Booking History
        bookingHistoryArea = new TextArea();
        bookingHistoryArea.setEditable(false);
        bookingHistoryArea.setPrefHeight(200);

        root.getChildren().addAll(listsBox, new Label("New Booking:"), bookingGrid, new Label("Booking History:"), bookingHistoryArea);

        // --- Button Action ---
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
                int trainId = Integer.parseInt(selectedTrain.split(":")[0]);
                Train train = trainService.getTrainById(trainId);

                // Create a new Passenger on the fly and add to service
                int newId = passengerIdSeq.getAndIncrement();
                Passenger newPassenger = new Passenger(newId, name, balance);
                passengerService.addPassenger(newPassenger);

                double costPerSeat = 100; // fixed cost
                Ticket ticket = bookingService.bookTicket(newPassenger, train, seats, costPerSeat);

                showAlert("Success", "Ticket booked successfully!\n" + ticket.toString());
                updateTrainList();
                updatePassengerList();
                updateBookingHistory();

                // Clear inputs after success
                nameField.clear();
                balanceField.clear();
                seatsField.clear();
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        // --- Scene ---
        Scene scene = new Scene(root, 720, 480);
        primaryStage.setTitle("Railway Reservation System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void updateTrainList() {
        trainListView.getItems().clear();
        for (Train t : trainService.getAllTrains()) {
            trainListView.getItems().add(t.getTrainId() + ": " + t.getTrainName() + " (Available: " + t.getAvailableSeats() + ")");
        }
    }

    private void updatePassengerList() {
        passengerListView.getItems().clear();
        for (Passenger p : passengerService.getAllPassengers()) {
            passengerListView.getItems().add(p.getPassengerId() + ": " + p.getName() + " (Balance: " + p.getBalance() + ")");
        }
    }

    private void updateBookingHistory() {
        bookingHistoryArea.clear();
        List<Ticket> tickets = bookingService.getBookingHistory();
        for (Ticket t : tickets) {
            bookingHistoryArea.appendText(t.toString() + "\n");
        }
    }

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
