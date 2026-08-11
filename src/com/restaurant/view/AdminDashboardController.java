package com.restaurant.view;

import com.restaurant.controller.AuthController;
import com.restaurant.controller.MenuController;
import com.restaurant.model.MenuItem;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

import java.util.List;

public class AdminDashboardController {

    @FXML private TextField itemNameField;
    @FXML private TextField itemPriceField;
    @FXML private TableView<MenuItem> menuTableView;
    @FXML private TableColumn<MenuItem, String> nameColumn;
    @FXML private TableColumn<MenuItem, Number> priceColumn;
    @FXML private Label statusLabel;

    private final MenuController menuController = new MenuController();
    private final AuthController authController = new AuthController();
    private final ObservableList<MenuItem> menuList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Setup Table Columns
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        priceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()));

        loadMenuData();
    }

    private void loadMenuData() {
        try {
            List<MenuItem> items = menuController.getAllMenuItems();
            menuList.setAll(items);
            menuTableView.setItems(menuList);
        } catch (Exception e) {
            showError("Could not load menu: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddItem() {
        String name = itemNameField.getText().trim();
        String priceText = itemPriceField.getText().trim();

        if (name.isEmpty() || priceText.isEmpty()) {
            showError("Please enter both dish name and price.");
            return;
        }

        try {
            double price = Double.parseDouble(priceText);

            String id = "ITEM_" + System.currentTimeMillis();
            String description = "No description provided";
            com.restaurant.model.MenuCategory category = com.restaurant.model.MenuCategory.MAIN_COURSE;

            // Pass all 5 arguments to match both the MenuItem constructor and MenuController
            MenuItem newItem = new MenuItem(id, name, description, price, category);
            menuController.addMenuItem(id, name, description, price, category);
            menuList.add(newItem);

            itemNameField.clear();
            itemPriceField.clear();

            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            statusLabel.setText("Added " + name + " to menu successfully!");
            statusLabel.setVisible(true);
        } catch (NumberFormatException e) {
            showError("Price must be a valid number.");
        } catch (Exception e) {
            showError("Failed to add menu item: " + e.getMessage());
        }
    }

    @FXML
    private void handleDeleteItem() {
        MenuItem selected = menuTableView.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showError("Select an item from the table to delete.");
            return;
        }

        try {
            // Remove from backend controller and UI table
            menuController.deleteMenuItem(selected.getId());
            menuList.remove(selected);

            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            statusLabel.setText("Removed " + selected.getName() + " from menu.");
            statusLabel.setVisible(true);
        } catch (Exception e) {
            showError("Failed to delete menu item: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusLabel.setText(message);
        statusLabel.setVisible(true);
    }

    @FXML
    private void handleLogout() {
        authController.logout();
        ViewNavigator.loadView("/com/restaurant/view/login.fxml", "Login");
    }
}