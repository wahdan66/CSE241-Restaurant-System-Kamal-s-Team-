package com.restaurant;

import com.restaurant.network.OrderSocketServer;
import com.restaurant.view.ViewNavigator;
import javafx.application.Application;
import javafx.stage.Stage;

public class MainApp extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Start background socket server for real-time synchronization
        OrderSocketServer.getInstance().startServer();

        // Pass primary stage to ViewNavigator and navigate to Login
        ViewNavigator.setMainStage(primaryStage);
        ViewNavigator.loadView("/com/restaurant/view/login.fxml", "Login");
    }

    public static void main(String[] args) {
        launch(args);
    }
}