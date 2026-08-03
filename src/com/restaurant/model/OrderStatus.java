package com.restaurant.model;

/**
 * Represents the stage of an order in the restaurant workflow.
 */
public enum OrderStatus {
    /** The order has been received and awaits preparation. */
    PENDING,
    /** The kitchen is preparing the order. */
    PREPARING,
    /** The order is ready to be delivered. */
    READY,
    /** The order has been delivered to the customer. */
    SERVED,
    /** The order was cancelled before completion. */
    CANCELLED
}
