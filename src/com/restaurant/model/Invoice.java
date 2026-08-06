package com.restaurant.model;

import java.time.LocalDateTime;

/** Represents the bill for an order, including tax, issue time, and payment state. */
public class Invoice implements Payable {
    private String invoiceId;
    private Order order;
    private double taxRate;
    private LocalDateTime issueDate;
    private boolean paid = false;

    public Invoice() {
        this.issueDate = LocalDateTime.now();
    }

    public Invoice(String invoiceId, Order order, double taxRate) {
        this.invoiceId = invoiceId;
        this.order = order;
        this.taxRate = taxRate;
        this.issueDate = LocalDateTime.now();
    }

    public String getInvoiceId() {
        return invoiceId;
    }

    public void setInvoiceId(String invoiceId) {
        this.invoiceId = invoiceId;
    }

    public Order getOrder() {
        return order;
    }

    public void setOrder(Order order) {
        this.order = order;
    }

    public double getTaxRate() {
        return taxRate;
    }

    public void setTaxRate(double taxRate) {
        this.taxRate = taxRate;
    }

    public LocalDateTime getIssueDate() {
        return issueDate;
    }

    public boolean isPaid() {
        return paid;
    }

    @Override
    public double calculateTotal() {
        // An invoice without an order cannot have a billable amount.
        if (order == null) return 0.0;
        double subtotal = order.calculateTotal();
        // Tax is a decimal rate: for example, 0.08 represents 8 percent.
        return subtotal + (subtotal * taxRate);
    }

    @Override
    public boolean processPayment(double amount) {
        // Mark the invoice as paid only when the supplied amount covers its total.
        if (amount >= calculateTotal()) {
            this.paid = true;
            return true;
        }
        return false;
    }
}
