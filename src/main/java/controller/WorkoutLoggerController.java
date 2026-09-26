package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Exercise;
import model.Routine;
import model.RoutineExercise;
import model.WorkoutLog;
import repository.RoutineRepository;
import repository.WorkoutLogRepository;
import util.SceneManager;

import java.sql.SQLException;
import java.time.LocalDate;

public class WorkoutLoggerController {

    @FXML
    private ComboBox<Routine> routineComboBox;

    @FXML
    private DatePicker datePicker;

    @FXML
    private ComboBox<Exercise> exerciseComboBox;

    @FXML
    private TextField weightField;

    @FXML
    private TextField repsField;

    @FXML
    private TableView<SetEntry> setsTable;

    @FXML
    private TableColumn<SetEntry, String> exerciseColumn;

    @FXML
    private TableColumn<SetEntry, Double> weightColumn;

    @FXML
    private TableColumn<SetEntry, Integer> repsColumn;

    @FXML
    private Label statusLabel;

    private final RoutineRepository routineRepository = new RoutineRepository();
    private final WorkoutLogRepository workoutLogRepository = new WorkoutLogRepository();

    private final ObservableList<SetEntry> setEntries = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        setsTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        exerciseColumn.setCellValueFactory(new PropertyValueFactory<>("exerciseName"));
        weightColumn.setCellValueFactory(new PropertyValueFactory<>("weight"));
        repsColumn.setCellValueFactory(new PropertyValueFactory<>("reps"));

        setsTable.setItems(setEntries);

        datePicker.setValue(LocalDate.now());

        loadRoutines();

        routineComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadExercisesForRoutine(newValue);
            }
        });
    }

    private void loadRoutines() {
        try {
            routineComboBox.setItems(FXCollections.observableArrayList(routineRepository.findAll()));
        } catch (SQLException exception) {
            showError("Could not load routines.");
        }
    }

    private void loadExercisesForRoutine(Routine routine) {
        try {
            var linkedExercises = routineRepository.getExercisesForRoutine(routine.getId());

            ObservableList<Exercise> exercises = FXCollections.observableArrayList();

            for (RoutineExercise link : linkedExercises) {
                exercises.add(new Exercise(link.getExerciseId(), link.getExerciseName(), ""));
            }

            exerciseComboBox.setItems(exercises);

        } catch (SQLException exception) {
            showError("Could not load exercises for this routine.");
        }
    }

    @FXML
    private void handleAddSet() {
        Routine routine = routineComboBox.getValue();
        Exercise exercise = exerciseComboBox.getValue();

        if (routine == null) {
            showError("Please choose a routine first.");
            return;
        }

        if (exercise == null) {
            showError("Please choose an exercise.");
            return;
        }

        Double weight = parseWeight(weightField.getText());
        Integer reps = parseReps(repsField.getText());

        if (weight == null || reps == null) {
            showError("Please enter a valid weight and reps.");
            return;
        }

        setEntries.add(new SetEntry(exercise.getId(), exercise.getName(), weight, reps));

        weightField.clear();
        repsField.clear();
        hideError();
    }

    @FXML
    private void handleRemoveSet() {
        SetEntry selected = setsTable.getSelectionModel().getSelectedItem();

        if (selected == null) {
            showError("Select a set from the table first.");
            return;
        }

        setEntries.remove(selected);
        hideError();
    }

    @FXML
    private void handleSaveWorkout() {
        Routine routine = routineComboBox.getValue();
        LocalDate date = datePicker.getValue();

        if (routine == null) {
            showError("Please choose a routine.");
            return;
        }

        if (date == null) {
            showError("Please choose a date.");
            return;
        }

        if (setEntries.isEmpty()) {
            showError("Add at least one set before saving.");
            return;
        }

        try {
            for (SetEntry entry : setEntries) {
                WorkoutLog log = new WorkoutLog(
                        0,
                        routine.getId(),
                        entry.getExerciseId(),
                        date.toString(),
                        entry.getWeight(),
                        entry.getReps()
                );
                workoutLogRepository.save(log);
            }

            setEntries.clear();
            hideError();
            showSuccess("Workout saved successfully!");

        } catch (SQLException exception) {
            showError("Could not save workout.");
        }
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
        statusLabel.setStyle("-fx-text-fill: #D9534F; -fx-font-size: 12;");
        statusLabel.setText(message);
        statusLabel.setVisible(true);
        statusLabel.setManaged(true);
    }

    private void showSuccess(String message) {
        statusLabel.setStyle("-fx-text-fill: #2D6CDF; -fx-font-size: 12;");
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

    // Small helper class just to hold one row of the in-memory sets table
    // before it gets saved to the database. Not a database entity itself.
    public static class SetEntry {

        private final int exerciseId;
        private final String exerciseName;
        private final double weight;
        private final int reps;

        public SetEntry(int exerciseId, String exerciseName, double weight, int reps) {
            this.exerciseId = exerciseId;
            this.exerciseName = exerciseName;
            this.weight = weight;
            this.reps = reps;
        }

        public int getExerciseId() {
            return exerciseId;
        }

        public String getExerciseName() {
            return exerciseName;
        }

        public double getWeight() {
            return weight;
        }

        public int getReps() {
            return reps;
        }
    }
}