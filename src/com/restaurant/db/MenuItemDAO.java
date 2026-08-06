package com.restaurant.db;

import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/** Provides CRUD operations and category filtering for menu items. */
public class MenuItemDAO implements GenericDAO<MenuItem, String> {

    @Override
    public void add(MenuItem item) {
        RestaurantDatabase.menuItems.add(item);
    }

    @Override
    public Optional<MenuItem> findById(String id) {
        return RestaurantDatabase.menuItems.stream()
                .filter(m -> m.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    public List<MenuItem> findByCategory(MenuCategory category) {
        // Enum values are single constants, so identity comparison is appropriate.
        return RestaurantDatabase.menuItems.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    @Override
    public List<MenuItem> findAll() {
        return new ArrayList<>(RestaurantDatabase.menuItems);
    }

    @Override
    public boolean update(MenuItem item) {
        Optional<MenuItem> existing = findById(item.getId());
        if (existing.isPresent()) {
            int index = RestaurantDatabase.menuItems.indexOf(existing.get());
            RestaurantDatabase.menuItems.set(index, item);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        return RestaurantDatabase.menuItems.removeIf(m -> m.getId().equalsIgnoreCase(id));
    }
}
