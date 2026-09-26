package controller;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import util.SceneManager;

public class LoginController {

    @FXML
    private StackPane rootPane;

    @FXML
    private VBox card;

    @FXML
    private TextField usernameField;

    @FXML
    private PasswordField passwordField;

    @FXML
    private Label errorLabel;

    @FXML
    private Button loginButton;

    @FXML
    public void initialize() {
        // Makes the login card resize proportionally with the window width.
        card.prefWidthProperty().bind(rootPane.widthProperty().multiply(0.35));
    }

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username == null || username.isBlank() || password == null || password.isBlank()) {
            errorLabel.setText("Please enter both username and password.");
            errorLabel.setVisible(true);
            errorLabel.setManaged(true);
            return;
        }

        SceneManager.switchTo("dashboard.fxml", "GymLogFX - Dashboard");
    }
}