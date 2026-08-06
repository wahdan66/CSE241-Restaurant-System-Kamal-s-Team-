package com.restaurant.util;

import com.restaurant.exception.BusinessRuleException;
import com.restaurant.model.Order;
import com.restaurant.model.OrderStatus;
import com.restaurant.model.Table;
import com.restaurant.model.TableStatus;

public class BusinessRuleValidator {

    public static void validateTableAssignment(Table table, int partySize) {
        if (table == null) {
            throw new BusinessRuleException("Selected table does not exist.");
        }
        if (!table.isActive()) {
            throw new BusinessRuleException("Table " + table.getTableNumber() + " is inactive.");
        }
        if (table.getStatus() != TableStatus.AVAILABLE) {
            throw new BusinessRuleException("Table " + table.getTableNumber() + " is currently unavailable.");
        }
        if (partySize > table.getCapacity()) {
            throw new BusinessRuleException("Party size (" + partySize + ") exceeds table capacity (" + table.getCapacity() + ").");
        }
    }

    public static void validateOrderStateTransition(Order order, OrderStatus newStatus) {
        if (order == null) {
            throw new BusinessRuleException("Order cannot be null.");
        }
        OrderStatus currentStatus = order.getStatus();

        if (currentStatus == OrderStatus.CANCELLED || currentStatus == OrderStatus.SERVED) {
            throw new BusinessRuleException("Completed or cancelled orders cannot change status.");
        }

        if (currentStatus == OrderStatus.PENDING && newStatus == OrderStatus.SERVED) {
            throw new BusinessRuleException("Order must be PREPARING before it can be SERVED.");
        }
    }

    public static void validatePaymentProcessing(Order order, double amountPaid) {
        if (order == null) {
            throw new BusinessRuleException("No order provided for payment.");
        }
        if (order.getItems().isEmpty()) {
            throw new BusinessRuleException("Cannot bill an empty order.");
        }
        double total = order.calculateTotal();
        if (amountPaid < total) {
            throw new BusinessRuleException(String.format("Insufficient payment. Required: $%.2f, Provided: $%.2f", total, amountPaid));
        }
    }
}