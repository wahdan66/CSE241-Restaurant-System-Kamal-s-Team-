package com.restaurant.db;

import com.restaurant.model.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * In-memory data store using static ArrayLists.
 * Serves as the central repository populated with seed data for system testing.
 */
public class RestaurantDatabase {
    //Default Constructor used
    // Static in-memory storage lists
    public static final List<User> users = new CopyOnWriteArrayList<>();
    public static final List<Table> tables = new CopyOnWriteArrayList<>();
    public static final List<MenuItem> menuItems = new CopyOnWriteArrayList<>();
    public static final List<Order> orders = new CopyOnWriteArrayList<>();
    public static final List<Reservation> reservations = new CopyOnWriteArrayList<>();
    public static final List<Invoice> invoices = new CopyOnWriteArrayList<>();

    // Static initializer block runs automatically on application startup
    static {
        seedDatabase();
    }

    /**
     * Pre-populates mock datasets for instant testing.
     */
    public static void seedDatabase() {
        // Clear lists to prevent duplicate seeding if re-initialized
        users.clear();
        tables.clear();
        menuItems.clear();
        orders.clear();
        reservations.clear();
        invoices.clear();

        // 1. Seed Users (Admin, Waiter, Customer)
        users.add(new Admin("U001", "System Admin", "admin", "admin123"));
        users.add(new Waiter("U002", "John Waiter", "waiter1", "pass123"));
        users.add(new Customer("U003", "Alice Smith", "alice", "pass123", "555-0199"));

        // 2. Seed Tables
        tables.add(new Table(1, 2));
        tables.add(new Table(2, 4));
        tables.add(new Table(3, 4));
        tables.add(new Table(4, 6));

        // 3. Seed Menu Items
        MenuItem burger = new MenuItem("M001", "Cheeseburger", "Juicy beef patty with cheese", 12.99, MenuCategory.MAIN_COURSE);
        MenuItem pasta = new MenuItem("M002", "Pasta Carbonara", "Creamy pasta with bacon", 14.50, MenuCategory.MAIN_COURSE);
        MenuItem soda = new MenuItem("M003", "Fountain Soda", "Chilled soft drink", 2.99, MenuCategory.BEVERAGE);
        MenuItem cake = new MenuItem("M004", "Chocolate Lava Cake", "Warm chocolate cake with ice cream", 6.99, MenuCategory.DESSERT);

        menuItems.add(burger);
        menuItems.add(pasta);
        menuItems.add(soda);
        menuItems.add(cake);

        // 4. Seed Reservations
        reservations.add(new Reservation("R001", "Alice Smith", "555-0199", 2, 2,
                LocalDateTime.now().plusDays(1)));
    }
}
