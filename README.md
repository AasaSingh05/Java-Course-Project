# Railway Reservation System - Complete Documentation

---

## AIM OF THE PROJECT

Design and implement a robust Railway Reservation System in Java that provides a modern, user-friendly booking experience while demonstrating core software engineering concepts. The system should:

- Enable passengers to browse trains (with images, price in INR, and remaining seats), compute total fare in real time, confirm, and book tickets via a clean JavaFX interface.
- Guarantee correctness under concurrent booking attempts using synchronization, and illustrate a deadlock scenario along with a safe resolution strategy.
- Persist data reliably by saving passengers and bookings to files and by serializing ticket objects; reload this data across runs.
- Integrate basic database connectivity (SQLite via JDBC) to read/write core records (e.g., trains, passengers), showcasing end‑to‑end persistence beyond files.
- Apply disciplined exception handling to validate inputs and operational errors, ensuring graceful feedback to users.
- Use clean modular code (separated UI components, services, persistence) and standard Java collections and generics to maintain readable, maintainable, and testable code.

**In summary:** Build a full-stack desktop booking app that is correct under concurrency, resilient via exceptions, persistent via files/serialization/DB, and polished with a modern JavaFX UI.

---

## PROJECT NOTES

### Overview
- A desktop Railway Reservation System built in Java with a modern JavaFX UI, robust booking logic, file/serialization persistence, and optional SQLite JDBC integration.
- Clean separation of concerns: UI components, services, and persistence layers minimize coupling and make the code easier to maintain.

### Architecture

#### UI: JavaFX with split components
- **BookingApp**: Application entry that boots services and loads CSS.
- **MainLayout**: Composes the screen into left (trains + booking history) and right (passengers + new booking) areas; includes a demo menu.
- **TrainListView/TrainRowCell**: Shows train logo, name, remaining seats, and bold INR price.
- **PassengerListView**: Shows passenger cards without balances to match the wallet‑less flow.
- **BookingForm**: Collects name and seats, computes Total Price in real time, and triggers a confirm dialog before booking.

#### Services
- **TrainService**: In-memory train repository; attempts DB load, otherwise seeds defaults.
- **PassengerService**: Manages in-memory passengers.
- **BookingService**: Synchronized booking logic; records tickets and keeps history; wallet checks removed to match current UI.
- **SafeBookingService**: Example lock-ordering strategy to prevent deadlocks.

#### Persistence
- **FileHandler**: Text I/O for passengers and tickets.
- **TicketSerializer**: Object serialization of tickets (tickets.ser) to demonstrate Java serialization.
- **DatabaseHandler**: Optional SQLite connectivity to read trains and insert passengers.

### Key Features

#### Real-time pricing
- Total updates on both train selection and seat edits.
- Confirmation dialog displays train, unit price, seats, and total in INR.

#### Robust booking
- Synchronized seat deduction prevents race conditions.
- Invalid inputs guarded (zero/negative seats, missing name).

#### Demos for evaluation
- **Concurrency**: MultiBookRunner spawns multiple booking threads on one train, with before/after seat logs.
- **Deadlock**: DeadlockDemo shows a deliberate deadlock and references SafeBookingService for resolution.

#### Persistence
- File saves occur on actions and shutdown; serialized tickets are written to output/tickets.ser and can be reloaded for demonstration.
- Optional SQLite: load trains and insert passengers if SLF4J + SQLite JARs are present; app degrades gracefully if not.

### How Requirements Are Met

#### Concurrency and synchronization
- Train.bookSeats and BookingService methods are synchronized to prevent inconsistent seat counts during concurrent booking attempts.
- MultiBookRunner demonstrates correctness under contention.

#### Deadlock demonstration and resolution
- DeadlockDemo creates a classic opposite-order lock acquisition to show deadlock.
- SafeBookingService demonstrates consistent lock ordering to avoid deadlocks.

#### Exceptions
- Custom InvalidBookingException used for invalid bookings (e.g., zero seats, insufficient seats).
- UI catches exceptions and shows user-friendly messages via Snackbar-styled alerts.

#### Files, streams, serialization
- FileHandler reads/writes passengers and tickets as CSV-like text for transparency.
- TicketSerializer writes/reads tickets with ObjectOutputStream/ObjectInputStream for serialization demonstration.

#### GUI and events
- JavaFX components with CSS styling provide a clean, card-based interface.
- Event handlers wire user actions to service calls and data refresh.

#### JDBC (optional)
- DatabaseHandler initializes SQLite tables and supports insertPassenger and loadTrains.
- If the SQLite driver is missing, DatabaseHandler disables DB features and the app continues using in-memory data.

### Setup Notes
- JavaFX path: configure JAVA_FX_PATH in runApp.sh to your SDK lib folder.
- Classpath JARs: include sqlite-jdbc and SLF4J (slf4j-api + slf4j-simple) to enable JDBC; otherwise the app runs without DB features.
- Resources:
  - CSS at resources/css/app.css.
  - Logos at resources/logos/ (train.png is used as fallback).
  - Outputs saved under output/ (passengers.txt, tickets.txt, tickets.ser).

