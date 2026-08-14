package com.restaurant.network;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/** Transfers individual menu changes so connected clients stay in sync. */
public final class MenuUpdateMessage {
    private static final String UPSERT = "MENU_UPSERT";
    private static final String DELETE = "MENU_DELETE";
    private static final String SNAPSHOT = "MENU_SNAPSHOT";

    private MenuUpdateMessage() {
    }

    public static String upsert(MenuItem item) {
        return String.join("|", UPSERT, encode(item.getId()), encode(item.getName()),
                encode(item.getDescription()), String.valueOf(item.getPrice()), item.getCategory().name());
    }

    public static String delete(String itemId) {
        return DELETE + "|" + encode(itemId);
    }

    public static String snapshot() {
        StringBuilder items = new StringBuilder(SNAPSHOT).append('|');
        for (MenuItem item : RestaurantDatabase.menuItems) {
            if (items.charAt(items.length() - 1) != '|') {
                items.append(';');
            }
            items.append(encode(item.getId())).append(':').append(encode(item.getName())).append(':')
                    .append(encode(item.getDescription())).append(':').append(item.getPrice()).append(':')
                    .append(item.getCategory().name());
        }
        return items.toString();
    }

    /** Applies a received menu change and returns true when the message is recognized. */
    public static boolean apply(String message) {
        try {
            String[] parts = message.split("\\|", -1);
            if (parts.length == 6 && UPSERT.equals(parts[0])) {
                MenuItem item = new MenuItem(decode(parts[1]), decode(parts[2]), decode(parts[3]),
                        Double.parseDouble(parts[4]), MenuCategory.valueOf(parts[5]));
                RestaurantDatabase.menuItems.removeIf(existing -> existing.getId().equalsIgnoreCase(item.getId()));
                RestaurantDatabase.menuItems.add(item);
                return true;
            }
            if (parts.length == 2 && DELETE.equals(parts[0])) {
                String itemId = decode(parts[1]);
                RestaurantDatabase.menuItems.removeIf(item -> item.getId().equalsIgnoreCase(itemId));
                return true;
            }
            if (parts.length == 2 && SNAPSHOT.equals(parts[0])) {
                RestaurantDatabase.menuItems.clear();
                if (!parts[1].isEmpty()) {
                    for (String itemData : parts[1].split(";")) {
                        String[] itemParts = itemData.split(":", -1);
                        RestaurantDatabase.menuItems.add(new MenuItem(decode(itemParts[0]), decode(itemParts[1]),
                                decode(itemParts[2]), Double.parseDouble(itemParts[3]),
                                MenuCategory.valueOf(itemParts[4])));
                    }
                }
                return true;
            }
        } catch (RuntimeException ignored) {
            // Ignore malformed messages and keep the live listener running.
        }
        return false;
    }

    private static String encode(String value) {
        return Base64.getUrlEncoder().withoutPadding()
                .encodeToString((value == null ? "" : value).getBytes(StandardCharsets.UTF_8));
    }

    private static String decode(String value) {
        return new String(Base64.getUrlDecoder().decode(value), StandardCharsets.UTF_8);
    }
}
