package com.restaurant.model;

/**
 * Contract for entities that can be managed (activated, deactivated, or checked for operational status).
 */
public interface Manageable {
    /** @return true if the entity is active and usable in the system */
    boolean isActive();

    /** Sets the active operational state of the entity. */
    void setActive(boolean active);
}