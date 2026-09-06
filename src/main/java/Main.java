import db.DatabaseManager;
import javafx.application.Application;
import javafx.stage.Stage;
import util.AppExecutor;
import util.SceneManager;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) {

        DatabaseManager.initializeDatabase();

        SceneManager.setPrimaryStage(primaryStage);

        primaryStage.setMinWidth(900);
        primaryStage.setMinHeight(600);

        primaryStage.setOnCloseRequest(event -> AppExecutor.shutdown());

        SceneManager.switchTo("login.fxml", "GymLogFX - Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}
