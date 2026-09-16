package service;

import model.WorkoutLog;

import java.util.List;

public class ProgressService {

    public String compareLatestTwo(List<WorkoutLog> logsSortedByDateAscending) {

        if (logsSortedByDateAscending == null || logsSortedByDateAscending.isEmpty()) {
            return "No workouts logged yet for this exercise.";
        }

        if (logsSortedByDateAscending.size() == 1) {
            double onlyWeight = logsSortedByDateAscending.get(0).getWeight();
            return "First recorded weight: " + onlyWeight + " kg";
        }

        int lastIndex = logsSortedByDateAscending.size() - 1;

        double previousWeight = logsSortedByDateAscending.get(lastIndex - 1).getWeight();
        double currentWeight = logsSortedByDateAscending.get(lastIndex).getWeight();

        double difference = currentWeight - previousWeight;

        if (difference > 0) {
            return "Previous: " + previousWeight + " kg, Today: " + currentWeight +
                    " kg. Improved by " + difference + " kg!";
        } else if (difference < 0) {
            return "Previous: " + previousWeight + " kg, Today: " + currentWeight +
                    " kg. Lower by " + Math.abs(difference) + " kg.";
        } else {
            return "Previous: " + previousWeight + " kg, Today: " + currentWeight +
                    " kg. Same as last time.";
        }
    }
}