package com.restaurant.view;

import com.restaurant.controller.AuthController;
import com.restaurant.exception.BusinessRuleException;
import com.restaurant.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private Label statusLabel;

    private final AuthController authController = new AuthController();

    @FXML
    private void handleRegister() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // Reuses your existing login/authentication pipeline
            User user = authController.login(username, password);

            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            statusLabel.setText("Welcome back, " + user.getName());
            statusLabel.setVisible(true);

        } catch (BusinessRuleException e) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            statusLabel.setText(e.getMessage());
            statusLabel.setVisible(true);
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            statusLabel.setText("Error: " + e.getMessage());
            statusLabel.setVisible(true);
        }
    }

    @FXML
    private void handleBackToLogin() {
        ViewNavigator.loadView("/com/restaurant/view/login.fxml", "Login");
    }
}