### Usage Flow
- Select a train on the left; price per seat shows in rows and feeds the total in the form.
- Enter passenger name and seat count; confirm dialog shows live total.
- After booking: history updates left card; passengers list reflects the new passenger; files are saved; passenger is optionally inserted into the DB.
- Demo menu provides one-click concurrency and deadlock demonstrations with console logs.

### Design Choices
- Wallet-less UI: balance removed to simplify flow; prevents mismatch between UI and service checks.
- Height-fit lists: ListView fixedCellSize + prefHeight binding ensures no large blank areas when few items exist.
- Separation by components: splitting BookingUI into smaller classes reduces file size, improves readability, and makes targeted changes easier.

### Possible Enhancements
- Replace Alert-based snackbar with a transient toast overlay.
- Add search/filter for trains and passengers.
- Expand DB integration to persist bookings and passengers fully with DAOs.
- Add unit tests for booking logic and seat calculations.

### Deliverables to Include
- Source code with the split UI and services.
- Screenshots of:
  - Main UI before/after a booking.
  - Concurrency demo console output (seat counts consistent).
  - Deadlock demo console output and explanation of SafeBookingService.
  - tickets.txt, passengers.txt, tickets.ser presence in output/.
  - Optional: DB console logs for insert/load.
- Short report including Aim and these Notes.

---

## PROJECT FILE STRUCTURE

```
Java-Course-Project/
├── src/
│   ├── Main.java                          # Application entry point
│   │
│   ├── demo/
│   │   ├── MultiBookRunner.java           # Concurrency demonstration
│   │   └── DeadlockDemo.java              # Deadlock simulation
│   │
│   ├── exceptions/
│   │   └── InvalidBookingException.java   # Custom exception for invalid bookings
│   │
│   ├── gui/
│   │   ├── BookingApp.java                # JavaFX Application class
│   │   │
│   │   ├── components/
│   │   │   ├── BookingForm.java           # Name + Seats input with real-time total
│   │   │   ├── PassengerListView.java     # Passenger card list
│   │   │   ├── TrainListView.java         # Train card list with height binding
│   │   │   ├── TrainRowCell.java          # Custom cell: logo + title + price
│   │   │   └── Snackbar.java              # Alert helper
│   │   │
│   │   └── layout/
│   │       └── MainLayout.java            # Main UI layout with demo menu
│   │
│   ├── models/
│   │   ├── Passenger.java                 # Passenger model (Serializable)
│   │   ├── Ticket.java                    # Ticket model (Serializable)
│   │   └── Train.java                     # Train model with synchronized booking
│   │
│   ├── persistence/
│   │   ├── DatabaseHandler.java           # SQLite JDBC operations
│   │   ├── FileHandler.java               # Text file I/O for passengers/tickets
│   │   └── TicketSerializer.java          # Object serialization demo
│   │
│   └── services/
│       ├── BookingService.java            # Booking logic with sync + deadlock demo
│       ├── PassengerService.java          # Passenger management
│       ├── SafeBookingService.java        # Deadlock-free booking example
│       └── TrainService.java              # Train repository with DB integration
│
├── resources/
│   ├── css/
│   │   └── app.css                        # Light-theme styling
│   │
│   ├── logos/                             # Train logo images
│   │   ├── train.png                      # Fallback logo
│   │   ├── 1.png                          # (optional) Train 1 logo
│   │   ├── 2.png                          # (optional) Train 2 logo
│   │   └── 3.png                          # (optional) Train 3 logo
│   │
│   └── trains.db                          # SQLite database (auto-created)
│
├── libs/
│   ├── sqlite-jdbc-3.45.3.0.jar          # SQLite JDBC driver
│   ├── slf4j-api-2.0.9.jar               # SLF4J API
│   └── slf4j-simple-2.0.9.jar            # SLF4J simple implementation
│
├── output/                                # Generated at runtime
│   ├── passengers.txt                     # Text persistence
│   ├── tickets.txt                        # Text persistence
│   └── tickets.ser                        # Serialized tickets
│
├── out/                                   # Compiled classes (auto-generated)
│
├── runApp.sh                              # Build and run script
└── README.md                              # Project documentation
```

---

## README CONTENT

# Railway Reservation System

A comprehensive desktop booking application built with Java and JavaFX, demonstrating robust concurrency handling, persistence mechanisms, and modern UI design.

## Features

### Core Functionality
- **Train Browsing**: View available trains with logos, pricing in INR, and remaining seat counts
- **Real-time Pricing**: Total cost updates instantly as users select trains and enter seat quantities
- **Booking Confirmation**: Interactive dialog shows final price before completing reservations
- **Booking History**: Persistent record of all completed transactions

