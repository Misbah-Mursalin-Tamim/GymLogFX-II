package util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class SceneManager {

    private static Stage primaryStage;

    private SceneManager() {
        // Prevent creating SceneManager objects.
    }

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static void switchTo(String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(
                    SceneManager.class.getResource("/fxml/" + fxmlFileName)
            );

            Parent root = loader.load();

            Scene scene = new Scene(root, 1000, 650);

            primaryStage.setTitle(title);
            primaryStage.setScene(scene);
            primaryStage.show();

        } catch (IOException exception) {
            throw new RuntimeException(
                    "Could not load screen: " + fxmlFileName,
                    exception
            );
        }
    }
}