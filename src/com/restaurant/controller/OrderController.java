package com.restaurant.controller;

import com.restaurant.db.OrderDAO;
import com.restaurant.exception.BusinessRuleException;
import com.restaurant.model.*;
import com.restaurant.util.BusinessRuleValidator;

import java.util.ArrayList;
import java.util.List;

public class OrderController {
    private final OrderDAO orderDAO;

    public OrderController() {
        this.orderDAO = new OrderDAO();
    }

    public OrderController(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
    }

    public Order createOrder(String orderId, int tableNumber) {
        Order newOrder = new Order(orderId, tableNumber);
        orderDAO.add(newOrder);
        return newOrder;
    }

    public void addItemToOrder(String orderId, MenuItem item, int quantity) {
        if (quantity <= 0) {
            throw new BusinessRuleException("Quantity must be greater than zero.");
        }

        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new BusinessRuleException("Order not found: " + orderId));

        order.addItem(new OrderItem(item, quantity));
        orderDAO.update(order);
    }

    public void updateOrderStatus(String orderId, OrderStatus newStatus) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new BusinessRuleException("Order not found: " + orderId));

        BusinessRuleValidator.validateOrderStateTransition(order, newStatus);
        order.setStatus(newStatus);
        orderDAO.update(order);
    }

    public boolean checkout(String orderId, double paymentAmount) {
        Order order = orderDAO.findById(orderId)
                .orElseThrow(() -> new BusinessRuleException("Order not found: " + orderId));

        BusinessRuleValidator.validatePaymentProcessing(order, paymentAmount);
        boolean success = order.processPayment(paymentAmount);
        if (success) {
            orderDAO.update(order);
        }
        return success;
    }

    public List<Order> getAllOrders() {
        return orderDAO.findAll();
    }

    public <E> void createOrder(ArrayList<E> es) {
    }
}