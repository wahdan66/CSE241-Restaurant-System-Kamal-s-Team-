package com.restaurant.db;

import java.util.List;
import java.util.Optional;

/**
 * Generic Data Access Object contract defining standard CRUD operations.
 *
 * @param <T> Entity type
 * @param <K> Primary Key type (e.g., String, Integer)
 */
public interface GenericDAO<T, K> {
    void add(T entity);
    Optional<T> findById(K id);
    List<T> findAll();
    boolean update(T entity);
    boolean delete(K id);
}