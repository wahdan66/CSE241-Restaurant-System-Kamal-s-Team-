package com.restaurant.view;

import com.restaurant.controller.AuthController;
import com.restaurant.controller.MenuController;
import com.restaurant.controller.OrderController;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.Table;
import com.restaurant.model.TableStatus;
import com.restaurant.controller.TableController;
import com.restaurant.network.OrderSocketClient;
import com.restaurant.network.OrderUpdateMessage;
import com.restaurant.network.MenuUpdateMessage;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;

import java.util.List;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.HashMap;
import java.util.Map;

public class CustomerDashboardController {

    @FXML private TextField searchField;
    @FXML private ComboBox<String> categoryFilter;
    @FXML private ComboBox<Table> tableComboBox;
    @FXML private TextField partySizeField;
    @FXML private DatePicker orderDatePicker;
    @FXML private ComboBox<String> orderTimeComboBox;
    @FXML private ListView<MenuItem> menuListView;
    @FXML private Label statusLabel;
    @FXML private Label orderStatusLabel;

    private final MenuController menuController = new MenuController();
    private final OrderController orderController = new OrderController();
    private final TableController tableController = new TableController();
    private final AuthController authController = new AuthController();
    private final OrderSocketClient socketClient = new OrderSocketClient();
    private final ObservableList<MenuItem> observableMenuList = FXCollections.observableArrayList();
    private final Map<String, Integer> itemQuantities = new HashMap<>();

    @FXML
    public void initialize() {
        loadMenuItems();

        // Populate Category Dropdown
        categoryFilter.setItems(FXCollections.observableArrayList(
                "All Categories", "Main Course", "Appetizer", "Beverage", "Dessert", "Side"
        ));
        categoryFilter.getSelectionModel().selectFirst();
        orderDatePicker.setValue(LocalDate.now().plusDays(1));
        orderTimeComboBox.setItems(FXCollections.observableArrayList(
                "12:00 PM", "01:00 PM", "02:00 PM", "06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM"
        ));
        loadAvailableTables();
        configureMenuCells();

        // Listen for search input and dropdown changes
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterMenu());
        categoryFilter.setOnAction(event -> filterMenu());

