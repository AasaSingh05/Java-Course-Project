# Railway Reservation System - Complete Documentation

---
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
