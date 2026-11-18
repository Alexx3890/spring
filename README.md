# Enterprise Backend Systems: JDBC Inventory & Spring Boot REST API

This repository contains full implementations and test suites for two academic/enterprise assessment modules:
1. **Module 1 (CO4 — 10 Marks):** Inventory Management System using **Java JDBC and MySQL**
2. **Module 2 (CO5 — 10 Marks):** Employee Management System using **Spring Boot REST API**

---

## Quick Navigation
- [How to Start the Applications](#1-how-to-start-the-applications)
- [Module 2: Complete Postman Testing Guide](#2-module-2-employee-rest-api--postman-testing-guide)
  - [Option A: One-Click Postman Collection Import (Recommended)](#option-a-one-click-postman-collection-import-recommended)
  - [Option B: Manual Postman Setup (Step-by-Step for Every Endpoint)](#option-b-manual-postman-setup-step-by-step)
- [Equivalent cURL Commands](#3-equivalent-curl-commands)
- [Module 1: JDBC Inventory Management System Guide](#4-module-1-inventory-management-system-jdbc--mysql)
- [Automated Test Execution (JUnit & MockMvc)](#5-automated-test-execution)

---

## 1. How to Start the Applications

### Starting Module 2: Spring Boot REST API (Port 8080)
1. Open PowerShell or Command Prompt in the project folder.
2. Navigate to `employee-management-rest`:
   ```powershell
   cd employee-management-rest
   .\mvnw.cmd spring-boot:run
   ```
   *(Or simply double-click `run-employee-api.bat`)*
3. The server will boot up at: `http://localhost:8080`

### Starting Module 1: JDBC Inventory Management System
1. Open PowerShell or Command Prompt in the project folder.
2. Navigate to `inventory-management-jdbc`:
   ```powershell
   cd inventory-management-jdbc
   .\mvnw.cmd compile exec:java
   ```
   *(Or simply double-click `run-inventory.bat`)*

---

## 2. Module 2: Employee REST API — Postman Testing Guide

### Option A: One-Click Postman Collection Import (Recommended)

A pre-configured Postman Collection is included directly in this directory:  
📁 **`Employee_Management_API.postman_collection.json`**

#### How to Import:
1. Open **Postman**.
2. Click the **Import** button in the top-left corner (or press `Ctrl + O`).
3. Drag and drop the file `Employee_Management_API.postman_collection.json` (or browse to `c:\Users\ankit\Desktop\springOOPS\Employee_Management_API.postman_collection.json`).
4. Click **Import**.
5. You will see a folder named **`Employee Management REST API (Spring Boot)`** containing all 9 pre-configured requests ready to execute with a single click.

---

### Option B: Manual Postman Setup (Step-by-Step)

If you prefer to configure each request manually in Postman, follow the instructions below:

```
Base URL: http://localhost:8080
Default Content-Type Header: application/json
```

---

### Request 1: Create a New Employee (2 Marks — Operation B)

* **HTTP Method:** `POST`
* **URL:** `http://localhost:8080/api/employees`
* **Headers:**
  | Key | Value |
  | :--- | :--- |
  | `Content-Type` | `application/json` |
* **Body Tab:** Select **raw** -> choose **JSON** from dropdown:
  ```json
  {
    "name": "Jane Doe",
    "department": "Engineering",
    "salary": 85000.00
  }
  ```
* **Expected Status:** `201 Created`
* **Sample Response Body:**
  ```json
  {
    "id": 104,
    "name": "Jane Doe",
    "department": "Engineering",
    "salary": 85000.0
  }
  ```

---

### Request 2: Input Validation Failure Handling (400 Bad Request)

* **HTTP Method:** `POST`
* **URL:** `http://localhost:8080/api/employees`
* **Headers:** `Content-Type: application/json`
* **Body Tab:** Select **raw** -> **JSON**:
  ```json
  {
    "name": "",
    "department": "",
    "salary": -500.00
  }
  ```
* **Expected Status:** `400 Bad Request`
* **Sample Response Body:**
  ```json
  {
    "status": 400,
    "error": "Bad Request - Validation Failed",
    "path": "/api/employees",
    "messages": {
      "name": "Employee name must not be blank",
      "department": "Department must not be blank",
      "salary": "Salary must be strictly positive (> 0)"
    }
  }
  ```

---

### Request 3: Retrieve All Employees (1 Mark — Operation C)

* **HTTP Method:** `GET`
* **URL:** `http://localhost:8080/api/employees`
* **Headers:** *(None required)*
* **Body Tab:** Select **none**
* **Expected Status:** `200 OK`
* **Sample Response Body:**
  ```json
  [
    {
      "id": 101,
      "name": "Alice Johnson",
      "department": "Engineering",
      "salary": 92000.0
    },
    {
      "id": 102,
      "name": "Bob Smith",
      "department": "Human Resources",
      "salary": 68000.0
    },
    {
      "id": 103,
      "name": "Charlie Davis",
      "department": "Finance",
      "salary": 75000.0
    }
  ]
  ```

---

### Request 4: Retrieve Specific Employee by ID (1 Mark — Operation C)

* **HTTP Method:** `GET`
* **URL:** `http://localhost:8080/api/employees/101`
* **Headers:** *(None required)*
* **Expected Status:** `200 OK`
* **Sample Response Body:**
  ```json
  {
    "id": 101,
    "name": "Alice Johnson",
    "department": "Engineering",
    "salary": 92000.0
  }
  ```

---

### Request 5: Retrieve Employee by Non-Existent ID (404 Not Found)

* **HTTP Method:** `GET`
* **URL:** `http://localhost:8080/api/employees/9999`
* **Expected Status:** `404 Not Found`
* **Sample Response Body:**
  ```json
  {
    "timestamp": "2026-11-16T11:20:00.123456",
    "status": 404,
    "error": "Not Found",
    "message": "Employee not found with id: 9999",
    "path": "/api/employees/9999"
  }
  ```

---

### Request 6: Update an Existing Employee (2 Marks — Operation D)

* **HTTP Method:** `PUT`
* **URL:** `http://localhost:8080/api/employees/101`
* **Headers:**
  | Key | Value |
  | :--- | :--- |
  | `Content-Type` | `application/json` |
* **Body Tab:** Select **raw** -> **JSON**:
  ```json
  {
    "name": "Alice Johnson",
    "department": "VP of Engineering",
    "salary": 135000.00
  }
  ```
* **Expected Status:** `200 OK`
* **Sample Response Body:**
  ```json
  {
    "id": 101,
    "name": "Alice Johnson",
    "department": "VP of Engineering",
    "salary": 135000.0
  }
  ```

---

### Request 7: Update Non-Existent Employee (404 Not Found)

* **HTTP Method:** `PUT`
* **URL:** `http://localhost:8080/api/employees/9999`
* **Headers:** `Content-Type: application/json`
* **Body Tab:** Select **raw** -> **JSON**:
  ```json
  {
    "name": "Non Existent",
    "department": "HR",
    "salary": 50000.00
  }
  ```
* **Expected Status:** `404 Not Found`
* **Sample Response Body:**
  ```json
  {
    "timestamp": "2026-11-16T11:22:00.654321",
    "status": 404,
    "error": "Not Found",
    "message": "Employee not found with id: 9999",
    "path": "/api/employees/9999"
  }
  ```

---

### Request 8: Delete Employee by ID (2 Marks — Operation E)

* **HTTP Method:** `DELETE`
* **URL:** `http://localhost:8080/api/employees/102`
* **Expected Status:** `200 OK`
* **Sample Response Body:**
  ```json
  {
    "status": 200,
    "message": "Employee with ID 102 has been deleted successfully."
  }
  ```
*(Note: If you run `GET http://localhost:8080/api/employees/102` immediately after, it will now return `404 Not Found`)*

---

### Request 9: Delete Non-Existent Employee (404 Not Found)

* **HTTP Method:** `DELETE`
* **URL:** `http://localhost:8080/api/employees/9999`
* **Expected Status:** `404 Not Found`
* **Sample Response Body:**
  ```json
  {
    "timestamp": "2026-11-16T11:23:00.789101",
    "status": 404,
    "error": "Not Found",
    "message": "Employee not found with id: 9999",
    "path": "/api/employees/9999"
  }
  ```

---

## 3. Equivalent cURL Commands

For fast command-line testing without Postman, you can copy-paste these into PowerShell or Terminal:

```bash
# 1. Add Employee (201 Created)
curl -X POST http://localhost:8080/api/employees -H "Content-Type: application/json" -d "{\"name\":\"Sophia Taylor\",\"department\":\"Data Science\",\"salary\":115000.00}"

# 2. Get All Employees (200 OK)
curl -X GET http://localhost:8080/api/employees

# 3. Get Employee by ID (200 OK)
curl -X GET http://localhost:8080/api/employees/101

# 4. Get Missing Employee (404 Not Found)
curl -X GET http://localhost:8080/api/employees/9999

# 5. Update Employee (200 OK)
curl -X PUT http://localhost:8080/api/employees/101 -H "Content-Type: application/json" -d "{\"name\":\"Alice Johnson\",\"department\":\"Director of Engineering\",\"salary\":145000.00}"

# 6. Delete Employee (200 OK)
curl -X DELETE http://localhost:8080/api/employees/102

# 7. Delete Missing Employee (404 Not Found)
curl -X DELETE http://localhost:8080/api/employees/9999
```

---

## 4. Module 1: Inventory Management System (JDBC & MySQL)

### Console Menu Options
```text
===============================================================
  WELCOME TO RETAIL INVENTORY MANAGEMENT SYSTEM (JDBC & MySQL)
===============================================================
1. [Operation A] Insert Product Records (Single or 3-Batch)
2. [Operation B] View Low-Stock Products by Category (< 20)
3. [Operation C] Update Product Stock (Add Quantity)
4. [Operation D] Search & Modify Product Price
5. [Operation E] Total Inventory Valuation & Safe Delete (Qty=0)
6. View All Inventory Products
7. Exit
```

### MySQL Database Setup
If using a standalone MySQL server, run `retail_db.sql` in MySQL Workbench or CLI:
```sql
CREATE DATABASE IF NOT EXISTS retail_db;
USE retail_db;

CREATE TABLE IF NOT EXISTS Product (
    product_id INT PRIMARY KEY,
    product_name VARCHAR(100) NOT NULL,
    category VARCHAR(50) NOT NULL,
    quantity INT NOT NULL DEFAULT 0,
    price DOUBLE NOT NULL
);
```

Configure your credentials in `inventory-management-jdbc/src/main/resources/db.properties`:
```properties
db.url=jdbc:mysql://localhost:3306/retail_db?createDatabaseIfNotExist=true&useSSL=false&allowPublicKeyRetrieval=true
db.user=root
db.password=root
```
*(Note: If MySQL is offline, the system automatically falls back to embedded H2 in MySQL mode so you can run and demonstrate without interruptions!)*

---

## 5. Automated Test Execution

Both projects include comprehensive automated test suites verifying every single marking criterion.

### Test Module 1 (JDBC - 5 Tests):
```powershell
cd inventory-management-jdbc
.\mvnw.cmd test
```
* **Tests:** Insert & duplicate rejection, category filter `< 20` with price descending sort, atomic stock update, price validation `> 0`, and safe zero-stock deletion rule.

### Test Module 2 (Spring Boot REST - 9 Tests):
```powershell
cd employee-management-rest
.\mvnw.cmd test
```
* **Tests:** `201 Created` on POST, `400 Bad Request` on invalid input, `200 OK` on GET all, `200 OK` on GET by ID, `404 Not Found` on missing ID, `200 OK` on PUT update, `404 Not Found` on PUT missing ID, `200 OK` on DELETE, `404 Not Found` on DELETE missing ID.
