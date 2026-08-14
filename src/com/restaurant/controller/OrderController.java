package com.restaurant.controller;

import com.restaurant.db.OrderDAO;
import com.restaurant.db.RestaurantDatabase;
import com.restaurant.db.TableDAO;
import com.restaurant.exception.BusinessRuleException;
import com.restaurant.model.*;
import com.restaurant.util.BusinessRuleValidator;

import java.time.LocalDateTime;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class OrderController {
    private final OrderDAO orderDAO;
    private final TableDAO tableDAO;

    public OrderController() {
        this.orderDAO = new OrderDAO();
        this.tableDAO = new TableDAO();
    }

    public OrderController(OrderDAO orderDAO) {
        this.orderDAO = orderDAO;
        this.tableDAO = new TableDAO();
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

    public List<Order> getOrdersForCustomer(String customerId) {
        return orderDAO.findAll().stream()
                .filter(order -> customerId != null && customerId.equals(order.getCustomerId()))
                .toList();
    }

    public Order createOrder(List<MenuItem> menuItems) {
        throw new BusinessRuleException("Choose a table, seat count, date, and time before placing an order.");
    }

    public Order createOrder(List<MenuItem> menuItems, int tableNumber, String customerId) {
        return createOrder(menuItems, tableNumber, customerId, 1, LocalDateTime.now());
    }

    public Order createOrder(List<MenuItem> menuItems, int tableNumber, String customerId, int partySize,
                             LocalDateTime scheduledFor) {
        if (menuItems == null || menuItems.isEmpty()) {
            throw new BusinessRuleException("Cannot create an order without menu items.");
        }
        if (tableNumber <= 0) {
            throw new BusinessRuleException("Please choose a table before placing an order.");
        }
        if (customerId == null || customerId.isBlank()) {
            throw new BusinessRuleException("You must be logged in to place an order.");
        }
        if (scheduledFor == null || scheduledFor.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Please choose a future order date and time.");
        }

        Table table = tableDAO.findById(tableNumber)
                .orElseThrow(() -> new BusinessRuleException("Selected table does not exist."));
        if (!table.isActive() || table.getStatus() != TableStatus.AVAILABLE) {
            throw new BusinessRuleException("Selected table is not available.");
        }
        if (partySize <= 0 || partySize > table.getCapacity()) {
            throw new BusinessRuleException("Party size must fit the selected table.");
        }
        boolean timeAlreadyBooked = orderDAO.findAll().stream()
                .anyMatch(existing -> existing.getTableNumber() == tableNumber
                        && scheduledFor.equals(existing.getScheduledFor())
                        && existing.getStatus() != OrderStatus.CANCELLED);
        boolean timeAlreadyReserved = RestaurantDatabase.reservations.stream()
                .anyMatch(reservation -> reservation.getTableNumber() == tableNumber
                        && scheduledFor.equals(reservation.getReservationTime()));
        if (timeAlreadyBooked || timeAlreadyReserved) {
            throw new BusinessRuleException("Table " + tableNumber + " is already booked for the selected date and time.");
        }

        int nextOrderNumber = orderDAO.findAll().stream()
                .map(Order::getId)
                .filter(id -> id != null && id.matches("ORD-\\d+"))
                .mapToInt(id -> Integer.parseInt(id.substring(4)))
                .max()
                .orElse(0) + 1;
        Order order = new Order(String.format("ORD-%03d", nextOrderNumber), tableNumber);
        order.setCustomerId(customerId);
        order.setPartySize(partySize);
        order.setScheduledFor(scheduledFor);
        Map<String, Integer> quantitiesByItemId = new LinkedHashMap<>();
        Map<String, MenuItem> itemsById = new LinkedHashMap<>();

        for (MenuItem item : menuItems) {
            if (item == null || item.getId() == null) {
                throw new BusinessRuleException("Every ordered item must be valid.");
            }
            quantitiesByItemId.merge(item.getId(), 1, Integer::sum);
            itemsById.putIfAbsent(item.getId(), item);
        }

        for (Map.Entry<String, Integer> entry : quantitiesByItemId.entrySet()) {
            order.addItem(new OrderItem(itemsById.get(entry.getKey()), entry.getValue()));
        }

        orderDAO.add(order);
        return order;
    }
}
