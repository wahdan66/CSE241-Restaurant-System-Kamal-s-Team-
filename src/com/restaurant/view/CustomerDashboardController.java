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

        // Populate Category Dropdown
        categoryFilter.setItems(FXCollections.observableArrayList(
                "All Categories", "Main Course", "Appetizers", "Beverages", "Desserts"
        ));
        categoryFilter.getSelectionModel().selectFirst();

        // Listen for search input and dropdown changes
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterMenu());
        categoryFilter.setOnAction(event -> filterMenu());
    }

    private void filterMenu() {
        String query = searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryFilter.getSelectionModel().getSelectedItem();

        ObservableList<MenuItem> filtered = observableMenuList.filtered(item -> {
            boolean matchesSearch = query.isEmpty() || item.getName().toLowerCase().contains(query);
            boolean matchesCategory = selectedCategory == null
                    || selectedCategory.equals("All Categories")
                    || (item.getCategory() != null && item.getCategory().equalsIgnoreCase(selectedCategory));

            return matchesSearch && matchesCategory;
        });

        menuListView.setItems(filtered);
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
    private void handleBookReservation() {
        // Will route to Reservation screen in Task 1.2.5
    }

    @FXML
    private void handleLogout() {
        authController.logout();
        ViewNavigator.loadView("/com/restaurant/view/login.fxml", "Login");
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

        // Add item to shared cart list
        CartController.getCartItems().add(selectedItem);

        statusLabel.setStyle("-fx-text-fill: #229954;");
        statusLabel.setText("Added " + selectedItem.getName() + " to cart!");
        statusLabel.setVisible(true);
    }

    @FXML
    private void handleViewCart() {
        ViewNavigator.loadView("/com/restaurant/view/cart.fxml", "Your Cart");
    }
}