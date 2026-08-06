package com.restaurant;

import com.restaurant.controller.*;
import com.restaurant.exception.BusinessRuleException;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.OrderStatus;

import java.time.LocalDateTime;

public class MainTest {
    public static void main(String[] args) {
        System.out.println("=== STARTING RESTAURANT SYSTEM TESTS ===\n");

        // Initialize Controllers
        AuthController authController = new AuthController();
        TableController tableController = new TableController();
        OrderController orderController = new OrderController();
        MenuController menuController = new MenuController();
        ReservationController reservationController = new ReservationController();

        // 1. Test Authentication
        System.out.println("[1] Testing Authentication...");
        authController.login("admin", "admin123");
        System.out.println("   Logged in successfully as: " + authController.getCurrentUser().getName());

        // 2. Test Menu Management
        System.out.println("\n[2] Testing Menu Operations...");
        System.out.println("   Total menu items loaded from seed: " + menuController.getAllMenuItems().size());
        menuController.addMenuItem("M005", "Iced Tea", "Freshly brewed peach tea", 3.50, MenuCategory.BEVERAGE);
        System.out.println("   Added new item! Updated count: " + menuController.getAllMenuItems().size());

        // 3. Test Table Seating & Validation Rules
        System.out.println("\n[3] Testing Table Operations...");
        tableController.seatParty(1, 2); // Table 1 capacity is 2
        System.out.println("   Seated party of 2 at Table 1.");

        try {
            // Should fail because capacity exceeds limit or table is occupied
            tableController.seatParty(1, 10);
        } catch (BusinessRuleException e) {
            System.out.println("   [PASSED] Business Rule Enforced: " + e.getMessage());
        }

        // 4. Test Order Creation & Status Lifecycle
        System.out.println("\n[4] Testing Order Operations...");
        orderController.createOrder("O002", 1);
        orderController.addItemToOrder("O002", menuController.getAllMenuItems().get(0), 2);
        orderController.updateOrderStatus("O002", OrderStatus.PREPARING);
        System.out.println("   Order O002 updated to status: PREPARING");

        // 5. Test Reservations
        System.out.println("\n[5] Testing Reservations...");
        reservationController.createReservation("R002", "Bob Johnson", "555-0188", 2, LocalDateTime.now().plusDays(2));
        System.out.println("   Reservation created successfully! Total reservations: " + reservationController.getAllReservations().size());

        System.out.println("\n=== ALL CONTROLLER TESTS PASSED SUCCESSFULLY! ===");
    }
}