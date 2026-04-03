package backend.prm.repository;


import backend.prm.database.DatabaseConnection;
import backend.prm.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Optional;

public class ProductDAO {

    public Optional<Product> findById(String productId) {
        String sql = """
                SELECT product_id, merchant_id, description, package_type, unit,
                       units_in_pack, package_cost, availability_packs,
                       stock_limit_packs, is_active
                FROM products
                WHERE product_id = ?
                """;

        try (Connection connection = DatabaseConnection.getConnection();
             PreparedStatement ps = connection.prepareStatement(sql)) {

            ps.setString(1, productId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    Product product = new Product(
                            rs.getString("product_id"),
                            rs.getInt("merchant_id"),
                            rs.getString("description"),
                            rs.getString("package_type"),
                            rs.getString("unit"),
                            rs.getInt("units_in_pack"),
                            rs.getDouble("package_cost"),
                            rs.getInt("availability_packs"),
                            rs.getInt("stock_limit_packs"),
                            rs.getBoolean("is_active")
                    );
                    return Optional.of(product);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Failed to load product: " + productId, e);
        }

        return Optional.empty();
    }
}