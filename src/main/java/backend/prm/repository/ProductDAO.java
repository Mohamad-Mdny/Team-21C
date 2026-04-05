package backend.prm.repository;

import backend.DatabaseManager;
import backend.models.Item;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductDAO {

    DatabaseManager database = new DatabaseManager();
    public static class ProductSummary {
        private final String productId;
        private final String description;
        private final double packageCost;



        public ProductSummary(String productId, String description, double packageCost) {
            this.productId = productId;
            this.description = description;
            this.packageCost = packageCost;
        }

        public String getProductId() {
            return productId;
        }

        public String getDescription() {
            return description;
        }

        public double getPackageCost() {
            return packageCost;
        }

        @Override
        public String toString() {
            return productId + " | " + description + " | £" + String.format("%.2f", packageCost);
        }
    }

    public Optional<Item> findById(String productId) {
        String sql = """
                SELECT product_id, description, package_type, unit,
                       units_in_pack, package_cost, availability_packs,
                       stock_limit_packs, is_active
                FROM catalogue
                WHERE product_id = ?
                """;

        try (
                Connection connection = database.makeConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Item product = new Item(
                            rs.getInt("product_id"),
                            rs.getString("description"),
                            rs.getString("package_type"),
                            rs.getString("unit"),
                            rs.getInt("units_in_pack"),
                            (float) rs.getDouble("package_cost"),
                            rs.getInt("availability_packs"),
                            (float) rs.getDouble("stock_limit_packs")
                    );
                    return Optional.of(product);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load product: " + productId, e);
        }

        return Optional.empty();
    }

    public List<ProductSummary> findAllActiveProducts() {
        String sql = """
                SELECT product_id, description, package_cost
                FROM catalogue
                WHERE is_active = true
                ORDER BY description ASC, product_id ASC
                """;

        List<ProductSummary> result = new ArrayList<>();

        try (
                Connection connection = database.makeConnection();
                PreparedStatement ps = connection.prepareStatement(sql);
                ResultSet rs = ps.executeQuery()
        ) {
            while (rs.next()) {
                result.add(new ProductSummary(
                        rs.getString("product_id"),
                        rs.getString("description"),
                        rs.getDouble("package_cost")
                ));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to load products list.", e);
        }

        return result;
    }

    public List<ProductSummary> searchActiveProducts(String queryText) {
        String sql = """
            
                SELECT product_id, description, package_cost
            FROM catalogue
            WHERE is_active = TRUE
              AND (
                  product_id LIKE ?
                  OR description LIKE ?
              )
            ORDER BY description ASC, product_id ASC
            """;

        String q = "%" + (queryText == null ? "" : queryText.trim()) + "%";
        List<ProductSummary> result = new ArrayList<>();

        try (
                Connection connection = database.makeConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, q);
            ps.setString(2, q);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    result.add(new ProductSummary(
                            rs.getString("product_id"),
                            rs.getString("description"),
                            rs.getDouble("package_cost")
                    ));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to search products.", e);
        }

        return result;
    }
}