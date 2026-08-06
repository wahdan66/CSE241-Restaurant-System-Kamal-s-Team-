package com.restaurant.controller;

import com.restaurant.db.MenuItemDAO;
import com.restaurant.exception.*;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import com.restaurant.util.InputValidator;

import java.util.List;

/**
 * Controller responsible for managing restaurant menu operations.
 * Acts as the bridge between UI views and the MenuItemDAO data layer.
 */
public class MenuController {
    // Data access object for querying and updating menu items in storage
    private final MenuItemDAO menuItemDAO;

    /**
     * Default constructor initializing the controller with a standard MenuItemDAO.
     */
    public MenuController() {
        this.menuItemDAO = new MenuItemDAO();
    }

    /**
     * Overloaded constructor allowing dependency injection for testing.
     *
     * @param menuItemDAO The DAO instance to use for data operations
     */
    public MenuController(MenuItemDAO menuItemDAO) {
        this.menuItemDAO = menuItemDAO;
    }

    /**
     * Adds a new menu item to the catalog after passing validation checks.
     *
     * @param id          Unique identifier for the menu item
     * @param name        Display name of the dish or drink
     * @param description Detailed description of ingredients or preparation
     * @param price       Cost of the item (must be > 0)
     * @param category    Classification (e.g., MAIN_COURSE, BEVERAGE)
     * @throws BusinessRuleException If an item with the same ID already exists
     * @throws ValidationException   If inputs fail basic format/null checks
     */
    public void addMenuItem(String id, String name, String description, double price, MenuCategory category) {
        // Step 1: Validate required string fields and numerical constraints
        InputValidator.validateNotNullOrEmpty(id, "Item ID");
        InputValidator.validateNotNullOrEmpty(name, "Item Name");
        InputValidator.validatePositiveNumber(price, "Price");

        // Step 2: Ensure uniqueness - check if an item with this ID is already registered
        if (menuItemDAO.findById(id).isPresent()) {
            throw new BusinessRuleException("Menu item with ID " + id + " already exists.");
        }

        // Step 3: Instantiate the new MenuItem entity and save it to the database
        MenuItem newItem = new MenuItem(id, name, description, price, category);
        menuItemDAO.add(newItem);
    }

    /**
     * Updates the pricing of an existing menu item.
     *
     * @param id       The ID of the item to update
     * @param newPrice The updated monetary cost (must be > 0)
     */
    public void updateMenuItemPrice(String id, double newPrice) {
        // Step 1: Enforce positive pricing rule
        InputValidator.validatePositiveNumber(newPrice, "Price");

        // Step 2: Fetch the target item; throw an exception if it doesn't exist
        MenuItem item = menuItemDAO.findById(id)
                .orElseThrow(() -> new BusinessRuleException("Menu item not found: " + id));

        // Step 3: Mutate the entity state and commit the change via DAO
        item.setPrice(newPrice);
        menuItemDAO.update(item);
    }

    /**
     * Retrieves all menu items currently in the system.
     *
     * @return List of all registered MenuItem objects
     */
    public List<MenuItem> getAllMenuItems() {
        return menuItemDAO.findAll();
    }

    /**
     * Filters menu items based on a specific category (e.g., APPETIZER, DESSERT).
     *
     * @param category The category enum to filter by
     * @return Filtered list of matching MenuItem objects
     */
    public List<MenuItem> getMenuItemsByCategory(MenuCategory category) {
        return menuItemDAO.findByCategory(category);
    }

    /**
     * Removes a menu item from the catalog.
     *
     * @param id The ID of the item to delete
     * @throws BusinessRuleException If no item with the given ID was found
     */
    public void deleteMenuItem(String id) {
        // Attempt removal via DAO; returns true if successful, false if ID didn't exist
        boolean removed = menuItemDAO.delete(id);
        if (!removed) {
            throw new BusinessRuleException("Failed to delete menu item. Item not found: " + id);
        }
    }
}