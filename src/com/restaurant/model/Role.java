package com.restaurant.model;

/**
 * Defines the roles available to users of the restaurant system.
 */
public enum Role {
    /** System administrator with full access. */
    ADMIN,
    /** Restaurant manager responsible for operations. */
    MANAGER,
    /** Staff member who serves customers. */
    WAITER,
    /** Staff member who prepares food. */
    CHEF,
    /** Guest or registered restaurant customer. */
    CUSTOMER
}
