package com.organization.employee.service;

import com.organization.employee.model.Employee;

import java.util.List;

/**
 * Service Layer interface governing business rules and employee transactions.
 */
public interface EmployeeService {
    Employee addEmployee(Employee employee);
    List<Employee> getAllEmployees();
    Employee getEmployeeById(Long id);
    Employee updateEmployee(Long id, Employee employeeDetails);
    boolean deleteEmployee(Long id);
}

