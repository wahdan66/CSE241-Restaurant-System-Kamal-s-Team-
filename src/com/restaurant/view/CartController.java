package com.restaurant.view;

import com.restaurant.controller.OrderController;
import com.restaurant.model.MenuItem;
import com.restaurant.network.OrderSocketClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.util.ArrayList;

public class CartController {

    @FXML private ListView<MenuItem> cartListView;
    @FXML private Label totalLabel;
    @FXML private Label statusLabel;

    private static final ObservableList<MenuItem> cartItems = FXCollections.observableArrayList();
    private final OrderController orderController = new OrderController();
    private final OrderSocketClient socketClient = new OrderSocketClient();

    public static ObservableList<MenuItem> getCartItems() {
        return cartItems;
    }

    @FXML
    public void initialize() {
        cartListView.setItems(cartItems);
        updateTotal();
    }

    private void updateTotal() {
        double total = cartItems.stream().mapToDouble(MenuItem::getPrice).sum();
        totalLabel.setText(String.format("Total: $%.2f", total));
    }

    @FXML
    private void handleRemoveItem() {
        MenuItem selected = cartListView.getSelectionModel().getSelectedItem();
        if (selected != null) {
            cartItems.remove(selected);
            updateTotal();
        }
    }

    @FXML
    private void handlePlaceOrder() {
        if (cartItems.isEmpty()) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            statusLabel.setText("Your cart is empty!");
            statusLabel.setVisible(true);
            return;
        }

        try {
            // Converts ObservableList to a standard ArrayList to match OrderController
            orderController.createOrder(new ArrayList<>(cartItems));
            cartItems.clear();

            // Broadcast real-time notification to Waiter & Admin screens
            socketClient.sendMessage("REFRESH_ORDERS");

            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            statusLabel.setText("Order placed successfully!");
            statusLabel.setVisible(true);
            updateTotal();
        } catch (Exception e) {
            statusLabel.setStyle("-fx-text-fill: #e74c3c;");
            statusLabel.setText("Failed to place order: " + e.getMessage());
            statusLabel.setVisible(true);
        }
    }

    @FXML
    private void handleBackToMenu() {
        ViewNavigator.loadView("/com/restaurant/view/customer_dashboard.fxml", "Customer Dashboard");
    }
}