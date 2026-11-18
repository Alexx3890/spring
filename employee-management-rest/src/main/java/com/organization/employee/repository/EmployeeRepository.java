package com.organization.employee.repository;

import com.organization.employee.model.Employee;

import java.util.List;
import java.util.Optional;

/**
 * Data Access interface for Employee persistence operations.
 */
public interface EmployeeRepository {
    Employee save(Employee employee);
    Optional<Employee> findById(Long id);
    List<Employee> findAll();
    boolean deleteById(Long id);
    boolean existsById(Long id);
}

