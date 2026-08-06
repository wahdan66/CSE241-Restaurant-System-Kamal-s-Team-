package com.restaurant.view;

import com.restaurant.controller.AuthController;
import com.restaurant.exception.BusinessRuleException;
import com.restaurant.exception.ValidationException;
import com.restaurant.model.User;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

public class LoginController {

    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private Label statusLabel;

    private final AuthController authController = new AuthController();

    @FXML
    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        try {
            // Execute login via backend AuthController
            User user = authController.login(username, password);

            // Display success status
            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            statusLabel.setText("Login successful! Welcome " + user.getName());
            statusLabel.setVisible(true);

            // TODO: Route user based on role (Admin vs Customer vs Waiter) in Task 1.2.3 / 1.2.4

        } catch (ValidationException | BusinessRuleException e) {
            // Real-time validation error feedback
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            statusLabel.setText(e.getMessage());
            statusLabel.setVisible(true);
        }
    }
}