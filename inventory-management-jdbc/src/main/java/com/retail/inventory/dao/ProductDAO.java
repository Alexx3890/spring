package com.retail.inventory.dao;

import com.retail.inventory.model.Product;
import java.sql.SQLException;
import java.util.List;

/**
 * Data Access Object (DAO) interface defining all CRUD and analytical operations
 * specified in the assessment requirements.
 */
public interface ProductDAO {

    /**
     * Operation (a): Insert a product record using PreparedStatement.
     * Handles duplicate primary keys appropriately.
     */
    boolean insertProduct(Product product) throws SQLException;

    /**
     * Checks whether a product with the specified product_id exists.
     */
    boolean productExists(int productId) throws SQLException;

    /**
     * Retrieves a single product by its primary key.
     */
    Product getProductById(int productId) throws SQLException;

    /**
     * Retrieves all products currently stored.
     */
    List<Product> getAllProducts() throws SQLException;

    /**
     * Operation (b): Conditional Retrieval and Sorting.
     * Accepts a category from the user and retrieves all products belonging to that category
     * whose quantity is less than 20. Results are sorted by price in descending order.
     */
    List<Product> getProductsByCategoryAndLowStock(String category, int maxQuantity) throws SQLException;

    /**
     * Operation (c): Update Product Stock.
     * Accepts a product ID and a quantity to be added to existing stock.
     * Updates quantity and returns true if updated, false if product does not exist.
     */
    boolean updateProductStock(int productId, int quantityToAdd) throws SQLException;

    /**
     * Operation (d): Search and Modify Product Details.
     * Accepts a product ID and a new price. Updates price only if product exists
     * and new price > 0.
     */
    boolean updateProductPrice(int productId, double newPrice) throws SQLException;

    /**
     * Operation (e) Part 1: Aggregate Query.
     * Displays the total inventory value using SUM(quantity * price).
     */
    double calculateTotalInventoryValue() throws SQLException;

    /**
     * Operation (e) Part 2: Conditional Deletion.
     * Deletes the product only if its quantity is zero.
     * Returns true if deleted; throws IllegalStateException or returns false if quantity > 0.
     */
    boolean deleteProductIfZeroStock(int productId) throws SQLException;
}

