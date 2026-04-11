package backend.prm.repository;

import backend.DatabaseManager;
import backend.prm.model.PromotionCampaign;
import backend.prm.model.PromotionItem;
import backend.prm.model.PromotionStatus;
import backend.prm.report.CampaignHitReportRow;
import backend.prm.report.CampaignReportRow;
import backend.prm.report.SalesReportRow;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class PromotionRepository {

    DatabaseManager database = new DatabaseManager();
    public PromotionCampaign saveCampaign(PromotionCampaign campaign) {
        String sql = """
            INSERT INTO promotion_campaigns
            (campaign_code, title, description, start_datetime, end_datetime, discount_percent, status, cancelled_at, click_count)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {

            ps.setString(1, generateCampaignCode(connection));
            ps.setString(2, campaign.getTitle());
            ps.setString(3, campaign.getDescriptions());
            ps.setTimestamp(4, Timestamp.valueOf(campaign.getStartDateTime()));
            ps.setTimestamp(5, Timestamp.valueOf(campaign.getEndDateTime()));
            ps.setDouble(6, campaign.getDiscountPercent());
            ps.setString(7, deriveStatus(campaign, LocalDateTime.now()).name());

            // cancelled_at (param 8)
            if (campaign.getCancelledAt() != null) {
                ps.setTimestamp(8, Timestamp.valueOf(campaign.getCancelledAt()));
            } else {
                ps.setNull(8, Types.TIMESTAMP);
            }

            // click_count (param 9)
            ps.setInt(9, campaign.getClickCount());

            ps.executeUpdate();

            try (ResultSet keys = ps.getGeneratedKeys()) {
                if (keys.next()) {
                    campaign.setId(keys.getLong(1));
                }
            }

            return findCampaignById(campaign.getId()).orElse(campaign);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save campaign", e);
        }
    }


    public Optional<PromotionCampaign> findCampaignById(long id) {
        String sql = """
                SELECT campaign_id, campaign_code, title, description,
                       start_datetime, end_datetime, discount_percent,
                       cancelled_at, click_count
                FROM promotion_campaigns
                WHERE campaign_id = ?
                """;
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapCampaign(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find campaign by id: " + id, e);
        }
        return Optional.empty();
    }

    public List<PromotionCampaign> findAllCampaigns() {
        String sql = """
                SELECT campaign_id, campaign_code, title, description,
                       start_datetime, end_datetime, discount_percent,
                       cancelled_at, click_count
                FROM promotion_campaigns
                ORDER BY start_datetime DESC
                """;
        List<PromotionCampaign> campaigns = new ArrayList<>();
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {

            while (rs.next()) {
                campaigns.add(mapCampaign(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load all campaigns", e);
        }

        return campaigns;
    }

    public List<PromotionCampaign> findActiveCampaigns(LocalDateTime now) {
        String sql = """
                SELECT campaign_id, campaign_code, title, description,
                       start_datetime, end_datetime, discount_percent,
                       cancelled_at, click_count
                FROM promotion_campaigns
                WHERE cancelled_at IS NULL AND ? BETWEEN start_datetime AND end_datetime
                ORDER BY start_datetime ASC
                """;
        List<PromotionCampaign> campaigns = new ArrayList<>();
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(now));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    campaigns.add(mapCampaign(rs));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load active campaigns", e);
        }
        return campaigns;
    }

    public PromotionCampaign updateCampaign(PromotionCampaign campaign) {
        String sql = """
                UPDATE promotion_campaigns
                SET title = ?,
                    description = ?,
                    start_datetime = ?,
                    end_datetime = ?,
                    discount_percent = ?,
                    status = ?,
                    cancelled_at = ?,
                    click_count = ?
                WHERE campaign_id = ?
                """;
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, campaign.getTitle());
            ps.setString(2, campaign.getDescriptions());
            ps.setTimestamp(3, Timestamp.valueOf(campaign.getStartDateTime()));
            ps.setTimestamp(4, Timestamp.valueOf(campaign.getEndDateTime()));
            ps.setDouble(5, campaign.getDiscountPercent());
            ps.setString(6, deriveStatus(campaign, LocalDateTime.now()).name());

            if (campaign.getCancelledAt() != null) {
                ps.setTimestamp(7, Timestamp.valueOf(campaign.getCancelledAt()));
            } else {
                ps.setNull(7, Types.TIMESTAMP);
            }

            ps.setInt(8, campaign.getClickCount());
            ps.setLong(9, campaign.getId());

            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Campaign not found with ID: " + campaign.getId());
            }
            return campaign;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update campaign: " + campaign.getId(), e);
        }
    }


    public void incrementCampaignClick(long campaignId) {
        String sql = "UPDATE promotion_campaigns SET click_count = click_count + 1 WHERE campaign_id = ?";
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, campaignId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Campaign not found with ID: " + campaignId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to increment campaign click count", e);
        }
    }

    public void deleteCampaign(long campaignId) {
        String deleteItemsSql = "DELETE FROM promotion_campaign_items WHERE campaign_id = ?";
        String deleteCampaignSql = "DELETE FROM promotion_campaigns WHERE campaign_id = ?";

        try (Connection connection = database.makeConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement ps1 = connection.prepareStatement(deleteItemsSql);
                 PreparedStatement ps2 = connection.prepareStatement(deleteCampaignSql)) {

                ps1.setLong(1, campaignId);
                ps1.executeUpdate();

                ps2.setLong(1, campaignId);
                if (ps2.executeUpdate() == 0) {
                    connection.rollback();
                    throw new IllegalArgumentException("Campaign not found with ID: " + campaignId);
                }

                connection.commit();
            } catch (Exception e) {
                connection.rollback();
                throw e;
            } finally {
                connection.setAutoCommit(true);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete campaign: " + campaignId, e);
        }
    }

    public boolean campaignExists(long campaignId) {
        String sql = "SELECT 1 FROM promotion_campaigns WHERE campaign_id = ?";
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, campaignId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check campaign existence", e);
        }
    }

    public PromotionItem saveItem(PromotionItem item) {
        String productPriceSql = "SELECT PackageCost FROM catalogue WHERE ItemID = ?";
        String insertSql = """
            INSERT INTO promotion_campaign_items
            (campaign_id, product_id, discount_percent, promotional_price, added_to_order_count, purchased_count)
            VALUES (?, ?, ?, ?, ?, ?)
            """;

        try (Connection connection = database.makeConnection()) {
            double packageCost = readPackageCost(connection, item.getItemId(), productPriceSql);
            double effectiveDiscount = resolveEffectiveDiscount(connection, item);
            double promotionalPrice = packageCost - (packageCost * effectiveDiscount / 100.0);

            try (PreparedStatement ps = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setLong(1, item.getCampaignId());
                ps.setString(2, item.getItemId());

                if (item.getOverrideDiscountPercent() != null) {
                    ps.setDouble(3, item.getOverrideDiscountPercent());
                } else {
                    ps.setDouble(3, readCampaignDiscount(connection, item.getCampaignId()));
                }

                ps.setDouble(4, promotionalPrice);
                ps.setInt(5, item.getAddedToOrderCount());
                ps.setInt(6, item.getPurchasedCount());
                ps.executeUpdate();

                try (ResultSet keys = ps.getGeneratedKeys()) {
                    if (keys.next()) {
                        item.setId(keys.getLong(1));
                    }
                }
            }

            item.setPromotionalPrice(promotionalPrice);
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save campaign item", e);
        }
    }

    public PromotionItem updateItem(PromotionItem item) {
        String productPriceSql = "SELECT PackageCost FROM catalogue WHERE ItemID = ?";
        String sql = """
            UPDATE promotion_campaign_items
            SET product_id = ?,
                discount_percent = ?,
                promotional_price = ?,
                added_to_order_count = ?,
                purchased_count = ?
            WHERE campaign_item_id = ? AND campaign_id = ?
            """;

        try (Connection connection = database.makeConnection()) {
            double packageCost = readPackageCost(connection, item.getItemId(), productPriceSql);
            double effectiveDiscount = resolveEffectiveDiscount(connection, item);
            double promotionalPrice = packageCost - (packageCost * effectiveDiscount / 100.0);

            try (PreparedStatement ps = connection.prepareStatement(sql)) {
                ps.setString(1, item.getItemId());

                if (item.getOverrideDiscountPercent() != null) {
                    ps.setDouble(2, item.getOverrideDiscountPercent());
                } else {
                    ps.setDouble(2, readCampaignDiscount(connection, item.getCampaignId()));
                }

                ps.setDouble(3, promotionalPrice);
                ps.setInt(4, item.getAddedToOrderCount());
                ps.setInt(5, item.getPurchasedCount());
                ps.setLong(6, item.getId());
                ps.setLong(7, item.getCampaignId());

                if (ps.executeUpdate() == 0) {
                    throw new IllegalArgumentException("Item not found with ID: " + item.getId());
                }
            }

            item.setPromotionalPrice(promotionalPrice);
            return item;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to update campaign item", e);
        }
    }

    public void incrementItemAddedCount(long campaignId, long itemId, int quantity) {
        String sql = """
                UPDATE promotion_campaign_items
                SET added_to_order_count = added_to_order_count + ?
                WHERE campaign_item_id = ? AND campaign_id = ?
                """;
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, itemId);
            ps.setLong(3, campaignId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Item not found with ID: " + itemId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to increment added-to-order counter", e);
        }
    }

    public void incrementItemPurchasedCount(long campaignId, long itemId, int quantity) {
        String sql = """
                UPDATE promotion_campaign_items
                SET purchased_count = purchased_count + ?
                WHERE campaign_item_id = ? AND campaign_id = ?
                """;
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setLong(2, itemId);
            ps.setLong(3, campaignId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Item not found with ID: " + itemId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to increment purchased counter", e);
        }
    }

    public void savePromotionOrderEvent(long campaignId,
                                        long itemId,
                                        String productId,
                                        String eventType,
                                        int quantity,
                                        double unitPrice,
                                        String orderReference,
                                        LocalDateTime eventTime) {
        String sql = """
                INSERT INTO promotion_order_events
                (campaign_id, campaign_item_id, product_id, event_type, quantity, unit_price, order_reference, event_time)
                VALUES (?, ?, ?, ?, ?, ?, ?, ?)
                """;
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, campaignId);
            ps.setLong(2, itemId);
            ps.setString(3, productId);
            ps.setString(4, eventType);
            ps.setInt(5, quantity);
            ps.setDouble(6, unitPrice);
            ps.setString(7, orderReference);
            ps.setTimestamp(8, Timestamp.valueOf(eventTime));
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save promotion order event", e);
        }
    }

    public Optional<PromotionItem> findItemById(long itemId) {
        String sql = """
            SELECT campaign_item_id, campaign_id, product_id, discount_percent, promotional_price,
                   added_to_order_count, purchased_count
            FROM promotion_campaign_items
            WHERE campaign_item_id = ?
            """;
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapItem(rs, connection));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load campaign item: " + itemId, e);
        }
        return Optional.empty();
    }

    public List<PromotionItem> findItemsByCampaignId(long campaignId) {
        String sql = """
            SELECT campaign_item_id, campaign_id, product_id, discount_percent, promotional_price,
                   added_to_order_count, purchased_count
            FROM promotion_campaign_items
            WHERE campaign_id = ?
            ORDER BY campaign_item_id
            """;

        List<PromotionItem> result = new ArrayList<>();
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, campaignId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(mapItem(rs, connection));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load items for campaign: " + campaignId, e);
        }
        return result;
    }

    public Optional<PromotionItem> findItemByIdAndCampaignId(long itemId, long campaignId) {
        String sql = """
            SELECT campaign_item_id, campaign_id, product_id, discount_percent, promotional_price,
                   added_to_order_count, purchased_count
            FROM promotion_campaign_items
            WHERE campaign_item_id = ? AND campaign_id = ?
            """;

        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            ps.setLong(2, campaignId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return Optional.of(mapItem(rs, connection));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load campaign item: " + itemId + " in campaign " + campaignId, e);
        }
        return Optional.empty();
    }

    public boolean itemExistsInCampaign(long campaignId, String productId) {
        String sql = "SELECT 1 FROM promotion_campaign_items WHERE campaign_id = ? AND product_id = ?";
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, campaignId);
            ps.setString(2, productId);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to check item existence in campaign", e);
        }
    }

    public void deleteItem(long itemId) {
        String sql = "DELETE FROM promotion_campaign_items WHERE campaign_item_id = ?";
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, itemId);
            if (ps.executeUpdate() == 0) {
                throw new IllegalArgumentException("Item not found with ID: " + itemId);
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to delete campaign item", e);
        }
    }

    public List<SalesReportRow> getSalesReport(LocalDateTime from, LocalDateTime to) {
        String sql = """
            SELECT e.product_id,
                   COALESCE(c.description, 'Unknown product') AS product_description,
                   SUM(e.quantity) AS quantity_sold,
                   e.unit_price,
                   SUM(e.quantity * e.unit_price) AS total_price
            FROM promotion_order_events e
            LEFT JOIN catalogue c ON c.ItemID = e.product_id
            WHERE e.event_type = 'PURCHASED'
              AND e.event_time BETWEEN ? AND ?
            GROUP BY e.product_id, c.description, e.unit_price
            ORDER BY e.product_id
            """;

        List<SalesReportRow> rows = new ArrayList<>();
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(from));
            ps.setTimestamp(2, Timestamp.valueOf(to));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new SalesReportRow(
                            rs.getString("product_id"),
                            rs.getString("product_description"),
                            rs.getInt("quantity_sold"),
                            rs.getDouble("unit_price"),
                            rs.getDouble("total_price")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to build sales report", e);
        }
        return rows;
    }

    public List<CampaignReportRow> getCampaignReport(LocalDateTime from, LocalDateTime to) {
        String sql = """
                SELECT c.campaign_id,
                       c.campaign_code,
                       c.title,
                       c.start_datetime,
                       c.end_datetime,
                       c.status,
                       c.click_count,
                       COALESCE(SUM(i.added_to_order_count), 0) AS total_added,
                       COALESCE(SUM(i.purchased_count), 0) AS total_purchased
                FROM promotion_campaigns c
                LEFT JOIN promotion_campaign_items i ON i.campaign_id = c.campaign_id
                WHERE c.start_datetime <= ?
                  AND c.end_datetime >= ?
                GROUP BY c.campaign_id, c.campaign_code, c.title, c.start_datetime, c.end_datetime, c.status, c.click_count
                ORDER BY c.start_datetime
                """;

        List<CampaignReportRow> rows = new ArrayList<>();
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(to));
            ps.setTimestamp(2, Timestamp.valueOf(from));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new CampaignReportRow(
                            rs.getLong("campaign_id"),
                            rs.getString("campaign_code"),
                            rs.getString("title"),
                            rs.getTimestamp("start_datetime").toLocalDateTime(),
                            rs.getTimestamp("end_datetime").toLocalDateTime(),
                            rs.getString("status"),
                            rs.getInt("click_count"),
                            rs.getInt("total_added"),
                            rs.getInt("total_purchased")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to build campaign report", e);
        }
        return rows;
    }

    public List<CampaignHitReportRow> getCampaignHitReport(LocalDateTime from, LocalDateTime to) {
        String sql = """
            SELECT c.campaign_code,
                   c.title,
                   i.product_id,
                   COALESCE(cat.description, 'Unknown product') AS product_description,
                   c.click_count,
                   i.added_to_order_count,
                   i.purchased_count,
                   CASE
                       WHEN i.added_to_order_count = 0 THEN 0
                       ELSE (i.purchased_count * 1.0) / i.added_to_order_count
                   END AS conversion_rate
            FROM promotion_campaigns c
            JOIN promotion_campaign_items i ON i.campaign_id = c.campaign_id
            LEFT JOIN catalogue cat ON cat.ItemID = i.product_id
            WHERE c.start_datetime <= ?
              AND c.end_datetime >= ?
            ORDER BY c.campaign_id, i.campaign_item_id
            """;

        List<CampaignHitReportRow> rows = new ArrayList<>();
        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setTimestamp(1, Timestamp.valueOf(to));
            ps.setTimestamp(2, Timestamp.valueOf(from));
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    rows.add(new CampaignHitReportRow(
                            rs.getString("campaign_code"),
                            rs.getString("title"),
                            rs.getString("product_id"),
                            rs.getString("product_description"),
                            rs.getInt("click_count"),
                            rs.getInt("added_to_order_count"),
                            rs.getInt("purchased_count"),
                            rs.getDouble("conversion_rate")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to build campaign hit report", e);
        }
        return rows;


    }

    private PromotionCampaign mapCampaign(ResultSet rs) throws SQLException {
        PromotionCampaign campaign = new PromotionCampaign(
                rs.getLong("campaign_id"),
                rs.getString("campaign_code"),
                rs.getString("title"),
                rs.getString("description"),
                rs.getTimestamp("start_datetime").toLocalDateTime(),
                rs.getTimestamp("end_datetime").toLocalDateTime(),
                rs.getDouble("discount_percent")
        );

        Timestamp cancelled = rs.getTimestamp("cancelled_at");
        if (cancelled != null) {
            campaign.setCancelledAt(cancelled.toLocalDateTime());
        }

        campaign.setClickCount(rs.getInt("click_count"));
        return campaign;
    }

    private PromotionItem mapItem(ResultSet rs, Connection connection) throws SQLException {
        PromotionItem item = new PromotionItem(
                rs.getLong("campaign_item_id"),
                rs.getLong("campaign_id"),
                rs.getString("product_id"),
                rs.getDouble("promotional_price")
        );

        double itemDiscount = rs.getDouble("discount_percent");
        double campaignDiscount = readCampaignDiscount(connection, rs.getLong("campaign_id"));

        if (Double.compare(itemDiscount, campaignDiscount) == 0) {
            item.setOverrideDiscountPercent(null);
        } else {
            item.setOverrideDiscountPercent(itemDiscount);
        }

        item.setAddedToOrderCount(rs.getInt("added_to_order_count"));
        item.setPurchasedCount(rs.getInt("purchased_count"));
        return item;
    }

    private PromotionStatus deriveStatus(PromotionCampaign campaign, LocalDateTime now) {
        return campaign.getStatus(now);
    }

    private String generateCampaignCode(Connection connection) throws SQLException {
        String sql = "SELECT COUNT(*) + 1 AS next_code FROM promotion_campaigns";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            rs.next();
            int next = rs.getInt("next_code");
            return String.format("CAMP_%03d", next);
        }
    }

    private double readPackageCost(Connection connection, String productId, String sql) throws SQLException {
        try (PreparedStatement pricePs = connection.prepareStatement(sql)) {
            pricePs.setString(1, productId);
            try (ResultSet rs = pricePs.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Product not found: " + productId);
                }
                return rs.getDouble("PackageCost");
            }
        }
    }

    private double readCampaignDiscount(Connection connection, long campaignId) throws SQLException {
        String sql = "SELECT discount_percent FROM promotion_campaigns WHERE campaign_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setLong(1, campaignId);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    throw new IllegalArgumentException("Campaign not found: " + campaignId);
                }
                return rs.getDouble("discount_percent");
            }
        }
    }
    private double resolveEffectiveDiscount(Connection connection, PromotionItem item) throws SQLException {
        if (item.getOverrideDiscountPercent() != null) {
            return item.getOverrideDiscountPercent();
        }
        return readCampaignDiscount(connection, item.getCampaignId());
    }
    public void refreshPromotionalPricesForCampaignItemsWithoutOverride(long campaignId) {
        String itemSql = """
        SELECT campaign_item_id, campaign_id, product_id, discount_percent,
               promotional_price, added_to_order_count, purchased_count
        FROM promotion_campaign_items
        WHERE campaign_id = ?
        """;

        try (Connection connection = database.makeConnection();
             PreparedStatement ps = connection.prepareStatement(itemSql)) {

            ps.setLong(1, campaignId);

            try (ResultSet rs = ps.executeQuery()) {
                double campaignDiscount = readCampaignDiscount(connection, campaignId);

                while (rs.next()) {
                    PromotionItem item = mapItem(rs, connection);

                    if (item.getOverrideDiscountPercent() == null) {
                        item.setOverrideDiscountPercent(campaignDiscount);
                        updateItem(item);
                        item.setOverrideDiscountPercent(null);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to refresh item prices for campaign: " + campaignId, e);
        }
    }
}