        socketClient.startListening(message -> {
            if (OrderUpdateMessage.apply(message)) {
                loadLatestOrderStatus();
            } else if (MenuUpdateMessage.apply(message)) {
                loadMenuItems();
            }
        }, () -> socketClient.sendMessage("REQUEST_MENU"));
        loadLatestOrderStatus();
    }

    private void filterMenu() {
        String query = searchField.getText().toLowerCase().trim();
        String selectedCategory = categoryFilter.getSelectionModel().getSelectedItem();

        ObservableList<MenuItem> filtered = observableMenuList.filtered(item -> {
            boolean matchesSearch = query.isEmpty() || item.getName().toLowerCase().contains(query);
            boolean matchesCategory = selectedCategory == null
                    || selectedCategory.equals("All Categories")
                    || (item.getCategory() != null
                    && item.getCategory().name().equalsIgnoreCase(selectedCategory.replace(' ', '_')));

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

    private void configureMenuCells() {
        menuListView.setCellFactory(listView -> new ListCell<>() {
            @Override
            protected void updateItem(MenuItem item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setGraphic(null);
                    return;
                }

                Label itemLabel = new Label(String.format("%s — $%.2f", item.getName(), item.getPrice()));
                HBox.setHgrow(itemLabel, Priority.ALWAYS);
                Button decreaseButton = new Button("−");
                Label quantityLabel = new Label(String.valueOf(itemQuantities.getOrDefault(item.getId(), 0)));
                Button increaseButton = new Button("+");
                decreaseButton.setOnAction(event -> changeQuantity(item, -1));
                increaseButton.setOnAction(event -> changeQuantity(item, 1));
                setGraphic(new HBox(10, itemLabel, decreaseButton, quantityLabel, increaseButton));
            }
        });
    }

    private void changeQuantity(MenuItem item, int change) {
        int updatedQuantity = Math.max(0, itemQuantities.getOrDefault(item.getId(), 0) + change);
        itemQuantities.put(item.getId(), updatedQuantity);
        menuListView.refresh();
    }

    private void loadAvailableTables() {
        List<Table> tables = tableController.getAllTables().stream()
                .filter(table -> table.isActive() && table.getStatus() == TableStatus.AVAILABLE)
                .toList();
        tableComboBox.setItems(FXCollections.observableArrayList(tables));
    }

    private boolean saveOrderDetails() {
        Table table = tableComboBox.getValue();
        LocalDate date = orderDatePicker.getValue();
        String time = orderTimeComboBox.getValue();
        if (table == null || date == null || time == null || partySizeField.getText().trim().isEmpty()) {
            showError("Choose a table, number of seats, date, and time before adding food.");
            return false;
        }
        try {
            int partySize = Integer.parseInt(partySizeField.getText().trim());
            LocalDateTime scheduledFor = LocalDateTime.of(date,
                    LocalTime.parse(time, DateTimeFormatter.ofPattern("hh:mm a", Locale.US)));
            if (partySize <= 0 || partySize > table.getCapacity()) {
                showError("Table #" + table.getTableNumber() + " seats up to " + table.getCapacity() + " guests.");
                return false;
            }
            if (scheduledFor.isBefore(LocalDateTime.now())) {
                showError("Choose a future date and time for your order.");
                return false;
            }
            CartController.setOrderDetails(table.getTableNumber(), partySize, scheduledFor);
            return true;
        } catch (NumberFormatException e) {
            showError("Number of seats must be a whole number.");
            return false;
        }
    }

    private void loadLatestOrderStatus() {
        if (authController.getCurrentUser() == null) {
            orderStatusLabel.setText("No active order.");
            return;
        }
        List<Order> orders = orderController.getOrdersForCustomer(authController.getCurrentUser().getId());
        if (orders.isEmpty()) {
            orderStatusLabel.setText("No active order.");
            return;
        }
        Order latestOrder = orders.get(orders.size() - 1);
        orderStatusLabel.setText("Your latest order for Table #" + latestOrder.getTableNumber()
                + " is " + latestOrder.getStatus() + ".");
    }

    @FXML
    private void handleRefresh() {
        loadMenuItems();
        loadLatestOrderStatus();
        statusLabel.setStyle("-fx-text-fill: #27ae60;");
        statusLabel.setText("Menu and order status refreshed.");
        statusLabel.setVisible(true);
    }

    @FXML
    private void handleLogout() {
        CartController.clearCart();
        authController.logout();
        ViewNavigator.loadView("/com/restaurant/view/login.fxml", "Login");
    }
    @FXML
    private void handleAddToCart() {
        if (!saveOrderDetails()) {
            return;
        }
        int totalItemsAdded = 0;
        for (MenuItem item : observableMenuList) {
            int quantity = itemQuantities.getOrDefault(item.getId(), 0);
            for (int count = 0; count < quantity; count++) {
                CartController.getCartItems().add(item);
                totalItemsAdded++;
            }
        }
        if (totalItemsAdded == 0) {
            showError("Use the + button to choose at least one menu item.");
            return;
        }

        itemQuantities.clear();
        menuListView.refresh();

        statusLabel.setStyle("-fx-text-fill: #229954;");
        statusLabel.setText(totalItemsAdded + " item(s) added to cart. Quantities reset to zero.");
        statusLabel.setVisible(true);
    }

    @FXML
    private void handleViewCart() {
        if (!CartController.getCartItems().isEmpty() && !saveOrderDetails()) {
            return;
        }
        ViewNavigator.loadView("/com/restaurant/view/cart.fxml", "Your Cart");
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusLabel.setText(message);
        statusLabel.setVisible(true);
    }

}
