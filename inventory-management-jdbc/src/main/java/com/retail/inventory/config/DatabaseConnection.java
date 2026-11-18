package com.retail.inventory.config;

import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

/**
 * Manages database connectivity for the Inventory Management System.
 * Supports native MySQL connectivity with an automatic embedded H2 fallback
 * (in MySQL-compatible mode) so the entire suite can be tested even if a
 * local MySQL server daemon is offline.
 */
public class DatabaseConnection {
    private static Properties props = new Properties();
    private static boolean fallbackMode = false;
    private static boolean h2Initialized = false;

    static {
        try (InputStream in = DatabaseConnection.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (Exception e) {
            System.err.println("Warning: db.properties could not be loaded, using defaults: " + e.getMessage());
        }
    }

    /**
     * Obtains an active SQL connection.
     * Tries primary MySQL first. If unavailable, falls back to embedded H2 (MySQL mode).
     */
    public static Connection getConnection() throws SQLException {
        if (!fallbackMode) {
            String driver = props.getProperty("db.driver", "com.mysql.cj.jdbc.Driver");
            String url = props.getProperty("db.url", "jdbc:mysql://localhost:3306/retail_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true");
            String user = props.getProperty("db.user", "root");
            String pass = props.getProperty("db.password", "root");

            try {
                Class.forName(driver);
                // Attempt connection with a short login timeout
                DriverManager.setLoginTimeout(3);
                Connection conn = DriverManager.getConnection(url, user, pass);
                return conn;
            } catch (ClassNotFoundException | SQLException e) {
                System.out.println("\n[NOTICE] Unable to reach MySQL Server on localhost:3306 (" + e.getMessage() + ")");
                System.out.println("[NOTICE] Seamlessly switching to embedded H2 database (in MySQL mode) for instant execution...\n");
                fallbackMode = true;
            }
        }

        // Fallback: H2 in MySQL compatibility mode
        String h2Driver = props.getProperty("db.fallback.driver", "org.h2.Driver");
        String h2Url = props.getProperty("db.fallback.url", "jdbc:h2:mem:retail_db;MODE=MySQL;DATABASE_TO_LOWER=TRUE;DB_CLOSE_DELAY=-1");
        String h2User = props.getProperty("db.fallback.user", "sa");
        String h2Pass = props.getProperty("db.fallback.password", "");

        try {
            Class.forName(h2Driver);
        } catch (ClassNotFoundException ex) {
            throw new SQLException("Neither MySQL nor H2 JDBC driver is available on classpath.", ex);
        }

        Connection conn = DriverManager.getConnection(h2Url, h2User, h2Pass);
        if (!h2Initialized) {
            initSchema(conn);
            h2Initialized = true;
        }
        return conn;
    }

    /**
     * Initializes the Product schema and starter records in fallback mode.
     */
    private static void initSchema(Connection conn) {
        String ddl = "CREATE TABLE IF NOT EXISTS Product (" +
                     "product_id INT PRIMARY KEY, " +
                     "product_name VARCHAR(100) NOT NULL, " +
                     "category VARCHAR(50) NOT NULL, " +
                     "quantity INT NOT NULL DEFAULT 0, " +
                     "price DOUBLE NOT NULL)";

        String seed = "MERGE INTO Product (product_id, product_name, category, quantity, price) KEY(product_id) VALUES " +
                      "(101, 'Wireless Mouse', 'Electronics', 15, 24.99), " +
                      "(102, 'Mechanical Keyboard', 'Electronics', 8, 89.99), " +
                      "(103, 'USB-C Cable', 'Electronics', 45, 9.99), " +
                      "(104, 'Organic Green Tea', 'Grocery', 12, 14.50), " +
                      "(105, 'Ground Coffee', 'Grocery', 25, 18.00), " +
                      "(106, 'Ergonomic Desk Chair', 'Furniture', 5, 199.99), " +
                      "(107, 'Defective Monitor (Discontinued)', 'Electronics', 0, 149.99)";

        try (Statement stmt = conn.createStatement()) {
            stmt.execute(ddl);
            stmt.execute(seed);
        } catch (SQLException e) {
            System.err.println("Warning: Could not initialize schema: " + e.getMessage());
        }
    }

    public static boolean isFallbackMode() {
        return fallbackMode;
    }

    public static String getDatabaseType() {
        return fallbackMode ? "Embedded H2 (MySQL Compatibility Mode)" : "MySQL Server (retail_db)";
    }
}

