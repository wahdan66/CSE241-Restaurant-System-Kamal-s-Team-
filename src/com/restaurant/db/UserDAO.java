package com.restaurant.db;

import com.restaurant.model.User;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/** Provides CRUD and username lookup operations over the in-memory user list. */
public class UserDAO implements GenericDAO<User, String> {

    @Override
    public void add(User user) {
        RestaurantDatabase.users.add(user);
    }

    @Override
    public Optional<User> findById(String id) {
        return RestaurantDatabase.users.stream()
                .filter(u -> u.getId().equalsIgnoreCase(id))
                .findFirst();
    }

    public Optional<User> findByUsername(String username) {
        // Usernames are compared without case so sign-in lookup is consistent.
        return RestaurantDatabase.users.stream()
                .filter(u -> u.getUsername().equalsIgnoreCase(username))
                .findFirst();
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(RestaurantDatabase.users);
    }

    @Override
    public boolean update(User user) {
        // Find the stored instance first, then replace it at the same list position.
        Optional<User> existing = findById(user.getId());
        if (existing.isPresent()) {
            int index = RestaurantDatabase.users.indexOf(existing.get());
            RestaurantDatabase.users.set(index, user);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(String id) {
        return RestaurantDatabase.users.removeIf(u -> u.getId().equalsIgnoreCase(id));
    }
}
