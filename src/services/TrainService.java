package services;

import models.Train;
import java.util.*;
import java.util.stream.Collectors;

public class TrainService {
    private Map<Integer, Train> trainMap;

    public TrainService() {
        trainMap = new HashMap<>();
        // Sample trains
        trainMap.put(1, new Train(1, "Express A", 100));
        trainMap.put(2, new Train(2, "Express B", 50));
        trainMap.put(3, new Train(3, "Express C", 75));
    }

    public void addTrain(Train train) {
        trainMap.put(train.getTrainId(), train);
    }

    public Train getTrainById(int id) {
        return trainMap.get(id);
    }

    public List<Train> getAllTrains() {
        return new ArrayList<>(trainMap.values());
    }

    public List<Train> getAvailableTrains() {
        return trainMap.values().stream()
                .filter(t -> t.getAvailableSeats() > 0)
                .collect(Collectors.toList());
    }

    public void displayTrains() {
        trainMap.values().forEach(System.out::println);
    }
}
