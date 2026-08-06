package com.restaurant.model;

public class Waiter extends User implements Manageable {
    private int assignedTableCount;
    private boolean active = true;

    public Waiter() {
        super();
        setRole(Role.WAITER);
    }

    public Waiter(String id, String name, String username, String passwordHash) {
        super(id, name, username, passwordHash, Role.WAITER);
    }

    public int getAssignedTableCount() {
        return assignedTableCount;
    }

    public void setAssignedTableCount(int assignedTableCount) {
        this.assignedTableCount = assignedTableCount;
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