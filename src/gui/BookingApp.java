package gui;

import gui.layout.MainLayout;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import services.BookingService;
import services.PassengerService;
import services.TrainService;

/**
 * Application entry for the split UI composition.
 */
public class BookingApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        TrainService trainService = new TrainService();
        PassengerService passengerService = new PassengerService();
        BookingService bookingService = new BookingService();

        MainLayout root = new MainLayout(trainService, passengerService, bookingService);

        Scene scene = new Scene(root.getRoot(), 1080, 640);
        scene.getStylesheets().add("file:resources/css/app.css");

        primaryStage.setTitle("Railway Reservation System");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
