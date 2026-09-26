package controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import model.Exercise;
import model.Routine;
import model.RoutineExercise;
import repository.ExerciseRepository;
import repository.RoutineRepository;
import util.SceneManager;

import java.sql.SQLException;
import java.util.List;

public class RoutineBuilderController {

    @FXML
    private TableView<Routine> routineTable;

    @FXML
    private TableColumn<Routine, String> routineNameColumn;

    @FXML
    private TextField routineNameField;

    @FXML
    private ListView<Exercise> availableListView;

    @FXML
    private ListView<Exercise> chosenListView;

    @FXML
    private Label statusLabel;

    private final RoutineRepository routineRepository = new RoutineRepository();
    private final ExerciseRepository exerciseRepository = new ExerciseRepository();

    private final ObservableList<Routine> routineList = FXCollections.observableArrayList();
    private final ObservableList<Exercise> availableExercises = FXCollections.observableArrayList();
    private final ObservableList<Exercise> chosenExercises = FXCollections.observableArrayList();

    private Routine selectedRoutine = null;

    @FXML
    public void initialize() {
        routineTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        routineNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        routineTable.setItems(routineList);

        availableListView.setItems(availableExercises);
        chosenListView.setItems(chosenExercises);

        routineTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                loadRoutineDetails(newValue);
            }
        });

        loadRoutines();
        resetExerciseLists();
    }

    private void loadRoutines() {
        try {
            routineList.setAll(routineRepository.findAll());
        } catch (SQLException exception) {
            showError("Could not load routines.");
        }
    }

    private void resetExerciseLists() {
        try {
            availableExercises.setAll(exerciseRepository.findAll());
            chosenExercises.clear();
        } catch (SQLException exception) {
            showError("Could not load exercises.");
        }
    }

    private void loadRoutineDetails(Routine routine) {
        selectedRoutine = routine;
        routineNameField.setText(routine.getName());

        try {
            List<Exercise> allExercises = exerciseRepository.findAll();
            List<RoutineExercise> linked = routineRepository.getExercisesForRoutine(routine.getId());

            chosenExercises.clear();
            availableExercises.clear();

            for (Exercise exercise : allExercises) {
                boolean isLinked = linked.stream()
                        .anyMatch(link -> link.getExerciseId() == exercise.getId());

                if (isLinked) {
                    chosenExercises.add(exercise);
                } else {
                    availableExercises.add(exercise);
                }
            }

        } catch (SQLException exception) {
            showError("Could not load routine details.");
        }
    }

    @FXML
    private void handleAddToRoutine() {
        Exercise chosen = availableListView.getSelectionModel().getSelectedItem();

        if (chosen == null) {
            showError("Select an exercise from the Available list first.");
            return;
        }

        availableExercises.remove(chosen);
        chosenExercises.add(chosen);
        hideError();
    }

    @FXML
    private void handleRemoveFromRoutine() {
        Exercise chosen = chosenListView.getSelectionModel().getSelectedItem();

        if (chosen == null) {
            showError("Select an exercise from the In-Routine list first.");
            return;
        }

        chosenExercises.remove(chosen);
        availableExercises.add(chosen);
        hideError();
    }

    @FXML
    private void handleNew() {
        selectedRoutine = null;
        routineNameField.clear();
        routineTable.getSelectionModel().clearSelection();
        resetExerciseLists();
        hideError();
    }

    @FXML
    private void handleSave() {
        String name = routineNameField.getText();

        if (name == null || name.isBlank()) {
            showError("Please enter a routine name.");
            return;
        }

        try {
            if (selectedRoutine == null) {
                Routine routine = new Routine(0, name.trim());
                int newId = routineRepository.save(routine);

                for (Exercise exercise : chosenExercises) {
                    routineRepository.addExerciseToRoutine(newId, exercise.getId());
                }

            } else {
                selectedRoutine.setName(name.trim());
                routineRepository.update(selectedRoutine);
                routineRepository.clearExercisesForRoutine(selectedRoutine.getId());

                for (Exercise exercise : chosenExercises) {
                    routineRepository.addExerciseToRoutine(selectedRoutine.getId(), exercise.getId());
                }
            }

            loadRoutines();
            handleNew();

        } catch (SQLException exception) {
            showError("Could not save routine.");
        }
    }

    @FXML
    private void handleDelete() {
        if (selectedRoutine == null) {
            showError("Select a routine from the table first.");
            return;
        }

        try {
            routineRepository.delete(selectedRoutine.getId());
            loadRoutines();
            handleNew();
        } catch (SQLException exception) {
            showError("Could not delete routine.");
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