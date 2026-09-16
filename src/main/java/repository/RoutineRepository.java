package repository;

import db.DatabaseManager;
import model.Routine;
import model.RoutineExercise;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class RoutineRepository implements Repository<Routine> {

    @Override
    public int save(Routine routine) throws SQLException {
        String sql = "INSERT INTO routine (routine_name) VALUES (?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, routine.getName());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    routine.setId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public List<Routine> findAll() throws SQLException {
        String sql = "SELECT routine_id, routine_name FROM routine ORDER BY routine_name";
        List<Routine> routines = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                routines.add(new Routine(
                        resultSet.getInt("routine_id"),
                        resultSet.getString("routine_name")
                ));
            }
        }
        return routines;
    }

    @Override
    public void update(Routine routine) throws SQLException {
        String sql = "UPDATE routine SET routine_name = ? WHERE routine_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, routine.getName());
            statement.setInt(2, routine.getId());
            statement.executeUpdate();
        }
    }

    public void clearExercisesForRoutine(int routineId) throws SQLException {
        String sql = "DELETE FROM routine_exercise WHERE routine_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, routineId);
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM routine WHERE routine_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }

    // ---- Relationship methods: routine <-> exercise ----

    public void addExerciseToRoutine(int routineId, int exerciseId) throws SQLException {
        String sql = "INSERT OR IGNORE INTO routine_exercise (routine_id, exercise_id) VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, routineId);
            statement.setInt(2, exerciseId);
            statement.executeUpdate();
        }
    }

    public void removeExerciseFromRoutine(int routineId, int exerciseId) throws SQLException {
        String sql = "DELETE FROM routine_exercise WHERE routine_id = ? AND exercise_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, routineId);
            statement.setInt(2, exerciseId);
            statement.executeUpdate();
        }
    }

    public List<RoutineExercise> getExercisesForRoutine(int routineId) throws SQLException {
        String sql = """
                SELECT re.id, re.routine_id, re.exercise_id, e.name
                FROM routine_exercise re
                JOIN exercise e ON re.exercise_id = e.exercise_id
                WHERE re.routine_id = ?
                ORDER BY e.name
                """;

        List<RoutineExercise> result = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, routineId);

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    result.add(new RoutineExercise(
                            resultSet.getInt("id"),
                            resultSet.getInt("routine_id"),
                            resultSet.getInt("exercise_id"),
                            resultSet.getString("name")
                    ));
                }
            }
        }
        return result;
    }
}