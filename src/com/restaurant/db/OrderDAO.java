package com.restaurant.db;

import com.restaurant.model.Order;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class OrderDAO implements GenericDAO<Order, String> {

    @Override
    public void add(Order order) {
        RestaurantDatabase.orders.add(order);
    }

    @Override
    public Optional<Order> findById(String id) {
        return RestaurantDatabase.orders.stream()
                .filter(o -> o.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    @Override
    public List<Order> findAll() {
        return new ArrayList<>(RestaurantDatabase.orders);
    }

    @Override
    public boolean update(Order order) {
        Optional<Order> existing = findById(order.getId());
        if (existing.isPresent()) {
            int index = RestaurantDatabase.orders.indexOf(existing.get());
            RestaurantDatabase.orders.set(index, order);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        return RestaurantDatabase.orders.removeIf(o -> o.getId().equalsIgnoreCase(id));
    }
}