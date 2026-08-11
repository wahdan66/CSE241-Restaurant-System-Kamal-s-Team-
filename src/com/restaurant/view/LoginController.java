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
            User user = authController.login(username, password);

            // Load the Customer Dashboard on successful login
            ViewNavigator.loadView("/com/restaurant/view/customer_dashboard.fxml", "Customer Dashboard");

        } catch (BusinessRuleException e) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            statusLabel.setText(e.getMessage());
            statusLabel.setVisible(true);
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            statusLabel.setText("An error occurred: " + e.getMessage());
            statusLabel.setVisible(true);
        }
    }
    @FXML
    private void handleGoToRegister() {
        ViewNavigator.loadView("/com/restaurant/view/register.fxml", "Register Customer");
    }
}