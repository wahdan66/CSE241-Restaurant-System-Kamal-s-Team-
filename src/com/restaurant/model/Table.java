package com.restaurant.model;

/**
 * Represents a physical seating table in the restaurant.
 */
public class Table {
    private int tableNumber;
    private int capacity;
    private TableStatus status;

    /**
     * Creates an empty table. Fields can be supplied later through setters.
     */
    public Table() {
    }

    /**
     * Creates a table with its identifying number, seating capacity, and status.
     *
     * @param tableNumber unique number assigned to the table
     * @param capacity maximum number of guests the table can seat
     * @param status current availability and condition of the table
     */
    public Table(int tableNumber, int capacity, TableStatus status) {
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        this.status = status;
    }

    /** @return the unique number assigned to this table */
    public int getTableNumber() {
        return tableNumber;
    }

    /** @param tableNumber the unique number assigned to this table */
    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    /** @return the maximum number of guests this table can seat */
    public int getCapacity() {
        return capacity;
    }

    /** @param capacity the maximum number of guests this table can seat */
    public void setCapacity(int capacity) {
        this.capacity = capacity;
    }

    /** @return the table's current availability and condition */
    public TableStatus getStatus() {
        return status;
    }

    /** @param status the table's current availability and condition */
    public void setStatus(TableStatus status) {
        this.status = status;
    }
}
