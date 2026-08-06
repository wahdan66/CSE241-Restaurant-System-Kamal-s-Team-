package com.restaurant.model;

public class Admin extends User implements Manageable {
    private boolean active = true;

    public Admin() {
        super();
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