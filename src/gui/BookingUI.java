package gui;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;

import models.Passenger;
import models.Ticket;
import models.Train;
import services.BookingService;
import services.PassengerService;
import services.TrainService;
import persistence.FileHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Modern dark-mode JavaFX UI:
 * - Top app bar
 * - Left: train catalog with card rows (logo, title, remaining seats, bold INR price)
 * - Right: passengers in card rows, booking form, and booking history
 * - External stylesheet (resources/css/app.css) for theme, spacing, and typography
 */
public class BookingUI extends Application {

    // Services for demo data and booking operations.
    private final TrainService trainService = new TrainService();
    private final PassengerService passengerService = new PassengerService();
    private final BookingService bookingService = new BookingService();

    // Simple local ID generator for ad-hoc passengers created from the form.
    private final AtomicInteger passengerIdSeq = new AtomicInteger(1000);

    // UI nodes shared across methods.
    private ListView<Train> trainListView;       // typed as Train for custom cell rendering
    private ListView<String> passengerListView;  // renders card from "id|name|balance" text
    private TextArea bookingHistoryArea;

    // Force currency to Indian Rupees with Indian numbering system.
    private final NumberFormat currencyFmt = NumberFormat.getCurrencyInstance(new Locale("en", "IN"));

    @Override
    public void start(Stage primaryStage) {
        // Root layout with a top app bar and center content area.
        BorderPane root = new BorderPane();

        // Top app bar with title, styled via CSS.
        HBox appBar = new HBox();
        appBar.getStyleClass().add("app-bar");
        Label title = new Label("Railway Reservation System");
        title.getStyleClass().add("app-title");
        appBar.getChildren().add(title);
        root.setTop(appBar);

        // Main content area: left (trains) and right (passengers + booking + history).
        HBox content = new HBox(16);
        content.setPadding(new Insets(16));
        root.setCenter(content);

        // LEFT COLUMN — Trains card with a custom ListCell.
        VBox leftCol = new VBox(12);
        leftCol.setFillWidth(true);

        Label trainsLabel = new Label("Available Trains");
        trainsLabel.getStyleClass().add("section-title");

        trainListView = new ListView<>();
        trainListView.getStyleClass().add("carded-list");
        trainListView.setPrefWidth(560);
        trainListView.setCellFactory(list -> new TrainCardCell(currencyFmt));
        updateTrainList();

        VBox trainsCard = new VBox(12, trainsLabel, trainListView);
        trainsCard.getStyleClass().add("card");
        trainsCard.setPadding(new Insets(14));

        leftCol.getChildren().addAll(trainsCard);

        // RIGHT COLUMN — Passengers, booking form, and history stacked as cards.
        VBox rightCol = new VBox(16);
        rightCol.setFillWidth(true);

        // Passengers card with custom passenger card cells.
        Label paxLabel = new Label("Passengers");
        paxLabel.getStyleClass().add("section-title");

        passengerListView = new ListView<>();
        passengerListView.getStyleClass().add("carded-list");
        passengerListView.setPrefWidth(360);
        passengerListView.setCellFactory(list -> new PassengerCardCell());
        updatePassengerList();

        VBox paxCard = new VBox(12, paxLabel, passengerListView);
        paxCard.getStyleClass().add("card");
        paxCard.setPadding(new Insets(14));

        // Booking form card.
        Label bookingLabel = new Label("New Booking");
        bookingLabel.getStyleClass().add("section-title");

        GridPane bookingGrid = new GridPane();
        bookingGrid.getStyleClass().add("form-grid");
        bookingGrid.setHgap(12);
        bookingGrid.setVgap(10);

        TextField nameField = new TextField();
        nameField.setPromptText("Passenger name");

        TextField balanceField = new TextField();
        balanceField.setPromptText("Balance (e.g., 500.00)");

        TextField seatsField = new TextField();
        seatsField.setPromptText("Seats to Book");

        Button bookButton = new Button("Book Ticket");
        bookButton.getStyleClass().add("accent-button");

        bookingGrid.addRow(0, new Label("Name"), nameField);
        bookingGrid.addRow(1, new Label("Balance"), balanceField);
        bookingGrid.addRow(2, new Label("Tickets"), seatsField);

        HBox bookingActions = new HBox(bookButton);
        bookingActions.setAlignment(Pos.CENTER_RIGHT);

        VBox bookingCard = new VBox(12, bookingLabel, bookingGrid, bookingActions);
        bookingCard.getStyleClass().add("card");
        bookingCard.setPadding(new Insets(14));

        // Booking history card.
        Label historyLabel = new Label("Booking History");
        historyLabel.getStyleClass().add("section-title");

        bookingHistoryArea = new TextArea();
        bookingHistoryArea.setEditable(false);
        bookingHistoryArea.setPrefHeight(160);
        bookingHistoryArea.getStyleClass().add("history-area");

        VBox historyCard = new VBox(12, historyLabel, bookingHistoryArea);
        historyCard.getStyleClass().add("card");
        historyCard.setPadding(new Insets(14));

        rightCol.getChildren().addAll(paxCard, bookingCard, historyCard);

        // Layout columns in the main content.
        content.getChildren().addAll(leftCol, rightCol);
        HBox.setHgrow(leftCol, Priority.ALWAYS);

        // Booking action: validate, create passenger, book with INR price, refresh, and persist to files.
        bookButton.setOnAction(e -> {
            Train selectedTrain = trainListView.getSelectionModel().getSelectedItem();
            if (selectedTrain == null) {
                showSnack("Please select a train.");
                return;
            }

            String name = nameField.getText() == null ? "" : nameField.getText().trim();
            String balanceText = balanceField.getText() == null ? "" : balanceField.getText().trim();
            String seatsText = seatsField.getText() == null ? "" : seatsField.getText().trim();

            if (name.isEmpty()) {
                showSnack("Name is required.");
                return;
            }

            double balance;
            int seats;
            try {
                balance = Double.parseDouble(balanceText);
            } catch (NumberFormatException nfe) {
                showSnack("Balance must be a valid number.");
                return;
            }
            try {
                seats = Integer.parseInt(seatsText);
            } catch (NumberFormatException nfe) {
                showSnack("Tickets must be a whole number.");
                return;
            }
            if (balance < 0) {
                showSnack("Balance cannot be negative.");
                return;
            }
            if (seats <= 0) {
                showSnack("Tickets must be greater than zero.");
                return;
            }

            try {
                int newId = passengerIdSeq.getAndIncrement();
                Passenger newPassenger = new Passenger(newId, name, balance);
                passengerService.addPassenger(newPassenger);

                double costPerSeat = selectedTrain.getPricePerSeat();
                Ticket ticket = bookingService.bookTicket(newPassenger, selectedTrain, seats, costPerSeat);

                double total = seats * costPerSeat;
                showSnack("Booked " + seats + " seat(s) on " + selectedTrain.getTrainName()
                        + " for " + currencyFmt.format(total));

                updateTrainList();
                updatePassengerList();
                updateBookingHistory();

                FileHandler.savePassengers(passengerService.getAllPassengers(), "output/passengers.txt");
                FileHandler.saveTickets(bookingService.getBookingHistory(), "output/tickets.txt");

                nameField.clear();
                balanceField.clear();
                seatsField.clear();
            } catch (Exception ex) {
                showSnack(ex.getMessage());
            }
        });

        // Scene + stylesheet load. Using a file path so it works from your repo.
        Scene scene = new Scene(root, 1080, 640);
        scene.getStylesheets().add("file:resources/css/app.css");
        primaryStage.setTitle("Railway Reservation System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * Rebuilds the train list with the latest objects (cells render fields).
     */
    private void updateTrainList() {
        trainListView.getItems().setAll(trainService.getAllTrains());
    }

    /**
     * Passengers are rendered as card rows from a compact "id|name|balance" string.
     * This avoids changing the service API; if preferred, swap to ListView<Passenger> and
     * update the cell to read fields directly.
     */
    private void updatePassengerList() {
        passengerListView.getItems().clear();
        for (Passenger p : passengerService.getAllPassengers()) {
            passengerListView.getItems().add(
                    p.getPassengerId() + "|" + p.getName() + "|" + p.getBalance()
            );
        }
    }

    /**
     * Prints each booked ticket summary on its own line.
     */
    private void updateBookingHistory() {
        bookingHistoryArea.clear();
        List<Ticket> tickets = bookingService.getBookingHistory();
        for (Ticket t : tickets) {
            bookingHistoryArea.appendText(t.toString() + "\n");
        }
    }

    /**
     * Simple unified information alert (styled like a snackbar via CSS).
     */
    private void showSnack(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(null);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.getDialogPane().getStyleClass().add("snackbar");
        alert.showAndWait();
    }

    /**
     * Custom card-like ListCell for Train rows:
     * [Logo]  [Title + grey subtitle]                            [Bold INR price]
     */
    private static class TrainCardCell extends ListCell<Train> {
        private final HBox root = new HBox(14);
        private final ImageView logoView = new ImageView();
        private final VBox centerBox = new VBox(2);
        private final Label title = new Label();
        private final Label subtitle = new Label();
        private final Label price = new Label();
        private final Region spacer = new Region();

        private final NumberFormat currencyFmt;

        public TrainCardCell(NumberFormat currencyFmt) {
            this.currencyFmt = currencyFmt;

            logoView.setFitWidth(42);
            logoView.setFitHeight(42);
            logoView.setPreserveRatio(true);
            logoView.setSmooth(true);

            title.getStyleClass().add("train-title");
            subtitle.getStyleClass().add("train-subtitle");
            centerBox.getChildren().addAll(title, subtitle);

            price.getStyleClass().add("train-price");

            HBox.setHgrow(spacer, Priority.ALWAYS);

            root.getChildren().addAll(logoView, centerBox, spacer, price);
            root.setAlignment(Pos.CENTER_LEFT);
            root.getStyleClass().add("train-row");
            root.setPadding(new Insets(10));
        }

        @Override
        protected void updateItem(Train train, boolean empty) {
            super.updateItem(train, empty);
            if (empty || train == null) {
                setGraphic(null);
                setText(null);
                return;
            }

            logoView.setImage(loadLogo(train.getTrainId()));
            title.setText(train.getTrainName());
            subtitle.setText("Remaining: " + train.getAvailableSeats());
            price.setText(currencyFmt.format(train.getPricePerSeat()));

            setGraphic(root);
            setText(null);
        }

        private Image loadLogo(int trainId) {
            String specific = "resources/logos/" + trainId + ".png";
            String fallback = "resources/logos/train.png";
            try {
                return new Image(new FileInputStream(specific));
            } catch (FileNotFoundException e1) {
                try {
                    return new Image(new FileInputStream(fallback));
                } catch (FileNotFoundException e2) {
                    return null;
                }
            }
        }
    }

    /**
     * Passenger card cell that mirrors the train card styling with a simple avatar:
     * [Avatar]  [Name (bold) + grey "Balance: x"].
     * Expects each item as "id|name|balance".
     */
    private static class PassengerCardCell extends ListCell<String> {
        private final HBox root = new HBox(14);
        private final StackPane avatar = new StackPane();
        private final VBox centerBox = new VBox(2);
        private final Label title = new Label();
        private final Label subtitle = new Label();

        public PassengerCardCell() {
            // Circular avatar with subtle accent-tinted background.
            avatar.setMinSize(36, 36);
            avatar.setMaxSize(36, 36);
            // Inline style for the avatar blob; rest driven by CSS classes.
            avatar.setStyle("-fx-background-color: #223049; -fx-background-radius: 18;");

            title.getStyleClass().add("passenger-title");
            subtitle.getStyleClass().add("passenger-subtitle");

            centerBox.getChildren().addAll(title, subtitle);

            root.getChildren().addAll(avatar, centerBox);
            root.setAlignment(Pos.CENTER_LEFT);
            root.getStyleClass().add("passenger-row");
            root.setPadding(new Insets(10));
        }

        @Override
        protected void updateItem(String value, boolean empty) {
            super.updateItem(value, empty);
            if (empty || value == null) {
                setGraphic(null);
                setText(null);
                return;
            }
            // Parse "id|name|balance"
            String[] parts = value.split("\\|", 3);
            String id = parts.length > 0 ? parts[0] : "";
            String name = parts.length > 1 ? parts[1] : "";
            String balance = parts.length > 2 ? parts[2] : "";

            title.setText(name + " (ID " + id + ")");
            subtitle.setText("Balance: " + balance);

            setGraphic(root);
            setText(null);
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
