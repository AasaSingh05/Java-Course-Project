# File Structure

### repository format:
```RailwayReservationSystem/
│
├─ src/
│   ├─ Main.java                     // Main driver class with JavaFX launch
│   │
│   ├─ models/
│   │   ├─ Train.java                    // Train object
│   │   ├─ Passenger.java                // Passenger object
│   │   ├─ Ticket.java                   // Ticket object
│   │
│   ├─ exceptions/
│   │   ├─ InvalidBookingException.java  // User-defined exception
│   │
│   ├─ services/
│   │   ├─ BookingService.java           // Handles booking, concurrency, synchronization
│   │   ├─ TrainService.java             // CRUD for train details
│   │   ├─ PassengerService.java         // CRUD for passenger details
│   │
│   ├─ persistence/
│   │   ├─ FileHandler.java              // File I/O and serialization
│   │   ├─ DatabaseHandler.java          // JDBC connections and queries
│   │
│   ├─ gui/
│       ├─ BookingUI.java                // JavaFX GUI screens
│
├─ resources/
│   ├─ trains.db                          // SQLite database with train & passenger tables
│
├─ output/
│   ├─ tickets.ser                        // Serialized tickets storage
│   ├─ passengers.txt                     // Passenger info storage
│
├─ libs/
│   ├─sqlite-jdbc-3.45.3.0.jar            // jdbc Library
│
└─ README.md
```