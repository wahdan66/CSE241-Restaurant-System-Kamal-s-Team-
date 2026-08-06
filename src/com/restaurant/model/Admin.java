package com.restaurant.model;

/** Represents a system administrator, which always receives the {@link Role#ADMIN} role. */
public class Admin extends User implements Manageable {
    private boolean active = true;

    public Admin() {
        super();
        // Assign the fixed role required for this specialized user type.
        setRole(Role.ADMIN);
    }

    public Admin(String id, String name, String username, String passwordHash) {
        super(id, name, username, passwordHash, Role.ADMIN);
    }

    @Override
    public boolean isActive() {
        return active;
    }

    @Override
    public void setActive(boolean active) {
        this.active = active;
    }
}
