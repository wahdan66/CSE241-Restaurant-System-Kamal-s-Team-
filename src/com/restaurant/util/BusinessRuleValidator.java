package com.restaurant.util;

import com.restaurant.exception.BusinessRuleException;
import com.restaurant.model.Order;
import com.restaurant.model.OrderStatus;
import com.restaurant.model.Table;
import com.restaurant.model.TableStatus;

/** Enforces restaurant workflow rules before operations are performed. */
public class BusinessRuleValidator {

    public static void validateTableAssignment(Table table, int partySize) {
        // Validate in dependency order: table existence, availability, then capacity.
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
        // Keep the current value so all transition rules evaluate the same state.
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
        // Reuse the order's total calculation so payment rules match invoice pricing.
        double total = order.calculateTotal();
        if (amountPaid < total) {
            throw new BusinessRuleException(String.format("Insufficient payment. Required: $%.2f, Provided: $%.2f", total, amountPaid));
        }
    }
}
