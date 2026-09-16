package repository;

import db.DatabaseManager;
import model.WorkoutLog;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class WorkoutLogRepository implements Repository<WorkoutLog> {

    @Override
    public int save(WorkoutLog log) throws SQLException {
        String sql = "INSERT INTO workout_log (routine_id, exercise_id, date, weight, reps) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setInt(1, log.getRoutineId());
            statement.setInt(2, log.getExerciseId());
            statement.setString(3, log.getDate());
            statement.setDouble(4, log.getWeight());
            statement.setInt(5, log.getReps());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    log.setId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public List<WorkoutLog> findAll() throws SQLException {
        String sql = """
                SELECT w.workout_id, w.routine_id, w.exercise_id, w.date, w.weight, w.reps,
                       e.name AS exercise_name, r.routine_name
                FROM workout_log w
                JOIN exercise e ON w.exercise_id = e.exercise_id
                JOIN routine r ON w.routine_id = r.routine_id
                ORDER BY w.date DESC
                """;

        return mapResults(sql, null);
    }

    public List<WorkoutLog> findByExercise(int exerciseId) throws SQLException {
        String sql = """
                SELECT w.workout_id, w.routine_id, w.exercise_id, w.date, w.weight, w.reps,
                       e.name AS exercise_name, r.routine_name
                FROM workout_log w
                JOIN exercise e ON w.exercise_id = e.exercise_id
                JOIN routine r ON w.routine_id = r.routine_id
                WHERE w.exercise_id = ?
                ORDER BY w.date ASC
                """;

        return mapResults(sql, exerciseId);
    }

    private List<WorkoutLog> mapResults(String sql, Integer exerciseId) throws SQLException {
        List<WorkoutLog> logs = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            if (exerciseId != null) {
                statement.setInt(1, exerciseId);
            }

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    WorkoutLog log = new WorkoutLog(
                            resultSet.getInt("workout_id"),
                            resultSet.getInt("routine_id"),
                            resultSet.getInt("exercise_id"),
                            resultSet.getString("date"),
                            resultSet.getDouble("weight"),
                            resultSet.getInt("reps")
                    );
                    log.setExerciseName(resultSet.getString("exercise_name"));
                    log.setRoutineName(resultSet.getString("routine_name"));
                    logs.add(log);
                }
            }
        }
        return logs;
    }

    @Override
    public void update(WorkoutLog log) throws SQLException {
        String sql = "UPDATE workout_log SET routine_id = ?, exercise_id = ?, date = ?, weight = ?, reps = ? WHERE workout_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, log.getRoutineId());
            statement.setInt(2, log.getExerciseId());
            statement.setString(3, log.getDate());
            statement.setDouble(4, log.getWeight());
            statement.setInt(5, log.getReps());
            statement.setInt(6, log.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM workout_log WHERE workout_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}