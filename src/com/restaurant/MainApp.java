package com.restaurant;

import com.restaurant.view.ViewNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main JavaFX Application launcher.
 */
public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Register the primary stage into the navigation framework
        ViewNavigator.setPrimaryStage(primaryStage);

        // Load the initial screen (e.g., Login view in Task 1.2.2)
        ViewNavigator.loadView("/com/restaurant/view/login.fxml", "Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}