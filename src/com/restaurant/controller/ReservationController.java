package com.restaurant.controller;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.db.TableDAO;
import com.restaurant.exception.*;
import com.restaurant.model.Reservation;
import com.restaurant.model.Table;
import com.restaurant.util.InputValidator;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Controller responsible for managing customer table bookings.
 * Handles validation of party dates, customer details, and table eligibility.
 */
public class ReservationController {
    // Data access object for validating target tables
    private final TableDAO tableDAO;

    /**
     * Default constructor initializing with standard TableDAO.
     */
    public ReservationController() {
        this.tableDAO = new TableDAO();
    }

    /**
     * Overloaded constructor for dependency injection.
     *
     * @param tableDAO The DAO instance to use for table verification
     */
    public ReservationController(TableDAO tableDAO) {
        this.tableDAO = tableDAO;
    }

    /**
     * Creates and records a new table reservation after verifying scheduling and table eligibility.
     *
     * @param reservationId   Unique identifier for this booking
     * @param customerName    Name of the person making the booking
     * @param phone           Contact phone number (validated against standard phone patterns)
     * @param tableNumber     The specific table requested for booking
     * @param reservationTime The target date and time for the reservation
     * @return The newly created Reservation object
     * @throws BusinessRuleException If table doesn't exist, is inactive, or time is invalid
     */
    public Reservation createReservation(String reservationId, String customerName, String phone, int tableNumber, LocalDateTime reservationTime) {
        // Step 1: Validate text field inputs and format correctness
        InputValidator.validateNotNullOrEmpty(reservationId, "Reservation ID");
        InputValidator.validateNotNullOrEmpty(customerName, "Customer Name");
        InputValidator.validatePhoneNumber(phone);

        // Step 2: Enforce temporal rule — reservations cannot be scheduled in the past
        if (reservationTime.isBefore(LocalDateTime.now())) {
            throw new BusinessRuleException("Reservation time cannot be in the past.");
        }

        // Step 3: Verify the physical table exists in the system
        Table table = tableDAO.findById(tableNumber)
                .orElseThrow(() -> new BusinessRuleException("Table number " + tableNumber + " does not exist."));

        // Step 4: Ensure the table is active and operationally available for seating
        if (!table.isActive()) {
            throw new BusinessRuleException("Cannot reserve an inactive table.");
        }

        // Step 5: Instantiate and store the reservation in the database repository
        Reservation reservation = new Reservation(reservationId, customerName, phone, tableNumber, reservationTime);
        RestaurantDatabase.reservations.add(reservation);

        return reservation;
    }

    /**
     * Retrieves a copy of all current reservations in the system.
     *
     * @return Defensive copy list of all Reservation objects
     */
    public List<Reservation> getAllReservations() {
        // Returning a new ArrayList copy prevents external code from mutating the central DB list directly
        return new ArrayList<>(RestaurantDatabase.reservations);
    }

    /**
     * Cancels an active reservation by its unique identifier.
     *
     * @param reservationId The ID of the reservation to cancel
     * @throws BusinessRuleException If the target reservation ID is not found
     */
    public void cancelReservation(String reservationId) {
        // Search and remove matching record based on case-insensitive ID match
        boolean removed = RestaurantDatabase.reservations.removeIf(r -> r.getId().equalsIgnoreCase(reservationId));

        if (!removed) {
            throw new BusinessRuleException("Reservation not found: " + reservationId);
        }
    }
}