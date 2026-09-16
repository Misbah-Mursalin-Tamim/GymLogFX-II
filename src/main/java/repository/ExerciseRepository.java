package repository;

import db.DatabaseManager;
import model.Exercise;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class ExerciseRepository implements Repository<Exercise> {

    @Override
    public int save(Exercise exercise) throws SQLException {
        String sql = "INSERT INTO exercise (name, muscle) VALUES (?, ?)";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            statement.setString(1, exercise.getName());
            statement.setString(2, exercise.getMuscle());
            statement.executeUpdate();

            try (ResultSet keys = statement.getGeneratedKeys()) {
                if (keys.next()) {
                    int generatedId = keys.getInt(1);
                    exercise.setId(generatedId);
                    return generatedId;
                }
            }
        }
        return -1;
    }

    @Override
    public List<Exercise> findAll() throws SQLException {
        String sql = "SELECT exercise_id, name, muscle FROM exercise ORDER BY name";
        List<Exercise> exercises = new ArrayList<>();

        try (Connection connection = DatabaseManager.getConnection();
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(sql)) {

            while (resultSet.next()) {
                exercises.add(new Exercise(
                        resultSet.getInt("exercise_id"),
                        resultSet.getString("name"),
                        resultSet.getString("muscle")
                ));
            }
        }
        return exercises;
    }

    public Exercise findById(int exerciseId) throws SQLException {
        String sql = "SELECT exercise_id, name, muscle FROM exercise WHERE exercise_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, exerciseId);

            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return new Exercise(
                            resultSet.getInt("exercise_id"),
                            resultSet.getString("name"),
                            resultSet.getString("muscle")
                    );
                }
            }
        }
        return null;
    }

    @Override
    public void update(Exercise exercise) throws SQLException {
        String sql = "UPDATE exercise SET name = ?, muscle = ? WHERE exercise_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setString(1, exercise.getName());
            statement.setString(2, exercise.getMuscle());
            statement.setInt(3, exercise.getId());
            statement.executeUpdate();
        }
    }

    @Override
    public void delete(int id) throws SQLException {
        String sql = "DELETE FROM exercise WHERE exercise_id = ?";

        try (Connection connection = DatabaseManager.getConnection();
             PreparedStatement statement = connection.prepareStatement(sql)) {

            statement.setInt(1, id);
            statement.executeUpdate();
        }
    }
}