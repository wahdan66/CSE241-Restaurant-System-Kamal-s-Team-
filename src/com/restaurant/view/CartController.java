package com.restaurant.view;

import com.restaurant.controller.AuthController;
import com.restaurant.controller.OrderController;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.network.OrderSocketClient;
import com.restaurant.network.OrderUpdateMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;

import java.time.LocalDateTime;
import java.util.List;

public class CartController {

    @FXML private ListView<MenuItem> cartListView;
    @FXML private Label orderDetailsLabel;
    @FXML private Label totalLabel;
    @FXML private Label statusLabel;

    private static final ObservableList<MenuItem> cartItems = FXCollections.observableArrayList();
    private final OrderController orderController = new OrderController();
    private final AuthController authController = new AuthController();
    private final OrderSocketClient socketClient = new OrderSocketClient();
    private static OrderDetails orderDetails;

    private record OrderDetails(int tableNumber, int partySize, LocalDateTime scheduledFor) {
    }

    public static ObservableList<MenuItem> getCartItems() {
        return cartItems;
    }

    public static void clearCart() {
        cartItems.clear();
        orderDetails = null;
    }

    public static void setOrderDetails(int tableNumber, int partySize, LocalDateTime scheduledFor) {
        orderDetails = new OrderDetails(tableNumber, partySize, scheduledFor);
    }

    @FXML
    public void initialize() {
        cartListView.setItems(cartItems);
        socketClient.startListening(OrderUpdateMessage::apply);
        showOrderDetails();
        updateTotal();
    }

    private void showOrderDetails() {
        if (orderDetails == null) {
            orderDetailsLabel.setText("Choose table, seats, and time before adding food.");
            return;
        }
        orderDetailsLabel.setText("Table #" + orderDetails.tableNumber() + " • " + orderDetails.partySize()
                + " guests • " + orderDetails.scheduledFor());
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
        if (orderDetails == null) {
            showError("Choose a table, seat count, and time before adding food.");
            return;
        }
        if (authController.getCurrentUser() == null) {
            showError("Please log in before placing an order.");
            return;
        }

        try {
            Order order = orderController.createOrder(cartItems, orderDetails.tableNumber(),
                    authController.getCurrentUser().getId(), orderDetails.partySize(), orderDetails.scheduledFor());
            cartItems.clear();

            socketClient.sendMessage(OrderUpdateMessage.orderCreated(order));

            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            statusLabel.setText("Order " + order.getId() + " placed for Table #" + orderDetails.tableNumber() + ".");
            statusLabel.setVisible(true);
            orderDetails = null;
            showOrderDetails();
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

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusLabel.setText(message);
        statusLabel.setVisible(true);
    }
}
