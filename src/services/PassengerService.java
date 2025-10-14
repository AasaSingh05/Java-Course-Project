package services;

import models.Passenger;
import java.util.*;

public class PassengerService {
    private Map<Integer, Passenger> passengerMap;

    public PassengerService() {
        passengerMap = new HashMap<>();
        // Sample passengers
        passengerMap.put(1, new Passenger(1, "Alice", 500.0));
        passengerMap.put(2, new Passenger(2, "Bob", 300.0));
        passengerMap.put(3, new Passenger(3, "Charlie", 1000.0));
    }

    public void addPassenger(Passenger passenger) {
        passengerMap.put(passenger.getPassengerId(), passenger);
    }

    public Passenger getPassengerById(int id) {
        return passengerMap.get(id);
    }

    public List<Passenger> getAllPassengers() {
        return new ArrayList<>(passengerMap.values());
    }

    public void displayPassengers() {
        passengerMap.values().forEach(System.out::println);
    }
}
