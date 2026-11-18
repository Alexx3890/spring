package com.organization.employee.controller;

import com.organization.employee.model.Employee;
import com.organization.employee.service.EmployeeService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * REST Controller exposing Employee CRUD operations.
 * Fulfills requirements (b), (c), (d), and (e).
 */
@RestController
@RequestMapping("/api/employees")
public class EmployeeController {

    private final EmployeeService employeeService;

    public EmployeeController(EmployeeService employeeService) {
        this.employeeService = employeeService;
    }

    /**
     * Operation (b): Add a new employee using @PostMapping and @RequestBody.
     * Returns HTTP 201 Created on success.
     */
    @PostMapping
    public ResponseEntity<Employee> addEmployee(@Valid @RequestBody Employee employee) {
        Employee created = employeeService.addEmployee(employee);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    /**
     * Operation (c) Part 1: Retrieve all employees using @GetMapping.
     * Returns HTTP 200 OK.
     */
    @GetMapping("/getAllEmployees")
    public ResponseEntity<List<Employee>> getAllEmployees() {
        List<Employee> list = employeeService.getAllEmployees();
        return ResponseEntity.ok(list);
    }

    /**
     * Operation (c) Part 2: Retrieve a specific employee by ID using @GetMapping.
     * Returns HTTP 200 OK or HTTP 404 Not Found via exception handler.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Employee> getEmployeeById(@PathVariable Long id) {
        Employee employee = employeeService.getEmployeeById(id);
        return ResponseEntity.ok(employee);
    }

    /**
     * Operation (d): Update an employee's details using @PutMapping.
     * Returns HTTP 200 OK or HTTP 404 Not Found.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Employee> updateEmployee(@PathVariable Long id, @Valid @RequestBody Employee employeeDetails) {
        Employee updated = employeeService.updateEmployee(id, employeeDetails);
        return ResponseEntity.ok(updated);
    }

    /**
     * Operation (e): Delete an employee by ID using @DeleteMapping.
     * Returns HTTP 200 OK with confirmation message (or HTTP 404 Not Found if absent).
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> deleteEmployee(@PathVariable Long id) {
        employeeService.deleteEmployee(id);
        Map<String, Object> response = new HashMap<>();
        response.put("status", HttpStatus.OK.value());
        response.put("message", "Employee with ID " + id + " has been deleted successfully.");
        return ResponseEntity.ok(response);
    }
}

