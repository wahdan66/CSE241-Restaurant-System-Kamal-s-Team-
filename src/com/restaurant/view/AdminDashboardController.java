package com.restaurant.view;

import com.restaurant.controller.AuthController;
import com.restaurant.controller.MenuController;
import com.restaurant.controller.OrderController;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.network.OrderSocketClient;
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

    @FXML private TableView<Order> ordersTableView;
    @FXML private TableColumn<Order, String> orderIdColumn;
    @FXML private TableColumn<Order, Number> orderTotalColumn;
    @FXML private TableColumn<Order, String> orderStatusColumn;

    @FXML private Label statusLabel;

    private final MenuController menuController = new MenuController();
    private final OrderController orderController = new OrderController();
    private final AuthController authController = new AuthController();
    private final OrderSocketClient socketClient = new OrderSocketClient();

    private final ObservableList<MenuItem> menuList = FXCollections.observableArrayList();
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        // Menu Table setup
        nameColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getName()));
        priceColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPrice()));

        // Orders Table setup
        orderIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        orderTotalColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().calculateTotal()));
        orderStatusColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStatus() != null ? data.getValue().getStatus().toString() : "PENDING"
        ));

        // Start listening for real-time background socket updates
        socketClient.startListening(message -> {
            if ("REFRESH_ORDERS".equals(message)) {
                loadOrdersData();
            } else if ("REFRESH_MENU".equals(message)) {
                loadMenuData();
            }
        });

        loadMenuData();
        loadOrdersData();
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

    private void loadOrdersData() {
        try {
            List<Order> orders = orderController.getAllOrders();
            orderList.setAll(orders);
            ordersTableView.setItems(orderList);
        } catch (Exception e) {
            orderList.clear();
        }
    }

    @FXML
    private void handleRefreshOrders() {
        loadOrdersData();
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
        statusLabel.setText("Orders refreshed successfully.");
        statusLabel.setVisible(true);
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

            MenuItem newItem = new MenuItem(id, name, description, price, category);
            menuController.addMenuItem(id, name, description, price, category);
            menuList.add(newItem);

            // Broadcast real-time menu update to other clients
            socketClient.sendMessage("REFRESH_MENU");

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
            menuController.deleteMenuItem(selected.getId());
            menuList.remove(selected);

            // Broadcast real-time menu update to other clients
            socketClient.sendMessage("REFRESH_MENU");

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