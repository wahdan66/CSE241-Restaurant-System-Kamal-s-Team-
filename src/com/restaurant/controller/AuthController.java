package com.restaurant.controller;

import com.restaurant.db.UserDAO;
import com.restaurant.exception.BusinessRuleException;
import com.restaurant.model.User;
import com.restaurant.util.InputValidator;

import java.util.Optional;

/**
 * Coordinates authentication and keeps track of the user signed in during the
 * current application session.
 */
public class AuthController {
    private final UserDAO userDAO;
    private static User currentUser;

    public AuthController() {
        this.userDAO = new UserDAO();
    }

    public AuthController(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    public User login(String username, String rawPassword) {
        // Reject incomplete credentials before querying the data source.
        InputValidator.validateNotNullOrEmpty(username, "Username");
        InputValidator.validateNotNullOrEmpty(rawPassword, "Password");

        // Look up the account first so authentication can be performed on its
        // stored credentials.
        Optional<User> userOpt = userDAO.findByUsername(username);
        if (userOpt.isEmpty()) {
            // Use the same message for both failures so usernames are not
            // disclosed to someone attempting to sign in.
            throw new BusinessRuleException("Invalid username or password.");
        }

        User user = userOpt.get();
        // The project currently compares the supplied password with the stored
        // value. Replace this with hash verification when password hashing is
        // introduced.
        if (!user.getPasswordHash().equals(rawPassword)) {
            throw new BusinessRuleException("Invalid username or password.");
        }

        // Only a fully authenticated user becomes the active session user.
        this.currentUser = user;
        return user;
    }

    public void logout() {
        // Clearing this reference ends the in-memory session.
        this.currentUser = null;
    }

    public User getCurrentUser() {
        return currentUser;
    }

    public boolean isLoggedIn() {
        return currentUser != null;
    }
}
