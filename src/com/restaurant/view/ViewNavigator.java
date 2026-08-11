package com.restaurant.view;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Objects;

/**
 * Utility manager responsible for scene switching and stage navigation.
 */
public class ViewNavigator {

    private static Stage primaryStage;

    /**
     * Set the primary application stage during app startup.
     */
    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Alias for setPrimaryStage to ensure compatibility.
     */
    public static void setMainStage(Stage stage) {
        primaryStage = stage;
    }

    /**
     * Dynamic screen switcher loading FXML layout files and applying global CSS.
     *
     * @param fxmlPath Path to the FXML resource (relative to view package or root)
     * @param title    Window title to display
     */
    public static void loadView(String fxmlPath, String title) {
        try {
            Parent root = FXMLLoader.load(Objects.requireNonNull(ViewNavigator.class.getResource(fxmlPath)));
            Scene scene = new Scene(root, 900, 600);

            // Attach global CSS stylesheet
            String cssPath = Objects.requireNonNull(ViewNavigator.class.getResource("/com/restaurant/view/style.css")).toExternalForm();
            scene.getStylesheets().add(cssPath);

            if (primaryStage != null) {
                primaryStage.setTitle("Restaurant Management System - " + title);
                primaryStage.setScene(scene);
                primaryStage.show();
            }

        } catch (IOException e) {
            System.err.println("Failed to load view: " + fxmlPath);
            e.printStackTrace();
        }
    }
}