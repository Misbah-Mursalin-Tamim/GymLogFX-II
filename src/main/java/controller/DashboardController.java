package controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import model.WorkoutLog;
import repository.ExerciseRepository;
import repository.WorkoutLogRepository;
import service.FitnessTipService;
import util.SceneManager;

import java.sql.SQLException;
import java.util.Collections;
import java.util.List;

public class DashboardController {

    @FXML
    private HBox statsRow;

    @FXML
    private AnchorPane totalWorkoutsCard;

    @FXML
    private AnchorPane totalExercisesCard;

    @FXML
    private AnchorPane latestWorkoutCard;

    @FXML
    private Label totalWorkoutsLabel;

    @FXML
    private Label totalExercisesLabel;

    @FXML
    private Label latestWorkoutLabel;

    @FXML
    private LineChart<String, Number> progressChart;

    @FXML
    private Label tipLabel;

    private final ExerciseRepository exerciseRepository = new ExerciseRepository();
    private final WorkoutLogRepository workoutLogRepository = new WorkoutLogRepository();
    private final FitnessTipService fitnessTipService = new FitnessTipService();

    @FXML
    public void initialize() {
        makeStatCardsResponsive();
        loadStats();
        loadFitnessTip();
    }

    private void makeStatCardsResponsive() {
        for (AnchorPane card : List.of(totalWorkoutsCard, totalExercisesCard, latestWorkoutCard)) {
            HBox.setHgrow(card, Priority.ALWAYS);
            card.prefWidthProperty().bind(
                    statsRow.widthProperty().subtract(40).divide(3)
            );
        }
    }

    private void loadStats() {
        try {


            System.out.println("Dashboard loaded");

            int exerciseCount = exerciseRepository.findAll().size();
            List<WorkoutLog> allLogs = workoutLogRepository.findAll();

            System.out.println("Exercises: " + exerciseCount);
            System.out.println("Workouts: " + allLogs.size());

            totalExercisesLabel.setText(String.valueOf(exerciseCount));
            totalWorkoutsLabel.setText(String.valueOf(allLogs.size()));

            if (allLogs.isEmpty()) {
                latestWorkoutLabel.setText("No workouts yet");
            } else {
                WorkoutLog latest = allLogs.get(0);
                latestWorkoutLabel.setText(
                        latest.getExerciseName() + " - " + latest.getWeight() + "kg on " + latest.getDate()
                );
            }

            loadChart(allLogs);

        } catch (SQLException exception) {
            exception.printStackTrace();
            latestWorkoutLabel.setText("Could not load data.");
        }
    }

    private void loadChart(List<WorkoutLog> allLogsNewestFirst) {
        List<WorkoutLog> chronological = allLogsNewestFirst.reversed();

        int startIndex = Math.max(0, chronological.size() - 7);
        List<WorkoutLog> lastFew = chronological.subList(startIndex, chronological.size());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Weight");

        for (WorkoutLog log : lastFew) {
            series.getData().add(new XYChart.Data<>(log.getDate(), log.getWeight()));
        }

        progressChart.getData().clear();
        progressChart.getData().add(series);
    }

    private void loadFitnessTip() {
        fitnessTipService.fetchTip(
                tip -> Platform.runLater(() -> tipLabel.setText(tip.getText())),
                error -> Platform.runLater(() -> tipLabel.setText("Could not load tip right now."))
        );
    }

    @FXML
    private void goToDashboard() {
        SceneManager.switchTo("dashboard.fxml", "GymLogFX - Dashboard");
    }

    @FXML
    private void goToExercises() {
        SceneManager.switchTo("exercise.fxml", "GymLogFX - Exercises");
    }

    @FXML
    private void goToRoutines() {
        SceneManager.switchTo("routine_builder.fxml", "GymLogFX - Routines");
    }

    @FXML
    private void goToWorkoutLogger() {
        SceneManager.switchTo("workout_logger.fxml", "GymLogFX - Log Workout");
    }

    @FXML
    private void goToProgress() {
        SceneManager.switchTo("progress.fxml", "GymLogFX - Progress");
    }

    @FXML
    private void goToHistory() {
        SceneManager.switchTo("history.fxml", "GymLogFX - History");
    }
}