package com.restaurant.model;

public class Customer extends User {
    private String phoneNumber;
    private int loyaltyPoints;

    public Customer() {
        super();
        setRole(Role.CUSTOMER);
    }

    public Customer(String id, String name, String username, String passwordHash, String phoneNumber) {
        super(id, name, username, passwordHash, Role.CUSTOMER);
        this.phoneNumber = phoneNumber;
        this.loyaltyPoints = 0;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public int getLoyaltyPoints() {
        return loyaltyPoints;
    }

    public void setLoyaltyPoints(int loyaltyPoints) {
        this.loyaltyPoints = loyaltyPoints;
    }
}