package gui.layout;

import gui.components.BookingForm;
import gui.components.PassengerListView;
import gui.components.TrainListView;
import gui.components.Snackbar;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import models.Passenger;
import models.Ticket;
import models.Train;
import persistence.FileHandler;
import services.BookingService;
import services.PassengerService;
import services.TrainService;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Composes the app bar, trains card, passengers card, booking card, and history card.
 */
public class MainLayout {

    private final BorderPane root = new BorderPane();

    private final TrainService trainService;
    private final PassengerService passengerService;
    private final BookingService bookingService;

    // Not final because they are assigned in build()
    private TrainListView trainList;
    private PassengerListView passengerList;
    private BookingForm bookingForm;

    private final TextArea historyArea = new TextArea();
    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));
    private final AtomicInteger passengerIdSeq = new AtomicInteger(1000);

    public MainLayout(TrainService trainService, PassengerService passengerService, BookingService bookingService) {
        this.trainService = trainService;
        this.passengerService = passengerService;
        this.bookingService = bookingService;

        build();
        wire();
        refreshAll();
    }

    public BorderPane getRoot() {
        return root;
    }

    private void build() {
        // App bar
        HBox appBar = new HBox();
        appBar.getStyleClass().add("app-bar");
        Label title = new Label("Railway Reservation System");
        title.getStyleClass().add("app-title");
        appBar.getChildren().add(title);
        root.setTop(appBar);

        // Content
        HBox content = new HBox(16);
        content.setPadding(new Insets(16));
        root.setCenter(content);

        // Left: trains
        VBox leftCol = new VBox(16);
        leftCol.setFillWidth(true);

        Label trainsLabel = new Label("Available Trains");
        trainsLabel.getStyleClass().add("section-title");

        trainList = new TrainListView();

        VBox trainsCard = new VBox(12, trainsLabel, trainList.getView());
        trainsCard.getStyleClass().add("card");
        trainsCard.setPadding(new Insets(14));
        leftCol.getChildren().add(trainsCard);

        // Right: passengers + booking + history
        VBox rightCol = new VBox(16);
        rightCol.setPrefWidth(420);
        rightCol.setFillWidth(true);

        Label paxLabel = new Label("Passengers");
        paxLabel.getStyleClass().add("section-title");

        passengerList = new PassengerListView();

        VBox paxCard = new VBox(12, paxLabel, passengerList.getView());
        paxCard.getStyleClass().add("card");
        paxCard.setPadding(new Insets(14));

        Label bookingLabel = new Label("New Booking");
        bookingLabel.getStyleClass().add("section-title");

        bookingForm = new BookingForm();

        VBox bookingCard = new VBox(12, bookingLabel, bookingForm.getGrid());
        bookingCard.getStyleClass().add("card");
        bookingCard.setPadding(new Insets(14));

        Label historyLabel = new Label("Booking History");
        historyLabel.getStyleClass().add("section-title");

        historyArea.setEditable(false);
        historyArea.setPrefHeight(140);
        historyArea.getStyleClass().add("history-area");

        VBox historyCard = new VBox(12, historyLabel, historyArea);
        historyCard.getStyleClass().add("card");
        historyCard.setPadding(new Insets(14));

        rightCol.getChildren().addAll(paxCard, bookingCard, historyCard);

        content.getChildren().addAll(leftCol, rightCol);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
    }

    private void wire() {
        bookingForm.onSubmit((name, balance, seats) -> {
            Train selectedTrain = trainList.getView().getSelectionModel().getSelectedItem();
            if (selectedTrain == null) {
                Snackbar.show("Please select a train.");
                return;
            }
            if (name.isBlank()) {
                Snackbar.show("Name is required.");
                return;
            }
            if (balance < 0) {
                Snackbar.show("Balance cannot be negative.");
                return;
            }
            if (seats <= 0) {
                Snackbar.show("Tickets must be greater than zero.");
                return;
            }

            try {
                int newId = passengerIdSeq.getAndIncrement();
                Passenger p = new Passenger(newId, name, balance);
                passengerService.addPassenger(p);

                double costPerSeat = selectedTrain.getPricePerSeat();
                Ticket t = bookingService.bookTicket(p, selectedTrain, seats, costPerSeat);

                Snackbar.show("Booked " + seats + " seat(s) on " + selectedTrain.getTrainName()
                        + " for " + currencyFmt.format(seats * costPerSeat));

                refreshAll();

                FileHandler.savePassengers(passengerService.getAllPassengers(), "output/passengers.txt");
                FileHandler.saveTickets(bookingService.getBookingHistory(), "output/tickets.txt");

                bookingForm.clear();
            } catch (Exception ex) {
                Snackbar.show(ex.getMessage());
            }
        });
    }

    private void refreshAll() {
        trainList.setItems(trainService.getAllTrains());
        passengerList.setItems(passengerService.getAllPassengers());
        refreshHistory();
    }

    private void refreshHistory() {
        historyArea.clear();
        for (Ticket t : bookingService.getBookingHistory()) {
            historyArea.appendText(t.toString() + "\n");
        }
    }
}
