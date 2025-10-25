package gui.components;

import demo.DeadlockDemo;
import demo.MultiBookRunner;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import models.Passenger;
import models.Train;
import services.BookingService;
import services.TrainService;

/**
 * Optional menu bar with demo buttons for concurrency and deadlock.
 */
public class DemoMenu {
    
    public static MenuBar create(TrainService trainService, BookingService bookingService) {
        MenuBar menuBar = new MenuBar();
        Menu demoMenu = new Menu("Demos");
        
        MenuItem concurrency = new MenuItem("Run Concurrency Demo");
        concurrency.setOnAction(e -> {
            Train train = trainService.getTrainById(1);
            if (train != null) {
                System.out.println("\n=== CONCURRENCY DEMO ===");
                MultiBookRunner.run(bookingService, train, 5, 3, train.getPricePerSeat());
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
            }
        });
        
        demoMenu.getItems().addAll(concurrency, deadlock);
        menuBar.getMenus().add(demoMenu);
        
        return menuBar;
    }
}
