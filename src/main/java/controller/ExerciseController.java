package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Exercise;
import repository.ExerciseRepository;
import util.SceneManager;

import java.sql.SQLException;

public class ExerciseController {

    @FXML
    private TableView<Exercise> exerciseTable;

    @FXML
    private TableColumn<Exercise, String> nameColumn;

    @FXML
    private TableColumn<Exercise, String> muscleColumn;

    @FXML
    private TextField nameField;

    @FXML
    private ComboBox<String> muscleComboBox;

    @FXML
    private Label statusLabel;

    private final ExerciseRepository exerciseRepository = new ExerciseRepository();
    private final ObservableList<Exercise> exerciseList = FXCollections.observableArrayList();

    private Exercise selectedExercise = null;

    @FXML
    public void initialize() {
        exerciseTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        nameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        muscleColumn.setCellValueFactory(new PropertyValueFactory<>("muscle"));

        muscleComboBox.setItems(FXCollections.observableArrayList(
                "Chest", "Back", "Legs", "Shoulders", "Arms", "Core", "Cardio"
        ));

        exerciseTable.setItems(exerciseList);

        exerciseTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                selectedExercise = newValue;
                nameField.setText(newValue.getName());
                muscleComboBox.setValue(newValue.getMuscle());
            }
        });

        loadExercises();
    }

    private void loadExercises() {
        try {
            exerciseList.setAll(exerciseRepository.findAll());
        } catch (SQLException exception) {
            showError("Could not load exercises from the database.");
        }
    }

    @FXML
    private void handleAdd() {
        if (!isFormValid()) {
            return;
        }

        try {
            Exercise exercise = new Exercise(0, nameField.getText().trim(), muscleComboBox.getValue());
            exerciseRepository.save(exercise);
            loadExercises();
            handleClear();
        } catch (SQLException exception) {
            showError("Could not add exercise.");
        }
    }

    @FXML
    private void handleUpdate() {
        if (selectedExercise == null) {
            showError("Select an exercise from the table first.");
            return;
        }

        if (!isFormValid()) {
            return;
        }

        try {
            selectedExercise.setName(nameField.getText().trim());
            selectedExercise.setMuscle(muscleComboBox.getValue());
            exerciseRepository.update(selectedExercise);
            loadExercises();
            handleClear();
        } catch (SQLException exception) {
            showError("Could not update exercise.");
        }
    }

    @FXML
    private void handleDelete() {
        Exercise chosen = exerciseTable.getSelectionModel().getSelectedItem();

        if (chosen == null) {
            showError("Select an exercise from the table first.");
            return;
        }

        try {
            exerciseRepository.delete(chosen.getId());
            loadExercises();
            handleClear();
        } catch (SQLException exception) {
            showError("Could not delete exercise. It may be used in a routine or workout log.");
        }
    }

    @FXML
    private void handleClear() {
        nameField.clear();
        muscleComboBox.setValue(null);
        selectedExercise = null;
        exerciseTable.getSelectionModel().clearSelection();
        hideError();
    }

    private boolean isFormValid() {
        if (nameField.getText() == null || nameField.getText().isBlank() || muscleComboBox.getValue() == null) {
            showError("Please enter a name and choose a muscle group.");
            return false;
        }
        hideError();
        return true;
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