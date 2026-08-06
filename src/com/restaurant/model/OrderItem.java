package com.restaurant.model;

/** Couples one menu item with the quantity selected for an order. */
public class OrderItem {
    private MenuItem menuItem;
    private int quantity;

    public OrderItem() {
    }

    public OrderItem(MenuItem menuItem, int quantity) {
        this.menuItem = menuItem;
        this.quantity = quantity;
    }

    public MenuItem getMenuItem() {
        return menuItem;
    }

    public void setMenuItem(MenuItem menuItem) {
        this.menuItem = menuItem;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getSubtotal() {
        // An unassigned menu item has no price, avoiding a null-pointer exception.
        return menuItem != null ? menuItem.getPrice() * quantity : 0.0;
    }
}
