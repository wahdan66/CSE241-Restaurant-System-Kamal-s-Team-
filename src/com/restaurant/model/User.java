package com.restaurant.model;

import java.util.UUID;

/**
 * Represents an account that can access the restaurant system.
 *
 * <p>The password is stored as a hash rather than as plain text.</p>
 */
public class User {
    /** Stores the user's unique identifier as text, including UUID values when used. */
    private String id;
    /** Stores the name shown for this user in the application. */
    private String name;
    /** Stores the username used during sign-in. */
    private String username;
    /** Stores only the password hash, never the original password. */
    private String passwordHash;
    /** Stores the permissions category assigned to the user. */
    private Role role;

    /**
     * Creates an empty user. Fields can be supplied later through setters.
     */
    public User() {
    }

    /**
     * Creates a user with all account details.
     *
     * @param id unique identifier for the user
     * @param name user's display name
     * @param username user's login name
     * @param passwordHash hashed password for the account
     * @param role user's access role
     */
    public User(String id, String name, String username, String passwordHash, Role role) {
        // Copy the supplied account data into this new user object.
        this.id = id;
        this.name = name;
        this.username = username;
        this.passwordHash = passwordHash;
        this.role = role;
    }

    /**
     * Creates a user with a UUID identifier.
     *
     * @param id unique UUID identifier for the user
     * @param name user's display name
     * @param username user's login name
     * @param passwordHash hashed password for the account
     * @param role user's access role
     */
    public User(UUID id, String name, String username, String passwordHash, Role role) {
        // Convert a UUID to text because the id field is stored as a String.
        // Preserve null when no identifier has been supplied.
        this(id == null ? null : id.toString(), name, username, passwordHash, role);
    }

    /** @return the user's unique identifier */
    public String getId() {
        return id;
    }

    /** @param id the user's unique identifier */
    public void setId(String id) {
        this.id = id;
    }

    /** @return the user's display name */
    public String getName() {
        return name;
    }

    /** @param name the user's display name */
    public void setName(String name) {
        this.name = name;
    }

    /** @return the user's login name */
    public String getUsername() {
        return username;
    }

    /** @param username the user's login name */
    public void setUsername(String username) {
        this.username = username;
    }

    /** @return the hashed password */
    public String getPasswordHash() {
        return passwordHash;
    }

    /** @param passwordHash the hashed password */
    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    /** @return the user's access role */
    public Role getRole() {
        return role;
    }

    /** @param role the user's access role */
    public void setRole(Role role) {
        this.role = role;
    }
}
