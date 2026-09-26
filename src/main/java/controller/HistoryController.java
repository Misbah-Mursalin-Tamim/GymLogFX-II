package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.WorkoutLog;
import repository.WorkoutLogRepository;
import util.SceneManager;

import java.sql.SQLException;

public class HistoryController {

    @FXML
    private TableView<WorkoutLog> historyTable;

    @FXML
    private TableColumn<WorkoutLog, String> dateColumn;

    @FXML
    private TableColumn<WorkoutLog, String> routineColumn;

    @FXML
    private TableColumn<WorkoutLog, String> exerciseColumn;

    @FXML
    private TableColumn<WorkoutLog, Double> weightColumn;

    @FXML
    private TableColumn<WorkoutLog, Integer> repsColumn;

    @FXML
    private TextField weightField;

    @FXML
    private TextField repsField;

    @FXML
    private Label statusLabel;

    private final WorkoutLogRepository workoutLogRepository = new WorkoutLogRepository();
    private final ObservableList<WorkoutLog> logList = FXCollections.observableArrayList();

    private WorkoutLog selectedLog = null;

    @FXML
    public void initialize() {
        historyTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        routineColumn.setCellValueFactory(new PropertyValueFactory<>("routineName"));
        exerciseColumn.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        repsColumn.setCellValueFactory(new PropertyValueFactory<>("reps"));

        historyTable.setItems(logList);

        historyTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedLog = newValue;
                weightField.setText(String.valueOf(newValue.getWeight()));
                repsField.setText(String.valueOf(newValue.getReps()));
                hideError();
            }
        });

        loadHistory();
    }

    private void loadHistory() {
        try {
            logList.setAll(workoutLogRepository.findAll());
        } catch (SQLException exception) {
            showError("Could not load workout history.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedLog == null) {
            showError("Select an entry from the table first.");
            return;
        }

        Double weight = parseWeight(weightField.getText());
        Integer reps = parseReps(repsField.getText());

        if (weight == null || reps == null) {
            showError("Please enter a valid weight and reps.");
            return;
        }

        try {
            selectedLog.setWeight(weight);
            selectedLog.setReps(reps);
            workoutLogRepository.update(selectedLog);
            loadHistory();
            hideError();
        } catch (SQLException exception) {
            showError("Could not update entry.");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedLog == null) {
            showError("Select an entry from the table first.");
            return;
        }

        try {
            workoutLogRepository.delete(selectedLog.getId());
            loadHistory();
            clearForm();
        } catch (SQLException exception) {
            showError("Could not delete entry.");
        }
    }

    private void clearForm() {
        weightField.clear();
        repsField.clear();
        selectedLog = null;
        historyTable.getSelectionModel().clearSelection();
        hideError();
    }

    private Double parseWeight(String text) {
        try {
            return Double.parseDouble(text.trim());
        } catch (Exception exception) {
            return null;
        }
    }

    private Integer parseReps(String text) {
        try {
            return Integer.parseInt(text.trim());
        } catch (Exception exception) {
            return null;
        }
    }

    private void showError(String message) {
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void hideError() {
        statusLabel.setVisible(false);
        statusLabel.setManaged(false);
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