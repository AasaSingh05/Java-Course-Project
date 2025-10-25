package gui.layout;

import gui.components.BookingForm;
import gui.components.PassengerListView;
import gui.components.TrainListView;
import gui.components.Snackbar;
import demo.DeadlockDemo;
import demo.MultiBookRunner;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import models.Passenger;
import models.Ticket;
import models.Train;
import persistence.DatabaseHandler;
import persistence.FileHandler;
import services.BookingService;
import services.PassengerService;
import services.TrainService;

import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Layout: Left (Trains + History), Right (Passengers + New Booking).
 * Includes demo menu, JDBC operations, real-time price updates, and confirmation dialog.
 */
public class MainLayout {

    private final BorderPane root = new BorderPane();

    private final TrainService trainService;
    private final PassengerService passengerService;
    private final BookingService bookingService;

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
        // Demo menu bar
        MenuBar menuBar = createDemoMenu();
        
        // App bar
        HBox appBar = new HBox();
        appBar.getStyleClass().add("app-bar");
        Label title = new Label("Railway Reservation System");
        title.getStyleClass().add("app-title");
        appBar.getChildren().add(title);
        
        // Combine menu and app bar
        VBox topContainer = new VBox(menuBar, appBar);
        root.setTop(topContainer);

        // Content
        HBox content = new HBox(16);
        content.setPadding(new Insets(16));
        root.setCenter(content);

        // Left: Trains + History
        VBox leftCol = new VBox(16);
        leftCol.setFillWidth(true);

        Label trainsLabel = new Label("Available Trains");
        trainsLabel.getStyleClass().add("section-title");

        trainList = new TrainListView();

        VBox trainsCard = new VBox(12, trainsLabel, trainList.getView());
        trainsCard.getStyleClass().add("card");
        trainsCard.setPadding(new Insets(14));

        Label historyLabel = new Label("Booking History");
        historyLabel.getStyleClass().add("section-title");

        historyArea.setEditable(false);
        historyArea.setPrefHeight(180);
        historyArea.getStyleClass().add("history-area");

        VBox historyCard = new VBox(12, historyLabel, historyArea);
        historyCard.getStyleClass().add("card");
        historyCard.setPadding(new Insets(14));

        leftCol.getChildren().addAll(trainsCard, historyCard);

        // Right: Passengers + New Booking
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

        // Keep total price in sync with selected train's unit price
        trainList.getView().getSelectionModel().selectedItemProperty().addListener((obs, oldT, newT) -> {
            double price = (newT == null) ? 0.0 : newT.getPricePerSeat();
            bookingForm.setUnitPrice(price);
            bookingForm.refreshTotal();
        });

        // Also recompute total when seats text changes (real-time update)
        bookingForm.seatsTextProperty().addListener((obs, o, n) -> {
            Train sel = trainList.getView().getSelectionModel().getSelectedItem();
            double price = (sel == null) ? 0.0 : sel.getPricePerSeat();
            bookingForm.setUnitPrice(price);
            bookingForm.refreshTotal();
        });

        VBox bookingCard = new VBox(12, bookingLabel, bookingForm.getGrid());
        bookingCard.getStyleClass().add("card");
        bookingCard.setPadding(new Insets(14));

        rightCol.getChildren().addAll(paxCard, bookingCard);

        content.getChildren().addAll(leftCol, rightCol);
        HBox.setHgrow(leftCol, Priority.ALWAYS);
    }

    private MenuBar createDemoMenu() {
        MenuBar menuBar = new MenuBar();
        Menu demoMenu = new Menu("Demos");
        
        MenuItem concurrency = new MenuItem("Run Concurrency Demo");
        concurrency.setOnAction(e -> {
            Train train = trainService.getTrainById(1);
            if (train != null) {
                System.out.println("\n=== CONCURRENCY DEMO ===");
                MultiBookRunner.run(bookingService, train, 5, 3, train.getPricePerSeat());
                refreshAll(); // Refresh UI after demo
                Snackbar.show("Concurrency demo completed. Check console output.");
            }
        });
        
        MenuItem deadlock = new MenuItem("Run Deadlock Demo");
        deadlock.setOnAction(e -> {
            Passenger p1 = new Passenger(9001, "Agent A", 1000);
            Passenger p2 = new Passenger(9002, "Agent B", 1000);
            Train t1 = trainService.getTrainById(1);
            Train t2 = trainService.getTrainById(2);
            if (t1 != null && t2 != null) {
                System.out.println("\n=== DEADLOCK DEMO ===");
                DeadlockDemo.run(bookingService, p1, t1, p2, t2);
                Snackbar.show("Deadlock demo completed. Check console output.");
            }
        });
        
        demoMenu.getItems().addAll(concurrency, deadlock);
        menuBar.getMenus().add(demoMenu);
        
        return menuBar;
    }

    private void wire() {
        bookingForm.onSubmit((name, seats) -> {
            Train selectedTrain = trainList.getView().getSelectionModel().getSelectedItem();
            if (selectedTrain == null) {
                Snackbar.show("Please select a train.");
                return;
            }
            if (name.isBlank()) {
                Snackbar.show("Name is required.");
                return;
            }
            if (seats <= 0) {
                Snackbar.show("Tickets must be greater than zero.");
                return;
            }

            double unit = selectedTrain.getPricePerSeat();
            double total = unit * seats;

            // Show confirmation dialog with live price
            if (!confirmBooking(selectedTrain.getTrainName(), unit, seats, total)) {
                return; // User cancelled
            }

            try {
                int newId = passengerIdSeq.getAndIncrement();
                Passenger p = new Passenger(newId, name, 0.0);
                passengerService.addPassenger(p);

                // JDBC demonstration - insert passenger to database
                DatabaseHandler.insertPassenger(p.getPassengerId(), p.getName());

                Ticket t = bookingService.bookTicket(p, selectedTrain, seats, unit);

                Snackbar.show("Booked " + seats + " seat(s) on " + selectedTrain.getTrainName());

                refreshAll();

                // Persist to files
                FileHandler.savePassengers(passengerService.getAllPassengers(), "output/passengers.txt");
                FileHandler.saveTickets(bookingService.getBookingHistory(), "output/tickets.txt");

                bookingForm.clear();

                // Recompute total for current selection after clear
                Train reselected = trainList.getView().getSelectionModel().getSelectedItem();
                double price = (reselected == null) ? 0.0 : reselected.getPricePerSeat();
                bookingForm.setUnitPrice(price);
                bookingForm.refreshTotal();
            } catch (Exception ex) {
                Snackbar.show(ex.getMessage());
            }
        });
    }

    // Confirmation dialog showing unit price, seats, and total in INR
    private boolean confirmBooking(String trainName, double unitPrice, int seats, double total) {
        String message = "Train: " + trainName
                + "\nPrice per seat: " + currencyFmt.format(unitPrice)
                + "\nSeats: " + seats
                + "\nTotal: " + currencyFmt.format(total)
                + "\n\nProceed with booking?";

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirm Booking");
        alert.setHeaderText("Please confirm your booking");
        alert.setContentText(message);
        return alert.showAndWait().filter(btn -> btn == ButtonType.OK).isPresent();
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
