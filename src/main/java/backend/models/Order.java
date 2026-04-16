package backend.models;

import backend.DatabaseManager;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.*;

public class Order {

    private String orderID;                 //uuids
    private String descriptions;
    private String address;
    private String deliveryType;
    private String orderStatus;
    private String emailAddress;
    private LocalDateTime createdAt;

    private final List<OrderItem> items = new ArrayList<>();

    public Order() {}

    public Order(String orderID, String descriptions, String address, String deliveryType,
                 String orderStatus, String emailAddress, LocalDateTime createdAt) {
        this.orderID = orderID;
        this.descriptions = descriptions;
        this.address = address;
        this.deliveryType = deliveryType;
        this.orderStatus = orderStatus;
        this.emailAddress = emailAddress;
        this.createdAt = createdAt;
    }

    public String getOrderID() { return orderID; }
    public String getDescriptions() { return descriptions; }
    public String getAddress() { return address; }
    public String getDeliveryType() { return deliveryType; }
    public String getOrderStatus() { return orderStatus; }
    public String getEmailAddress() { return emailAddress; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public List<OrderItem> getItems() { return items; }


    public static String newUUID() {
        return UUID.randomUUID().toString();
    }



    public static String buildDescriptionFromBasket(List<ItemCell> basket) {
        if (basket == null || basket.isEmpty()) return "Order: (empty)";
        Map<Integer, Integer> qty = new LinkedHashMap<>();
        Map<Integer, String> desc = new HashMap<>();

        for (ItemCell it : basket) {
            if (it == null) continue;
            qty.put(Integer.parseInt(Integer.toString( it.getItemID())), qty.getOrDefault(it.getItemID(), 0) + 1);
            desc.putIfAbsent(Integer.parseInt(Integer.toString(it.getItemID())), it.getDescriptions());
        }

        StringBuilder sb = new StringBuilder("Order: ");
        boolean first = true;
        for (Map.Entry<Integer, Integer> e : qty.entrySet()) {
            if (!first) sb.append(", ");
            first = false;
            String d = desc.getOrDefault(e.getKey(), "Item " + e.getKey());
            sb.append(d).append(" x").append(e.getValue());
        }
        return sb.toString();
    }

    public static void saveOrderWithItems(
            String orderId,
            String address,
            String deliveryType,
            String emailAddress,
            List<ItemCell> basketList
    ) {
        if (orderId == null || orderId.isBlank()) throw new IllegalArgumentException("orderId missing");
        if (basketList == null || basketList.isEmpty()) throw new IllegalArgumentException("basket empty");

        DatabaseManager db = new DatabaseManager();

        // compile dups into list
        Map<Integer, Integer> qty = new LinkedHashMap<>();
        Map<Integer, ItemCell> anyItemForId = new HashMap<>();
        for (ItemCell it : basketList) {
            if (it == null) continue;
            int id = it.getItemID();
            qty.put(id, qty.getOrDefault(id, 0) + 1);
            anyItemForId.putIfAbsent(id, it);
        }

        String description = buildDescriptionFromBasket(basketList);

        String insertOrderSql = """
                INSERT INTO catalogue.orders
                (OrderID, Descriptions, Address, DeliveryType, OrderStatus, EmailAddress)
                VALUES (?, ?, ?, ?, ?, ?)
                """;

        String insertItemSql = """
                INSERT INTO catalogue.`order_items`
                (OrderID, ItemID, Quantity, UnitPrice, ItemDescription)
                VALUES (?, ?, ?, ?, ?)
                """;

        try (Connection con = db.makeConnection()) {
            if (con == null) throw new SQLException("DB connection is null");
            con.setAutoCommit(false);

            try (PreparedStatement psOrder = con.prepareStatement(insertOrderSql)) {
                psOrder.setString(1, orderId);
                psOrder.setString(2, description);
                psOrder.setString(3, address);
                psOrder.setString(4, deliveryType);
                psOrder.setString(5, "Pending");
                psOrder.setString(6, emailAddress);
                psOrder.executeUpdate();
            }

            try (PreparedStatement psItem = con.prepareStatement(insertItemSql)) {
                for (Map.Entry<Integer, Integer> e : qty.entrySet()) {
                    int itemId = e.getKey();
                    int quantity = e.getValue();
                    ItemCell it = anyItemForId.get(itemId);

                    double unitPrice = (it == null) ? 0.0 : it.getPackageCost();
                    String itemDesc = (it == null) ? ("Item " + itemId) : it.getDescriptions();

                    psItem.setString(1, orderId);
                    psItem.setInt(2, itemId);
                    psItem.setInt(3, quantity);
                    psItem.setDouble(4, unitPrice);
                    psItem.setString(5, itemDesc);
                    psItem.addBatch();
                }
                psItem.executeBatch();
            }

            con.commit();
        } catch (SQLException ex) {
            throw new RuntimeException("Failed to save order + items for OrderID=" + orderId, ex);
        }
    }

    // for api
    public static Optional<Order> findById(String orderId) {
        DatabaseManager db = new DatabaseManager();

        String headerSql = """
                SELECT OrderID, Description, Address, DeliveryType, OrderStatus, EmailAddress, CreatedAt
                FROM catalogue.orders
                WHERE OrderID = ?
                """;

        String itemsSql = """
                SELECT ItemID, Quantity, UnitPrice, ItemDescription
                FROM catalogue.order_items
                WHERE OrderID = ?
                ORDER BY OrderItemID ASC
                """;

        try (Connection con = db.makeConnection()) {
            if (con == null) throw new SQLException("DB connection is null");

            Order order = null;

            try (PreparedStatement ps = con.prepareStatement(headerSql)) {
                ps.setString(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    if (!rs.next()) return Optional.empty();

                    Timestamp createdTs = rs.getTimestamp("CreatedAt");
                    LocalDateTime createdAt = (createdTs == null) ? null : createdTs.toLocalDateTime();

                    order = new Order(
                            rs.getString("OrderID"),
                            rs.getString("Description"),
                            rs.getString("Address"),
                            rs.getString("DeliveryType"),
                            rs.getString("OrderStatus"),
                            rs.getString("EmailAddress"),
                            createdAt
                    );
                }
            }

            try (PreparedStatement ps = con.prepareStatement(itemsSql)) {
                ps.setString(1, orderId);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        order.items.add(new OrderItem(
                                rs.getInt("ItemID"),
                                rs.getInt("Quantity"),
                                rs.getDouble("UnitPrice"),
                                rs.getString("ItemDescription")
                        ));
                    }
                }
            }

            return Optional.of(order);

        } catch (SQLException ex) {
            throw new RuntimeException("Failed to load order by OrderID=" + orderId, ex);
        }
    }

    public static class OrderItem {
        private final int itemID;
        private final int quantity;
        private final double unitPrice;
        private final String descriptions;

        public OrderItem(int itemID, int quantity, double unitPrice, String descriptions) {
            this.itemID = itemID;
            this.quantity = quantity;
            this.unitPrice = unitPrice;
            this.descriptions = descriptions;
        }

        public int getItemID() { return itemID; }
        public int getQuantity() { return quantity; }
        public double getUnitPrice() { return unitPrice; }
        public String getDescriptions() { return descriptions; }
    }
}