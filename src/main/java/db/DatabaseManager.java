package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {

    private static final String DATABASE_URL = "jdbc:sqlite:gymlogfx.db";

    private DatabaseManager() {
        // Prevent creating DatabaseManager objects.
    }

    public static Connection getConnection() throws SQLException {
        Connection connection = DriverManager.getConnection(DATABASE_URL);

        // SQLite requires this to be enabled for every connection.
        try (Statement statement = connection.createStatement()) {
            statement.execute("PRAGMA foreign_keys = ON");
        }

        return connection;
    }

    public static void initializeDatabase() {
        String createExerciseTable = """
                CREATE TABLE IF NOT EXISTS exercise (
                    exercise_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    name TEXT NOT NULL,
                    muscle TEXT NOT NULL
                )
                """;

        String createRoutineTable = """
                CREATE TABLE IF NOT EXISTS routine (
                    routine_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    routine_name TEXT NOT NULL
                )
                """;

        String createRoutineExerciseTable = """
                CREATE TABLE IF NOT EXISTS routine_exercise (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    routine_id INTEGER NOT NULL,
                    exercise_id INTEGER NOT NULL,

                    FOREIGN KEY (routine_id)
                        REFERENCES routine(routine_id)
                        ON DELETE CASCADE,

                    FOREIGN KEY (exercise_id)
                        REFERENCES exercise(exercise_id)
                        ON DELETE CASCADE,

                    UNIQUE (routine_id, exercise_id)
                )
                """;

        String createWorkoutLogTable = """
                CREATE TABLE IF NOT EXISTS workout_log (
                    workout_id INTEGER PRIMARY KEY AUTOINCREMENT,
                    routine_id INTEGER NOT NULL,
                    exercise_id INTEGER NOT NULL,
                    date TEXT NOT NULL,
                    weight REAL NOT NULL,
                    reps INTEGER NOT NULL,

                    FOREIGN KEY (routine_id)
                        REFERENCES routine(routine_id)
                        ON DELETE CASCADE,

                    FOREIGN KEY (exercise_id)
                        REFERENCES exercise(exercise_id)
                        ON DELETE CASCADE
                )
                """;

        try (
                Connection connection = getConnection();
                Statement statement = connection.createStatement()
        ) {
            statement.executeUpdate(createExerciseTable);
            statement.executeUpdate(createRoutineTable);
            statement.executeUpdate(createRoutineExerciseTable);
            statement.executeUpdate(createWorkoutLogTable);

            System.out.println("Database initialized successfully.");

        } catch (SQLException exception) {
            throw new RuntimeException(
                    "Could not initialize the GymLogFX database.",
                    exception
            );
        }
    }
}