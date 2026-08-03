package com.restaurant.model;

/**
 * Represents the current availability and condition of a restaurant table.
 */
public enum TableStatus {
    /** The table is ready for customers. */
    AVAILABLE,
    /** The table has been reserved for a customer. */
    RESERVED,
    /** The table is currently in use. */
    OCCUPIED,
    /** The table needs cleaning before it can be used. */
    DIRTY
}
