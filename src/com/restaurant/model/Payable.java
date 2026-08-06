package com.restaurant.model;

/**
 * Contract for domain entities that represent a financial transaction or billable item.
 */
public interface Payable {
    /**
     * Calculates the total amount payable.
     *
     * @return total cost in double
     */
    double calculateTotal();

    /**
     * Processes or records a payment.
     *
     * @param amount the amount paid
     * @return true if payment was successful, false otherwise
     */
    boolean processPayment(double amount);
}