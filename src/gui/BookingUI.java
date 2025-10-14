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

import java.util.List;

public class BookingUI extends Application {

    private TrainService trainService = new TrainService();
    private PassengerService passengerService = new PassengerService();
    private BookingService bookingService = new BookingService();

    private ListView<String> trainListView;
    private ListView<String> passengerListView;
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

        // Passenger List
        passengerListView = new ListView<>();
        updatePassengerList();
        VBox passengerBox = new VBox(5, new Label("Passengers:"), passengerListView);

        listsBox.getChildren().addAll(trainBox, passengerBox);

        // Booking Section
        HBox bookingBox = new HBox(10);
        TextField seatsField = new TextField();
        seatsField.setPromptText("Seats to Book");
        Button bookButton = new Button("Book Ticket");

        bookingBox.getChildren().addAll(seatsField, bookButton);

        // Booking History
        bookingHistoryArea = new TextArea();
        bookingHistoryArea.setEditable(false);
        bookingHistoryArea.setPrefHeight(200);

        root.getChildren().addAll(listsBox, bookingBox, new Label("Booking History:"), bookingHistoryArea);

        // --- Button Action ---
        bookButton.setOnAction(e -> {
            String selectedTrain = trainListView.getSelectionModel().getSelectedItem();
            String selectedPassenger = passengerListView.getSelectionModel().getSelectedItem();
            if (selectedTrain == null || selectedPassenger == null) {
                showAlert("Error", "Please select a train and a passenger!");
                return;
            }

            try {
                int trainId = Integer.parseInt(selectedTrain.split(":")[0]);
                int passengerId = Integer.parseInt(selectedPassenger.split(":")[0]);
                int seats = Integer.parseInt(seatsField.getText());

                Train train = trainService.getTrainById(trainId);
                Passenger passenger = passengerService.getPassengerById(passengerId);

                double costPerSeat = 100; // fixed cost
                Ticket ticket = bookingService.bookTicket(passenger, train, seats, costPerSeat);

                showAlert("Success", "Ticket booked successfully!\n" + ticket.toString());
                updateTrainList();
                updatePassengerList();
                updateBookingHistory();
            } catch (NumberFormatException ex) {
                showAlert("Error", "Please enter a valid number of seats.");
            } catch (Exception ex) {
                showAlert("Error", ex.getMessage());
            }
        });

        // --- Scene ---
        Scene scene = new Scene(root, 600, 400);
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
        alert.setContentText(message);
        alert.showAndWait();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
