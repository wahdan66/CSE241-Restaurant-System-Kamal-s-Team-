package com.restaurant.controller;

import com.restaurant.db.TableDAO;
import com.restaurant.exception.BusinessRuleException;
import com.restaurant.model.Table;
import com.restaurant.model.TableStatus;
import com.restaurant.util.BusinessRuleValidator;

import java.util.List;

public class TableController {
    private final TableDAO tableDAO;

    public TableController() {
        this.tableDAO = new TableDAO();
    }

    public TableController(TableDAO tableDAO) {
        this.tableDAO = tableDAO;
    }

    public List<Table> getAllTables() {
        return tableDAO.findAll();
    }

    public void seatParty(int tableNumber, int partySize) {
        Table table = tableDAO.findById(tableNumber)
                .orElseThrow(() -> new BusinessRuleException("Table not found: " + tableNumber));

        BusinessRuleValidator.validateTableAssignment(table, partySize);
        table.setStatus(TableStatus.OCCUPIED);
        tableDAO.update(table);
    }

    public void releaseTable(int tableNumber) {
        Table table = tableDAO.findById(tableNumber)
                .orElseThrow(() -> new BusinessRuleException("Table not found: " + tableNumber));

        table.setStatus(TableStatus.AVAILABLE);
        tableDAO.update(table);
    }
}