package com.restaurant.network;

import com.restaurant.db.RestaurantDatabase;
import com.restaurant.model.MenuCategory;
import com.restaurant.model.MenuItem;
import com.restaurant.model.Order;
import com.restaurant.model.OrderItem;
import com.restaurant.model.OrderStatus;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;

/** Serializes order changes so separate running clients can mirror live orders. */
public final class OrderUpdateMessage {
    private static final String CREATED = "ORDER_CREATED";
    private static final String STATUS_CHANGED = "ORDER_STATUS";

    private OrderUpdateMessage() {
    }

    public static String orderCreated(Order order) {
        StringBuilder items = new StringBuilder();
        for (OrderItem item : order.getItems()) {
            MenuItem menuItem = item.getMenuItem();
            if (items.length() > 0) {
                items.append(';');
            }
            items.append(encode(menuItem.getId())).append(':')
                    .append(encode(menuItem.getName())).append(':')
                    .append(encode(menuItem.getDescription())).append(':')
                    .append(menuItem.getPrice()).append(':')
                    .append(menuItem.getCategory().name()).append(':')
                    .append(item.getQuantity());
        }
        return String.join("|", CREATED, encode(order.getId()), String.valueOf(order.getTableNumber()),
                encode(order.getCustomerId()), order.getStatus().name(), String.valueOf(order.getPartySize()),
                order.getScheduledFor().toString(), items.toString());
    }

    public static String statusChanged(String orderId, OrderStatus status) {
        return String.join("|", STATUS_CHANGED, encode(orderId), status.name());
    }

    /** Applies a received live update. Returns true only for recognized order messages. */
    public static boolean apply(String message) {
        try {
            String[] parts = message.split("\\|", -1);
            if (parts.length == 8 && CREATED.equals(parts[0])) {
                Order order = new Order(decode(parts[1]), Integer.parseInt(parts[2]));
                order.setCustomerId(decode(parts[3]));
                order.setStatus(OrderStatus.valueOf(parts[4]));
                order.setPartySize(Integer.parseInt(parts[5]));
                order.setScheduledFor(LocalDateTime.parse(parts[6]));
                if (!parts[7].isEmpty()) {
                    for (String itemData : parts[7].split(";")) {
                        String[] itemParts = itemData.split(":", -1);
                        MenuItem item = new MenuItem(decode(itemParts[0]), decode(itemParts[1]), decode(itemParts[2]),
                                Double.parseDouble(itemParts[3]), MenuCategory.valueOf(itemParts[4]));
                        order.addItem(new OrderItem(item, Integer.parseInt(itemParts[5])));
                    }
                }
                RestaurantDatabase.orders.removeIf(existing -> existing.getId().equals(order.getId()));
                RestaurantDatabase.orders.add(order);
                return true;
            }
            if (parts.length == 3 && STATUS_CHANGED.equals(parts[0])) {
                String orderId = decode(parts[1]);
                OrderStatus status = OrderStatus.valueOf(parts[2]);
                RestaurantDatabase.orders.stream()
                        .filter(order -> order.getId().equals(orderId))
                        .findFirst()
                        .ifPresent(order -> {
                            order.setStatus(status);
                        });
                return true;
            }
        } catch (RuntimeException ignored) {
            // Ignore malformed network data rather than breaking the socket listener.
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
