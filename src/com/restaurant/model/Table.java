package com.restaurant.model;

/**
 * Represents a physical restaurant table and its current service state.
 *
 * <p>A table is active when it is available for the restaurant to manage.
 * Its {@link TableStatus} separately describes whether it is currently free,
 * reserved, occupied, or awaiting cleaning.</p>
 */
public class Table implements Manageable {
    /** Identifies this table on the restaurant floor plan. */
    private int tableNumber;
    /** Defines the maximum number of guests that can sit at this table. */
    private int capacity;
    /** Tracks the table's current seating or cleaning state. */
    private TableStatus status;
    /** Determines whether this table is enabled for use in the system. */
    private boolean active = true;

    /**
     * Creates a table with the default {@link TableStatus#AVAILABLE} status.
     */
    public Table() {
        // New tables start ready to receive customers.
        this.status = TableStatus.AVAILABLE;
    }

    /**
     * Creates an active, available table with its number and seating capacity.
     *
     * @param tableNumber identifier shown for the table
     * @param capacity maximum number of guests the table can seat
     */
    public Table(int tableNumber, int capacity) {
        // Save the physical table details provided during creation.
        this.tableNumber = tableNumber;
        this.capacity = capacity;
        // A newly created table is initially ready for service.
        this.status = TableStatus.AVAILABLE;
    }

    /** @return the identifier assigned to this table */
    public int getTableNumber() {
        return tableNumber;
    }

    /** @param tableNumber the identifier to assign to this table */
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

    /** @return the table's current seating or cleaning status */
    public TableStatus getStatus() {
        return status;
    }

    /** @param status the new seating or cleaning status for this table */
    public void setStatus(TableStatus status) {
        this.status = status;
    }

    /**
     * Checks whether this table is enabled for normal system operations.
     *
     * @return {@code true} when the table is active
     */
    @Override
    public boolean isActive() {
        return active;
    }

    /**
     * Enables or disables this table without changing its seating status.
     *
     * @param active {@code true} to enable the table; {@code false} to disable it
     */
    @Override
    public void setActive(boolean active) {
        this.active = active;
    }

    @Override
    public String toString() {
        // Adjust getter names to match your Table model
        return String.format("Table #%d (Capacity: %d seats)", getTableNumber(), getCapacity());
    }
}
