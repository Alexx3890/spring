package com.organization.employee.repository;

import com.organization.employee.model.Employee;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-Memory thread-safe repository implementation.
 * Provides immediate execution without requiring external database dependencies.
 */
@Repository
public class InMemoryEmployeeRepository implements EmployeeRepository {

    private final Map<Long, Employee> store = new ConcurrentHashMap<>();
    private final AtomicLong idGenerator = new AtomicLong(100);

    public InMemoryEmployeeRepository() {
        // Seed default employees for testing and verification
        save(new Employee("Alice Johnson", "Engineering", 92000.00));
        save(new Employee("Bob Smith", "Human Resources", 68000.00));
        save(new Employee("Charlie Davis", "Finance", 75000.00));
    }

    @Override
    public Employee save(Employee employee) {
        if (employee.getId() == null || employee.getId() <= 0) {
            employee.setId(idGenerator.incrementAndGet());
        }
        store.put(employee.getId(), employee);
        return employee;
    }

    @Override
    public Optional<Employee> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public List<Employee> findAll() {
        return new ArrayList<>(store.values());
    }

    @Override
    public boolean deleteById(Long id) {
        return store.remove(id) != null;
    }

    @Override
    public boolean existsById(Long id) {
        return store.containsKey(id);
    }
}

