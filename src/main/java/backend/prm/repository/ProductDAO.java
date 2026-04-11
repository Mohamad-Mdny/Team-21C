package backend.prm.repository;

import backend.DatabaseManager;
import backend.models.ItemCell;

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
        private final String descriptions;
        private final double packageCost;



        public ProductSummary(String productId, String descriptions, double packageCost) {
            this.productId = productId;
            this.descriptions = descriptions;
            this.packageCost = packageCost;
        }

        public String getProductId() {
            return productId;
        }

        public String getDescriptions() {
            return descriptions;
        }

        public double getPackageCost() {
            return packageCost;
        }

        @Override
        public String toString() {
            return productId + " | " + descriptions + " | £" + String.format("%.2f", packageCost);
        }
    }

    public Optional<ItemCell> findById(String productId) {
        String sql = """
                SELECT itemID, Descriptions, PackageType, Unit,
                       UnitsInAPack, PackageCost, Availability,
                       StockLimit
                FROM catalogue
                WHERE itemID = ?
                """;

        try (
                Connection connection = database.makeConnection();
                PreparedStatement ps = connection.prepareStatement(sql)
        ) {
            ps.setString(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    ItemCell product = new ItemCell(
                            rs.getInt("itemID"),
                            rs.getString("Descriptions"),
                            rs.getString("PackageType"),
                            rs.getString("Unit"),
                            rs.getInt("UnitsInAPack"),
                            (float) rs.getDouble("PackageCost"),
                            rs.getInt("Availability"),
                            (float) rs.getDouble("StockLimit")
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
        SELECT CAST(ItemID AS CHAR(20)) AS product_id,
               Descriptions AS descriptions,
               PackageCost AS package_cost
        FROM catalogue
        WHERE is_active = TRUE
        ORDER BY Descriptions ASC, ItemID ASC
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
                        rs.getString("descriptions"),
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
        SELECT CAST(ItemID AS CHAR(20)) AS product_id,
               Descriptions AS description,
               PackageCost AS package_cost
        FROM catalogue
        WHERE is_active = TRUE
          AND (
              CAST(ItemID AS CHAR(20)) LIKE ?
              OR Descriptions LIKE ?
          )
        ORDER BY Descriptions ASC, ItemID ASC
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
                            rs.getString("descriptions"),
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