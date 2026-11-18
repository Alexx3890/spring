-- =====================================================================
-- Database Initialization Script for Retail Inventory Management System
-- Course Outcome: CO4 (Database Connectivity using JDBC & PostgreSQL)
-- Target Database: retail_db
-- =====================================================================

-- Step 1: Drop Existing Product Table (if re-initializing)
DROP TABLE IF EXISTS Product CASCADE;

-- Step 2: Create Product Table
CREATE TABLE Product (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    price DOUBLE PRECISION NOT NULL
);

-- Step 3: Insert Initial Sample Records
INSERT INTO Product (product_id, product_name, category, quantity, price) VALUES
(101, 'Wireless Mouse', 'Electronics', 15, 24.99),
(102, 'Mechanical Keyboard', 'Electronics', 8, 89.99),
(103, 'USB-C Cable', 'Electronics', 45, 9.99),
(104, 'Organic Green Tea', 'Grocery', 12, 14.50),
(105, 'Ground Coffee', 'Grocery', 25, 18.00),
(106, 'Ergonomic Desk Chair', 'Furniture', 5, 199.99),
(107, 'Defective Monitor (Discontinued)', 'Electronics', 0, 149.99)
ON CONFLICT (product_id) DO NOTHING;

-- Step 4: Verify Initial Records
SELECT * FROM Product ORDER BY product_id;
