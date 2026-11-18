package com.retail.inventory.dao;

import com.retail.inventory.config.DatabaseConnection;
import com.retail.inventory.model.Product;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

/**
 * JDBC implementation of ProductDAO utilizing PreparedStatements for all parameterized queries
 * to ensure robust SQL injection prevention and type safety.
 */
public class ProductDAOImpl implements ProductDAO {

    @Override
    public boolean productExists(int productId) throws SQLException {
        String sql = "SELECT COUNT(*) FROM Product WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        return false;
    }

    @Override
    public boolean insertProduct(Product product) throws SQLException {
        // Handle duplicate primary keys explicitly
        if (productExists(product.getProductId())) {
            System.err.println(">> [DUPLICATE KEY ERROR] A product with ID " + product.getProductId() + " already exists in the database.");
            return false;
        }

        String sql = "INSERT INTO Product (product_id, product_name, category, quantity, price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, product.getProductId());
            ps.setString(2, product.getProductName());
            ps.setString(3, product.getCategory());
            ps.setInt(4, product.getQuantity());
            ps.setDouble(5, product.getPrice());

            int rows = ps.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            // Check for unique key violation across MySQL (1062 / 23000) and PostgreSQL (23505)
            if ("23505".equals(e.getSQLState()) || "23000".equals(e.getSQLState()) || e instanceof SQLIntegrityConstraintViolationException) {
                System.err.println(">> [SQL CONSTRAINT ERROR] Duplicate product ID encountered: " + product.getProductId());
                return false;
            }
            throw e;
        }
    }

    @Override
    public Product getProductById(int productId) throws SQLException {
        String sql = "SELECT product_id, product_name, category, quantity, price FROM Product WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return mapRowToProduct(rs);
                }
            }
        }
        return null;
    }

    @Override
    public List<Product> getAllProducts() throws SQLException {
        List<Product> list = new ArrayList<>();
        String sql = "SELECT product_id, product_name, category, quantity, price FROM Product ORDER BY product_id ASC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(mapRowToProduct(rs));
            }
        }
        return list;
    }

    @Override
    public List<Product> getProductsByCategoryAndLowStock(String category, int maxQuantity) throws SQLException {
        List<Product> list = new ArrayList<>();
        // Conditional retrieval: category match, quantity < maxQuantity, sorted by price descending
        String sql = "SELECT product_id, product_name, category, quantity, price " +
                     "FROM Product " +
                     "WHERE LOWER(category) = LOWER(?) AND quantity < ? " +
                     "ORDER BY price DESC";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setString(1, category.trim());
            ps.setInt(2, maxQuantity);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapRowToProduct(rs));
                }
            }
        }
        return list;
    }

    @Override
    public boolean updateProductStock(int productId, int quantityToAdd) throws SQLException {
        if (!productExists(productId)) {
            return false;
        }

        String sql = "UPDATE Product SET quantity = quantity + ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, quantityToAdd);
            ps.setInt(2, productId);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    @Override
    public boolean updateProductPrice(int productId, double newPrice) throws SQLException {
        if (newPrice <= 0.0) {
            throw new IllegalArgumentException("Price must be strictly greater than zero.");
        }
        if (!productExists(productId)) {
            return false;
        }

        String sql = "UPDATE Product SET price = ? WHERE product_id = ?";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setDouble(1, newPrice);
            ps.setInt(2, productId);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    @Override
    public double calculateTotalInventoryValue() throws SQLException {
        // Aggregate query: SUM(quantity * price)
        String sql = "SELECT COALESCE(SUM(quantity * price), 0.0) AS total_value FROM Product";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) {
                return rs.getDouble("total_value");
            }
        }
        return 0.0;
    }

    @Override
    public boolean deleteProductIfZeroStock(int productId) throws SQLException {
        Product p = getProductById(productId);
        if (p == null) {
            return false; // Does not exist
        }

        // Conditional Deletion: only if quantity == 0
        if (p.getQuantity() > 0) {
            throw new IllegalStateException("Product [ID: " + productId + ", Name: '" + p.getProductName() +
                    "'] cannot be deleted because its stock quantity is " + p.getQuantity() +
                    ". Only items with 0 quantity can be deleted.");
        }

        String sql = "DELETE FROM Product WHERE product_id = ? AND quantity = 0";
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            ps.setInt(1, productId);
            int rows = ps.executeUpdate();
            return rows > 0;
        }
    }

    private Product mapRowToProduct(ResultSet rs) throws SQLException {
        return new Product(
                rs.getInt("product_id"),
                rs.getString("product_name"),
                rs.getString("category"),
                rs.getInt("quantity"),
                rs.getDouble("price")
        );
    }
}

