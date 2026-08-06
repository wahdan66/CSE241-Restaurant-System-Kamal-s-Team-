package com.restaurant.db;

import com.restaurant.model.Table;
import com.restaurant.model.TableStatus;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class TableDAO implements GenericDAO<Table, Integer> {

    @Override
    public void add(Table table) {
        RestaurantDatabase.tables.add(table);
    }

    @Override
    public Optional<Table> findById(Integer tableNumber) {
        return RestaurantDatabase.tables.stream()
                .filter(t -> t.getTableNumber() == tableNumber)
                .findFirst();
    }

    public List<Table> findByStatus(TableStatus status) {
        return RestaurantDatabase.tables.stream()
                .filter(t -> t.getStatus() == status)
                .collect(Collectors.toList());
    }

    @Override
    public List<Table> findAll() {
        return new ArrayList<>(RestaurantDatabase.tables);
    }

    @Override
    public boolean update(Table table) {
        Optional<Table> existing = findById(table.getTableNumber());
        if (existing.isPresent()) {
            int index = RestaurantDatabase.tables.indexOf(existing.get());
            RestaurantDatabase.tables.set(index, table);
            return true;
        }
        return false;
    }

    @Override
    public boolean delete(Integer tableNumber) {
        return RestaurantDatabase.tables.removeIf(t -> t.getTableNumber() == tableNumber);
    }
}