package com.restaurant.view;

import com.restaurant.controller.AuthController;
import com.restaurant.controller.OrderController;
import com.restaurant.model.Order;
import com.restaurant.model.OrderStatus;
import com.restaurant.network.OrderSocketClient;
import com.restaurant.network.OrderUpdateMessage;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;

import java.util.List;

public class WaiterDashboardController {

    @FXML private ComboBox<OrderStatus> statusComboBox;
    @FXML private TableView<Order> ordersTableView;
    @FXML private TableColumn<Order, String> orderIdColumn;
    @FXML private TableColumn<Order, Number> tableColumn;
    @FXML private TableColumn<Order, Number> seatsColumn;
    @FXML private TableColumn<Order, String> timeColumn;
    @FXML private TableColumn<Order, String> itemsColumn;
    @FXML private TableColumn<Order, Number> totalColumn;
    @FXML private TableColumn<Order, String> statusColumn;
    @FXML private Label statusLabel;

    private final OrderController orderController = new OrderController();
    private final AuthController authController = new AuthController();
    private final OrderSocketClient socketClient = new OrderSocketClient();
    private final ObservableList<Order> orderList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        statusComboBox.setItems(FXCollections.observableArrayList(OrderStatus.values()));

        orderIdColumn.setCellValueFactory(data -> new SimpleStringProperty(data.getValue().getId()));
        tableColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getTableNumber()));
        seatsColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().getPartySize()));
        timeColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getScheduledFor() == null ? "Not scheduled" : data.getValue().getScheduledFor().toString()));
        itemsColumn.setCellValueFactory(data -> new SimpleStringProperty(orderItemsSummary(data.getValue())));
        totalColumn.setCellValueFactory(data -> new SimpleDoubleProperty(data.getValue().calculateTotal()));
        statusColumn.setCellValueFactory(data -> new SimpleStringProperty(
                data.getValue().getStatus() != null ? data.getValue().getStatus().toString() : "PENDING"
        ));

        // Start listening for real-time background socket updates
        socketClient.startListening(message -> {
            if (OrderUpdateMessage.apply(message) || "REFRESH_ORDERS".equals(message)) {
                loadOrders();
            }
        });

        loadOrders();
    }

    private void loadOrders() {
        try {
            List<Order> orders = orderController.getAllOrders();
            orderList.setAll(orders);
            ordersTableView.setItems(orderList);
        } catch (Exception e) {
            orderList.clear();
        }
    }

    @FXML
    private void handleUpdateStatus() {
        Order selected = ordersTableView.getSelectionModel().getSelectedItem();
        OrderStatus newStatus = statusComboBox.getValue();

        if (selected == null) {
            showError("Please select an order from the table.");
            return;
        }
        if (newStatus == null) {
            showError("Please select a new status from the dropdown.");
            return;
        }

        try {
            orderController.updateOrderStatus(selected.getId(), newStatus);
            loadOrders();

            // Broadcast real-time order update to Admin and Customer screens
            socketClient.sendMessage(OrderUpdateMessage.statusChanged(selected.getId(), newStatus));

            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            statusLabel.setText("Order #" + selected.getId() + " status updated to " + newStatus);
            statusLabel.setVisible(true);
        } catch (Exception e) {
            showError("Failed to update status: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusLabel.setText(message);
        statusLabel.setVisible(true);
    }

    private String orderItemsSummary(Order order) {
        return order.getItems().stream()
                .map(item -> item.getQuantity() + " × " + item.getMenuItem().getName())
                .reduce((first, second) -> first + ", " + second)
                .orElse("No items");
    }

    @FXML
    private void handleLogout() {
        authController.logout();
        ViewNavigator.loadView("/com/restaurant/view/login.fxml", "Login");
    }
}
