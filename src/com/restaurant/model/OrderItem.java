package com.restaurant.model;

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
        return menuItem != null ? menuItem.getPrice() * quantity : 0.0;
    }
}