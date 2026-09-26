package controller;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import model.Exercise;
import model.WorkoutLog;
import repository.ExerciseRepository;
import repository.WorkoutLogRepository;
import service.ProgressService;
import util.SceneManager;

import java.sql.SQLException;
import java.util.List;

public class ProgressController {

    @FXML
    private ComboBox<Exercise> exerciseComboBox;

    @FXML
    private LineChart<String, Number> progressChart;

    @FXML
    private Label resultLabel;

    private final ExerciseRepository exerciseRepository = new ExerciseRepository();
    private final WorkoutLogRepository workoutLogRepository = new WorkoutLogRepository();
    private final ProgressService progressService = new ProgressService();

    @FXML
    public void initialize() {
        loadExercises();

        exerciseComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadProgressFor(newValue);
            }
        });
    }

    private void loadExercises() {
        try {
            exerciseComboBox.setItems(FXCollections.observableArrayList(exerciseRepository.findAll()));
        } catch (SQLException exception) {
            resultLabel.setText("Could not load exercises.");
        }
    }

    private void loadProgressFor(Exercise exercise) {
        try {
            List<WorkoutLog> logs = workoutLogRepository.findByExercise(exercise.getId());

            updateChart(logs);
            updateResultMessage(logs);

        } catch (SQLException exception) {
            resultLabel.setText("Could not load progress for this exercise.");
        }
    }

    private void updateChart(List<WorkoutLog> logs) {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Weight");

        for (WorkoutLog log : logs) {
            series.getData().add(new XYChart.Data<>(log.getDate(), log.getWeight()));
        }

        progressChart.getData().clear();
        progressChart.getData().add(series);
    }

    private void updateResultMessage(List<WorkoutLog> logs) {
        String message = progressService.compareLatestTwo(logs);
        resultLabel.setText(message);
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