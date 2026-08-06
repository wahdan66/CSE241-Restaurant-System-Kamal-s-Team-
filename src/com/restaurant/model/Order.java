package com.restaurant.model;

import java.util.ArrayList;
import java.util.List;

public class Order implements Payable {
    private String id;
    private int tableNumber;
    private OrderStatus status;
    private List<OrderItem> items = new ArrayList<>();

    public Order() {
        this.status = OrderStatus.PENDING;
    }

    public Order(String id, int tableNumber) {
        this.id = id;
        this.tableNumber = tableNumber;
        this.status = OrderStatus.PENDING;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public OrderStatus getStatus() {
        return status;
    }

    public void setStatus(OrderStatus status) {
        this.status = status;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public void addItem(OrderItem item) {
        this.items.add(item);
    }

    @Override
    public double calculateTotal() {
        double total = 0.0;
        for (OrderItem item : items) {
            total += item.getSubtotal();
        }
        return total;
    }

    @Override
    public boolean processPayment(double amount) {
        if (amount >= calculateTotal()) {
            this.status = OrderStatus.SERVED;
            return true;
        }
        return false;
    }
}