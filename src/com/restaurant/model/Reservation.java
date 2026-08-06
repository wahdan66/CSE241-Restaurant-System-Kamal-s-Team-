package com.restaurant.model;

import java.time.LocalDateTime;

public class Reservation {
    private String id;
    private String customerName;
    private String customerPhone;
    private int tableNumber;
    private LocalDateTime reservationTime;

    public Reservation() {
    }

    public Reservation(String id, String customerName, String customerPhone, int tableNumber, LocalDateTime reservationTime) {
        this.id = id;
        this.customerName = customerName;
        this.customerPhone = customerPhone;
        this.tableNumber = tableNumber;
        this.reservationTime = reservationTime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCustomerName() {
        return customerName;
    }

    public void setCustomerName(String customerName) {
        this.customerName = customerName;
    }

    public String getCustomerPhone() {
        return customerPhone;
    }

    public void setCustomerPhone(String customerPhone) {
        this.customerPhone = customerPhone;
    }

    public int getTableNumber() {
        return tableNumber;
    }

    public void setTableNumber(int tableNumber) {
        this.tableNumber = tableNumber;
    }

    public LocalDateTime getReservationTime() {
        return reservationTime;
    }

    public void setReservationTime(LocalDateTime reservationTime) {
        this.reservationTime = reservationTime;
    }
}