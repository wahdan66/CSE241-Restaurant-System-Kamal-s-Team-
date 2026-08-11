package com.restaurant.view;

import com.restaurant.db.UserDAO;
import com.restaurant.exception.BusinessRuleException;
import com.restaurant.model.Customer;
import com.restaurant.util.InputValidator;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;

import java.util.UUID;

public class RegisterController {

    @FXML private TextField nameField;
    @FXML private TextField usernameField;
    @FXML private PasswordField passwordField;
    @FXML private TextField phoneField;
    @FXML private Label statusLabel;

    private final UserDAO userDAO = new UserDAO();

    @FXML
    private void handleRegister() {
        String name = nameField.getText();
        String username = usernameField.getText();
        String password = passwordField.getText();
        String phone = phoneField.getText();

        try {
            // Validate inputs using project's validator
            InputValidator.validateNotNullOrEmpty(name, "Name");
            InputValidator.validateNotNullOrEmpty(username, "Username");
            InputValidator.validateNotNullOrEmpty(password, "Password");
            InputValidator.validateNotNullOrEmpty(phone, "Phone");

            // Check if username is already taken
            if (userDAO.findByUsername(username).isPresent()) {
                throw new BusinessRuleException("Username is already taken.");
            }

            // Save new customer account
            Customer customer = new Customer(
                    UUID.randomUUID().toString(),
                    name,
                    username,
                    password,
                    phone
            );
            userDAO.save(customer);

            // Display success message
            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            statusLabel.setText("Registration successful! Click 'Back to Login' to sign in.");
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