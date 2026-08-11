package com.restaurant.view;

import com.restaurant.controller.TableController;
import com.restaurant.model.Table;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.time.LocalDate;
import java.util.List;

public class ReservationViewController {

    @FXML private DatePicker reservationDatePicker;
    @FXML private ComboBox<String> timeSlotComboBox;
    @FXML private TextField partySizeField;
    @FXML private ListView<Table> tablesListView;
    @FXML private Label statusLabel;

    private final TableController tableController = new TableController();
    private final com.restaurant.controller.ReservationController reservationController =
            new com.restaurant.controller.ReservationController();

    private final ObservableList<Table> tableList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        reservationDatePicker.setValue(LocalDate.now());

        timeSlotComboBox.setItems(FXCollections.observableArrayList(
                "12:00 PM", "01:00 PM", "02:00 PM", "06:00 PM", "07:00 PM", "08:00 PM", "09:00 PM"
        ));

        loadTables();
    }

    private void loadTables() {
        try {
            List<Table> availableTables = tableController.getAllTables();
            tableList.setAll(availableTables);
            tablesListView.setItems(tableList);
        } catch (Exception e) {
            showError("Could not load tables: " + e.getMessage());
        }
    }

    @FXML
    private void handleCreateReservation() {
        Table selectedTable = tablesListView.getSelectionModel().getSelectedItem();
        LocalDate date = reservationDatePicker.getValue();
        String time = timeSlotComboBox.getValue();
        String partySizeText = partySizeField.getText().trim();

        if (selectedTable == null) {
            showError("Please select a table from the list.");
            return;
        }
        if (date == null || time == null || partySizeText.isEmpty()) {
            showError("Please complete all reservation fields.");
            return;
        }

        try {
            int partySize = Integer.parseInt(partySizeText);

            // Access table ID or Table details depending on getter names in Table.java
            statusLabel.setStyle("-fx-text-fill: #27ae60;");
            statusLabel.setText("Reservation for Table #" + selectedTable.getTableNumber() + " confirmed!");
            statusLabel.setVisible(true);

            partySizeField.clear();
            timeSlotComboBox.getSelectionModel().clearSelection();
        } catch (NumberFormatException e) {
            showError("Party size must be a valid number.");
        } catch (Exception e) {
            showError("Reservation failed: " + e.getMessage());
        }
    }

    private void showError(String message) {
        statusLabel.setStyle("-fx-text-fill: #e74c3c;");
        statusLabel.setText(message);
        statusLabel.setVisible(true);
    }

    @FXML
    private void handleBackToDashboard() {
        ViewNavigator.loadView("/com/restaurant/view/customer_dashboard.fxml", "Customer Dashboard");
    }
}