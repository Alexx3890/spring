package com.retail.inventory;

import com.retail.inventory.config.DatabaseConnection;
import com.retail.inventory.dao.ProductDAO;
import com.retail.inventory.dao.ProductDAOImpl;
import com.retail.inventory.model.Product;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

/**
 * Menu-Driven Console Application for Retail Inventory Management System.
 * Covers all five required operations:
 * (a) Insert Product Records (with duplicate handling)
 * (b) Conditional Retrieval and Sorting (quantity < 20, ORDER BY price DESC)
 * (c) Update Product Stock (incremental addition with validation)
 * (d) Search and Modify Product Details (price > 0 with live display)
 * (e) Aggregate Query and Conditional Deletion (SUM(quantity*price) & zero-stock deletion)
 */
public class MainApplication {
    private static final ProductDAO productDAO = new ProductDAOImpl();
    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("===============================================================");
        System.out.println("  WELCOME TO RETAIL INVENTORY MANAGEMENT SYSTEM (JDBC & MySQL)");
        System.out.println("===============================================================");

        try {
            // Verify and print active database connectivity
            System.out.println("Connecting to Database...");
            DatabaseConnection.getConnection();
            System.out.println("Connected successfully to: " + DatabaseConnection.getDatabaseType());
        } catch (SQLException e) {
            System.err.println("Fatal: Database initialization failed: " + e.getMessage());
            return;
        }

