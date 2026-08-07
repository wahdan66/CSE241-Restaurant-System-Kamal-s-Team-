package com.restaurant.view;

import com.restaurant.controller.AuthController;
import com.restaurant.controller.MenuController;
import com.restaurant.model.MenuItem;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.util.List;

public class CustomerDashboardController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ListView<MenuItem> menuListView;
    @FXML private Label statusLabel;

    private final MenuController menuController = new MenuController();
    private final AuthController authController = new AuthController();
    private final ObservableList<MenuItem> observableMenuList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        loadMenuItems();

        // Listen for search input changes
        searchField.textProperty().addListener((observable, oldValue, newValue) -> filterMenu());
    }

    private void loadMenuItems() {
        try {
            List<MenuItem> items = menuController.getAllMenuItems();
            observableMenuList.setAll(items);
            menuListView.setItems(observableMenuList);
        } catch (Exception e) {
            statusLabel.setText("Could not load menu: " + e.getMessage());
            statusLabel.setVisible(true);
        }
    }

    private void filterMenu() {
        String query = searchField.getText().toLowerCase().trim();
        if (query.isEmpty()) {
            menuListView.setItems(observableMenuList);
        } else {
            ObservableList<MenuItem> filtered = observableMenuList.filtered(item ->
                    item.getName().toLowerCase().contains(query)
            );
            menuListView.setItems(filtered);
        }
    }

    @FXML
    private void handleAddToCart() {
        MenuItem selectedItem = menuListView.getSelectionModel().getSelectedItem();
        if (selectedItem == null) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            statusLabel.setText("Please select a menu item first.");
            statusLabel.setVisible(true);
            return;
        }

        statusLabel.setStyle("-fx-text-fill: #229954;");
        statusLabel.setText("Added " + selectedItem.getName() + " to cart!");
        statusLabel.setVisible(true);
    }

    @FXML
    private void handleViewCart() {
        // Will route to Cart screen in Task 1.2.4
    }

    @FXML
    private void handleBookReservation() {
        // Will route to Reservation screen in Task 1.2.5
    }

    @FXML
    private void handleLogout() {
        authController.logout();
        ViewNavigator.loadView("/com/restaurant/view/login.fxml", "Login");
    }
}