package com.retail.inventory;

import com.retail.inventory.dao.ProductDAO;
import com.retail.inventory.dao.ProductDAOImpl;
import com.retail.inventory.model.Product;
import org.junit.jupiter.api.*;

import java.sql.SQLException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class InventorySystemTest {

    private static ProductDAO productDAO;

    @BeforeAll
    public static void setUp() {
        productDAO = new ProductDAOImpl();
    }

    @Test
    @Order(1)
    @DisplayName("(a) Insert Product Records and Duplicate Handling")
    public void testInsertProductsAndDuplicateHandling() throws SQLException {
        // Insert product 301
        Product p1 = new Product(301, "Test Gaming Monitor", "Electronics", 10, 299.99);
        boolean inserted1 = productDAO.insertProduct(p1);
        assertTrue(inserted1, "Product 301 should be inserted successfully.");

        // Insert duplicate product 301 - should be handled and return false
        Product duplicate = new Product(301, "Duplicate Monitor", "Electronics", 5, 199.99);
        boolean insertedDuplicate = productDAO.insertProduct(duplicate);
        assertFalse(insertedDuplicate, "Duplicate product ID 301 should be rejected.");

        // Insert product 302 and 303 (ensuring at least three products are inserted)
        Product p2 = new Product(302, "Test Bluetooth Speaker", "Electronics", 12, 49.99);
        Product p3 = new Product(303, "Test HDMI Cable", "Electronics", 50, 15.00);
        productDAO.insertProduct(p2);
        productDAO.insertProduct(p3);

        assertTrue(productDAO.productExists(301));
        assertTrue(productDAO.productExists(302));
        assertTrue(productDAO.productExists(303));
    }

    @Test
    @Order(2)
    @DisplayName("(b) Conditional Retrieval and Sorting (quantity < 20, ORDER BY price DESC)")
    public void testConditionalRetrievalAndSorting() throws SQLException {
        List<Product> results = productDAO.getProductsByCategoryAndLowStock("Electronics", 20);
        assertNotNull(results);
        assertFalse(results.isEmpty(), "Should retrieve electronics with quantity < 20");

        // Verify all have quantity < 20 and category matches
        for (Product p : results) {
            assertTrue(p.getQuantity() < 20, "Quantity must be strictly less than 20");
            assertEquals("Electronics", p.getCategory(), "Category must match");
        }

        // Verify sorted by price in descending order
        for (int i = 0; i < results.size() - 1; i++) {
            assertTrue(results.get(i).getPrice() >= results.get(i + 1).getPrice(),
                    "Products must be sorted in descending order of price");
        }
    }

    @Test
    @Order(3)
    @DisplayName("(c) Update Product Stock (incremental addition)")
    public void testUpdateProductStock() throws SQLException {
        // Verify non-existent product update returns false
        boolean nonExistent = productDAO.updateProductStock(99999, 10);
        assertFalse(nonExistent, "Updating non-existent product should return false");

        // Update product 301: initial quantity was 10, add 15 -> should become 25
        Product before = productDAO.getProductById(301);
        int initialQty = before.getQuantity();

        boolean updated = productDAO.updateProductStock(301, 15);
        assertTrue(updated, "Stock update should return true");

        Product after = productDAO.getProductById(301);
        assertEquals(initialQty + 15, after.getQuantity(), "Quantity should be incremented by 15");
    }

    @Test
    @Order(4)
    @DisplayName("(d) Search and Modify Product Details (Price Update)")
    public void testSearchAndModifyPrice() throws SQLException {
        // Verify non-existent product
        boolean nonExistent = productDAO.updateProductPrice(99999, 50.0);
        assertFalse(nonExistent, "Updating price of non-existent product should return false");

        // Verify invalid price <= 0 throws exception
        assertThrows(IllegalArgumentException.class, () -> {
            productDAO.updateProductPrice(301, -20.0);
        });

        // Valid price update
        double newPrice = 349.99;
        boolean updated = productDAO.updateProductPrice(301, newPrice);
        assertTrue(updated, "Price update should succeed");

        Product refreshed = productDAO.getProductById(301);
        assertEquals(newPrice, refreshed.getPrice(), 0.001, "Price must reflect the new value");
    }

    @Test
    @Order(5)
    @DisplayName("(e) Aggregate Query and Conditional Deletion")
    public void testAggregateAndConditionalDeletion() throws SQLException {
        // Test Aggregate Query SUM(quantity * price)
        double totalVal = productDAO.calculateTotalInventoryValue();
        assertTrue(totalVal > 0.0, "Total inventory value must be greater than zero");

        // Insert a zero-quantity product
        Product zeroStockProduct = new Product(304, "Defective Keyboard Unit", "Electronics", 0, 75.00);
        productDAO.insertProduct(zeroStockProduct);

        // Attempt to delete a product with stock > 0 (e.g. 301 with qty 25) -> should be blocked
        assertThrows(IllegalStateException.class, () -> {
            productDAO.deleteProductIfZeroStock(301);
        }, "Deleting a product with quantity > 0 must throw IllegalStateException");

        // Delete product 304 with stock == 0 -> should succeed
        boolean deleted = productDAO.deleteProductIfZeroStock(304);
        assertTrue(deleted, "Product with 0 quantity must be successfully deleted");
        assertFalse(productDAO.productExists(304), "Product 304 should no longer exist in the database");
    }
}