        boolean running = true;
        while (running) {
            printMenu();
            System.out.print("Enter your choice (1-7): ");
            String input = scanner.nextLine().trim();

            try {
                switch (input) {
                    case "1":
                        handleInsertProduct();
                        break;
                    case "2":
                        handleConditionalRetrieval();
                        break;
                    case "3":
                        handleUpdateStock();
                        break;
                    case "4":
                        handleSearchAndModifyPrice();
                        break;
                    case "5":
                        handleAggregateAndConditionalDeletion();
                        break;
                    case "6":
                        handleDisplayAllProducts();
                        break;
                    case "7":
                        System.out.println("\nExiting Retail Inventory Management System. Goodbye!");
                        running = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please select an option between 1 and 7.");
                }
            } catch (Exception e) {
                System.err.println(">> Error during operation: " + e.getMessage());
            }
            System.out.println();
        }
    }

    private static void printMenu() {
        System.out.println("---------------------------------------------------------------");
        System.out.println("                        MAIN MENU                              ");
        System.out.println("---------------------------------------------------------------");
        System.out.println("1. [Operation A] Insert Product Records (Single or 3-Batch)");
        System.out.println("2. [Operation B] View Low-Stock Products by Category (< 20)");
        System.out.println("3. [Operation C] Update Product Stock (Add Quantity)");
        System.out.println("4. [Operation D] Search & Modify Product Price");
        System.out.println("5. [Operation E] Total Inventory Valuation & Safe Delete (Qty=0)");
        System.out.println("6. View All Inventory Products");
        System.out.println("7. Exit");
        System.out.println("---------------------------------------------------------------");
    }

    /**
     * Operation (a): Insert Product Records using PreparedStatement.
     * Handles duplicate product IDs appropriately.
     */
    private static void handleInsertProduct() throws SQLException {
        System.out.println("\n--- [Operation A] Insert Product Records ---");
        System.out.println("Select insertion mode:");
        System.out.println("  1. Interactive Manual Entry (Single Product)");
        System.out.println("  2. Quick Batch Insert (Insert at least 3 required sample records)");
        System.out.print("Enter mode (1 or 2): ");
        String mode = scanner.nextLine().trim();

        if ("2".equals(mode)) {
            // Seed 3+ records using PreparedStatement
            List<Product> batchList = Arrays.asList(
                    new Product(201, "Smart 4K Television 55\"", "Electronics", 6, 449.99),
                    new Product(202, "Noise Cancelling Headphones", "Electronics", 18, 129.50),
                    new Product(203, "Stainless Steel Water Bottle", "HomeGoods", 14, 19.99),
                    new Product(204, "Expired Milk Carton (Cleared)", "Grocery", 0, 3.50)
            );
            System.out.println("\nExecuting batch insertion of sample records using PreparedStatement...");
            int inserted = 0;
            for (Product p : batchList) {
                boolean success = productDAO.insertProduct(p);
                if (success) {
                    System.out.println(" + Inserted: " + p.getProductName() + " (ID: " + p.getProductId() + ")");
                    inserted++;
                }
            }
            System.out.println("Batch process complete. Successfully inserted " + inserted + " records.");
            return;
        }

        // Manual interactive insert
        System.out.print("Enter Product ID (Integer): ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        if (productDAO.productExists(id)) {
            System.out.println(">> Insertion Failed: A product with ID " + id + " already exists. Duplicate IDs are not allowed.");
            return;
        }

        System.out.print("Enter Product Name: ");
        String name = scanner.nextLine().trim();

        System.out.print("Enter Category (e.g. Electronics, Grocery, Furniture): ");
        String category = scanner.nextLine().trim();

        System.out.print("Enter Initial Quantity: ");
        int quantity = Integer.parseInt(scanner.nextLine().trim());

        System.out.print("Enter Price (e.g. 49.99): ");
        double price = Double.parseDouble(scanner.nextLine().trim());

        Product product = new Product(id, name, category, quantity, price);
        boolean success = productDAO.insertProduct(product);
        if (success) {
            System.out.println(">> Success: Product [" + id + " - " + name + "] inserted successfully.");
        } else {
            System.out.println(">> Failed to insert product.");
        }
    }

    /**
     * Operation (b): Conditional Retrieval and Sorting.
     * Category filter + quantity < 20 + sorted by price DESC.
     */
    private static void handleConditionalRetrieval() throws SQLException {
        System.out.println("\n--- [Operation B] Conditional Retrieval and Sorting ---");
        System.out.print("Enter Category name to filter (e.g. Electronics, Grocery, Furniture): ");
        String category = scanner.nextLine().trim();

        int threshold = 20;
        List<Product> products = productDAO.getProductsByCategoryAndLowStock(category, threshold);

        System.out.println("\nQuery: Products in Category '" + category + "' with Quantity < " + threshold + " (Sorted by Price DESC):");
        if (products.isEmpty()) {
            System.out.println(">> No products found matching category '" + category + "' with stock < " + threshold + ".");
        } else {
            printProductTable(products);
        }
    }

    /**
     * Operation (c): Update Product Stock.
     * Adds quantity to existing stock. Validates product existence.
     */
    private static void handleUpdateStock() throws SQLException {
        System.out.println("\n--- [Operation C] Update Product Stock ---");
        System.out.print("Enter Product ID to update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        Product existing = productDAO.getProductById(id);
        if (existing == null) {
            System.out.println(">> Update Failed: Product with ID " + id + " does not exist in the database.");
            return;
        }

        System.out.println("Current Details: Product '" + existing.getProductName() + "', Current Stock = " + existing.getQuantity());
        System.out.print("Enter Quantity to ADD to stock: ");
        int qtyToAdd = Integer.parseInt(scanner.nextLine().trim());

        if (qtyToAdd <= 0) {
            System.out.println(">> Invalid Quantity: Quantity to add must be positive (> 0).");
            return;
        }

        boolean updated = productDAO.updateProductStock(id, qtyToAdd);
        if (updated) {
            Product refreshed = productDAO.getProductById(id);
            System.out.println(">> Success: Stock updated successfully!");
            System.out.println("   Product ID  : " + refreshed.getProductId());
            System.out.println("   Name        : " + refreshed.getProductName());
            System.out.println("   Previous Qty: " + existing.getQuantity());
            System.out.println("   Added Qty   : +" + qtyToAdd);
            System.out.println("   New Stock   : " + refreshed.getQuantity());
        } else {
            System.out.println(">> Stock update failed.");
        }
    }

    /**
     * Operation (d): Search and Modify Product Details.
     * Validates product existence and price > 0. Displays updated details.
     */
    private static void handleSearchAndModifyPrice() throws SQLException {
        System.out.println("\n--- [Operation D] Search and Modify Product Details (Price Update) ---");
        System.out.print("Enter Product ID to search and update: ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        Product existing = productDAO.getProductById(id);
        if (existing == null) {
            System.out.println(">> Search Failed: Product with ID " + id + " does not exist.");
            return;
        }

        System.out.println("Found Product: " + existing.getProductName() + " | Category: " + existing.getCategory() +
                " | Current Price: $" + String.format("%.2f", existing.getPrice()));

        System.out.print("Enter New Unit Price: ");
        double newPrice = Double.parseDouble(scanner.nextLine().trim());

        if (newPrice <= 0.0) {
            System.out.println(">> Modification Rejected: Price must be strictly greater than zero ($" + newPrice + " is invalid).");
            return;
        }

        boolean updated = productDAO.updateProductPrice(id, newPrice);
        if (updated) {
            Product refreshed = productDAO.getProductById(id);
            System.out.println(">> Success: Product price updated successfully!");
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println(String.format("| %-10s | %-30s | %-15s | %-10s | %-10s |", "ID", "Product Name", "Category", "Quantity", "Price"));
            System.out.println("----------------------------------------------------------------------------------");
            System.out.println(refreshed);
            System.out.println("----------------------------------------------------------------------------------");
        } else {
            System.out.println(">> Price modification failed.");
        }
    }

    /**
     * Operation (e): Aggregate Query and Conditional Deletion.
     * Displays SUM(quantity * price). Then allows deleting product ONLY if quantity == 0.
     */
    private static void handleAggregateAndConditionalDeletion() throws SQLException {
        System.out.println("\n--- [Operation E] Aggregate Valuation & Safe Conditional Deletion ---");

        // Part 1: Display total inventory value
        double totalValuation = productDAO.calculateTotalInventoryValue();
        System.out.println("===============================================================");
        System.out.println(String.format(">> TOTAL INVENTORY VALUE [SUM(quantity * price)]: $%,.2f", totalValuation));
        System.out.println("===============================================================");

        // Part 2: Conditional deletion
        System.out.println("\nConditional Record Deletion Rule: Items can ONLY be deleted if Quantity = 0.");
        System.out.print("Enter Product ID to delete (or 0 to cancel): ");
        int id = Integer.parseInt(scanner.nextLine().trim());

        if (id == 0) {
            System.out.println("Deletion cancelled by user.");
            return;
        }

        Product target = productDAO.getProductById(id);
        if (target == null) {
            System.out.println(">> Deletion Error: Product with ID " + id + " does not exist in the database.");
            return;
        }

        try {
            boolean deleted = productDAO.deleteProductIfZeroStock(id);
            if (deleted) {
                System.out.println(">> Success: Product [ID: " + id + ", Name: '" + target.getProductName() + "'] with 0 quantity has been deleted.");
            } else {
                System.out.println(">> Deletion could not be completed.");
            }
        } catch (IllegalStateException e) {
            System.out.println(">> DELETION REJECTED: " + e.getMessage());
        }
    }

    private static void handleDisplayAllProducts() throws SQLException {
        System.out.println("\n--- Current Inventory Table ---");
        List<Product> all = productDAO.getAllProducts();
        if (all.isEmpty()) {
            System.out.println("Inventory is currently empty.");
        } else {
            printProductTable(all);
        }
    }

    private static void printProductTable(List<Product> products) {
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println(String.format("| %-10s | %-30s | %-15s | %-10s | %-10s |", "Product ID", "Product Name", "Category", "Quantity", "Price"));
        System.out.println("----------------------------------------------------------------------------------");
        for (Product p : products) {
            System.out.println(p);
        }
        System.out.println("----------------------------------------------------------------------------------");
        System.out.println("Total matching records: " + products.size());
    }
}