### Technical Highlights
- **Concurrency Safety**: Synchronized seat booking prevents race conditions during simultaneous reservations
- **Deadlock Demonstration**: Includes intentional deadlock scenario and resolution strategy
- **Multi-layer Persistence**:
  - Text file I/O for human-readable data
  - Object serialization for ticket records
  - SQLite JDBC integration for structured storage
- **Exception Handling**: Custom exceptions with user-friendly error messages
- **Modular Architecture**: Clean separation of UI, business logic, and data layers

## Project Structure

```
src/
├── gui/              # JavaFX components and layouts
├── models/           # Data models (Train, Passenger, Ticket)
├── services/         # Business logic and booking operations
├── persistence/      # File, serialization, and database handlers
├── demo/             # Concurrency and deadlock demonstrations
└── exceptions/       # Custom exception classes

resources/
├── css/              # Stylesheet for modern UI
├── logos/            # Train logos
└── trains.db         # SQLite database

libs/                 # External dependencies (JDBC, SLF4J)
output/               # Runtime-generated persistence files
```

## Prerequisites

- **Java 21+** with JavaFX SDK
- **SQLite JDBC Driver** (3.45.3.0 or later)
- **SLF4J** (2.0.9 or later) - API and Simple implementation
- **Bash** (for runApp.sh script)

## Setup

1. **Configure JavaFX path** in `runApp.sh`:
   ```bash
   JAVA_FX_PATH="/path/to/javafx-sdk/lib"
   ```

2. **Download dependencies** to `libs/`:
   ```bash
   wget https://repo1.maven.org/maven2/org/xerial/sqlite-jdbc/3.45.3.0/sqlite-jdbc-3.45.3.0.jar
   wget https://repo1.maven.org/maven2/org/slf4j/slf4j-api/2.0.9/slf4j-api-2.0.9.jar
   wget https://repo1.maven.org/maven2/org/slf4j/slf4j-simple/2.0.9/slf4j-simple-2.0.9.jar
   ```

3. **Add train logos** (optional) to `resources/logos/` as `train.png`, `1.png`, `2.png`, etc.

## Running the Application

```bash
chmod +x runApp.sh
./runApp.sh
```

The script will:
- Clean previous builds
- Compile all sources
- Launch the JavaFX application

## Usage

### Normal Booking Flow
1. Select a train from the left panel
2. Enter passenger name and number of seats
3. Review the real-time total price calculation
4. Click "Book Ticket" and confirm in the dialog
5. View updated booking history and passenger list

### Demonstration Features
Access via the **Demos** menu:
- **Concurrency Demo**: Spawns 5 threads booking the same train simultaneously
- **Deadlock Demo**: Shows intentional deadlock and logs resolution approach

Check console output for detailed logs during demonstrations.

## Persistence

Data is saved in three formats:

1. **Text Files** (`output/passengers.txt`, `output/tickets.txt`)
   - CSV-like format for transparency
   - Loaded on startup and saved on shutdown

2. **Serialization** (`output/tickets.ser`)
   - Binary format demonstrating Java object streams
   - Created alongside text files

3. **SQLite Database** (`resources/trains.db`)
   - Structured storage for trains and passengers
   - Optional - app runs without if driver missing

## Key Components

### GUI (`gui/`)
- **MainLayout**: Composes the interface with trains, passengers, booking form, and history
- **TrainListView**: Custom cells with logos, titles, and INR pricing
- **BookingForm**: Real-time total calculation with confirmation dialog

### Services (`services/`)
- **BookingService**: Thread-safe booking with synchronized methods
- **TrainService**: Manages train inventory with optional DB loading
- **SafeBookingService**: Demonstrates deadlock prevention via lock ordering

### Persistence (`persistence/`)
- **FileHandler**: Text I/O operations
- **TicketSerializer**: Object serialization/deserialization
- **DatabaseHandler**: JDBC operations with graceful degradation

### Demos (`demo/`)
- **MultiBookRunner**: Concurrent booking simulation
- **DeadlockDemo**: Intentional deadlock scenario

## Technical Stack

- **Language**: Java 21
- **UI Framework**: JavaFX 22
- **Database**: SQLite 3.45.3
- **Build Tool**: Custom bash script
- **Persistence**: File I/O, Serialization, JDBC

## Design Patterns

- **MVC Separation**: Clear boundaries between UI, business logic, and data
- **Observer Pattern**: JavaFX properties for reactive UI updates
- **Repository Pattern**: Services abstract data access
- **Singleton Pattern**: Shared service instances

## Known Limitations

- Balance/wallet feature removed for simplified flow
- Database operations are optional (app continues without JDBC)
- No authentication or multi-user support
- Console-based demo output (not integrated in GUI)

## Future Enhancements

- Add search and filter capabilities
- Implement toast notifications instead of dialog alerts
- Expand JDBC integration for full persistence
- Add unit tests for booking logic
- Support multiple payment methods

## License

Educational project for CSE1007 - Java Programming course.

## Authors

Developed as part of Fall Semester 2025-2026 case study assignment.

---

## END OF DOCUMENTATION
