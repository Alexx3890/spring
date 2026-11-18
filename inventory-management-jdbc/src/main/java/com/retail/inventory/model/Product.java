package com.retail.inventory.model;

/**
 * Entity representing a Product in the retail store inventory.
 * Maps to table: Product (product_id, product_name, category, quantity, price)
 */
public class Product {
    private int productId;
    private String productName;
    private String category;
    private int quantity;
    private double price;

    public Product() {
    }

    public Product(int productId, String productName, String category, int quantity, double price) {
        this.productId = productId;
        this.productName = productName;
        this.category = category;
        this.quantity = quantity;
        this.price = price;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    @Override
    public String toString() {
        return String.format("| %-10d | %-30s | %-15s | %-10d | $%-9.2f |",
                productId, productName, category, quantity, price);
    }
}

