package com.organization.employee;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.organization.employee.model.Employee;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class EmployeeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @Order(1)
    @DisplayName("(b) POST /api/employees - Create new Employee (201 Created)")
    public void testCreateEmployeeSuccess() throws Exception {
        Employee newEmp = new Employee("Daniel Brown", "Quality Assurance", 65000.00);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(newEmp)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.name", is("Daniel Brown")))
                .andExpect(jsonPath("$.department", is("Quality Assurance")))
                .andExpect(jsonPath("$.salary", is(65000.00)));
    }

    @Test
    @Order(2)
    @DisplayName("(b) POST /api/employees - Validation Failure (400 Bad Request)")
    public void testCreateEmployeeValidationFailure() throws Exception {
        // Blank name and negative salary
        Employee invalidEmp = new Employee("", "Engineering", -500.0);

        mockMvc.perform(post("/api/employees")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidEmp)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status", is(400)))
                .andExpect(jsonPath("$.messages.name", notNullValue()))
                .andExpect(jsonPath("$.messages.salary", notNullValue()));
    }

    @Test
    @Order(3)
    @DisplayName("(c) GET /api/employees - Retrieve All Employees (200 OK)")
    public void testGetAllEmployees() throws Exception {
        mockMvc.perform(get("/api/employees"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", isA(java.util.List.class)))
                .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(3)));
    }

    @Test
    @Order(4)
    @DisplayName("(c) GET /api/employees/{id} - Retrieve Specific Employee Success (200 OK)")
    public void testGetEmployeeByIdSuccess() throws Exception {
        // Retrieve seeded employee with ID 101
        mockMvc.perform(get("/api/employees/101"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.name", is("Alice Johnson")))
                .andExpect(jsonPath("$.department", is("Engineering")));
    }

    @Test
    @Order(5)
    @DisplayName("(c) GET /api/employees/{id} - Employee Not Found (404 Not Found)")
    public void testGetEmployeeByIdNotFound() throws Exception {
        mockMvc.perform(get("/api/employees/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")))
                .andExpect(jsonPath("$.message", containsString("Employee not found with id: 9999")));
    }

    @Test
    @Order(6)
    @DisplayName("(d) PUT /api/employees/{id} - Update Employee Details (200 OK)")
    public void testUpdateEmployeeSuccess() throws Exception {
        Employee updatePayload = new Employee("Alice Johnson", "Lead Engineering", 105000.00);

        mockMvc.perform(put("/api/employees/101")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id", is(101)))
                .andExpect(jsonPath("$.department", is("Lead Engineering")))
                .andExpect(jsonPath("$.salary", is(105000.00)));
    }

    @Test
    @Order(7)
    @DisplayName("(d) PUT /api/employees/{id} - Update Non-Existent Employee (404 Not Found)")
    public void testUpdateEmployeeNotFound() throws Exception {
        Employee updatePayload = new Employee("Non Existent", "HR", 50000.00);

        mockMvc.perform(put("/api/employees/9999")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updatePayload)))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)));
    }

    @Test
    @Order(8)
    @DisplayName("(e) DELETE /api/employees/{id} - Delete Employee Success (200 OK)")
    public void testDeleteEmployeeSuccess() throws Exception {
        // Delete seeded employee 102
        mockMvc.perform(delete("/api/employees/102"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status", is(200)))
                .andExpect(jsonPath("$.message", containsString("deleted successfully")));

        // Verify subsequent GET returns 404
        mockMvc.perform(get("/api/employees/102"))
                .andExpect(status().isNotFound());
    }

    @Test
    @Order(9)
    @DisplayName("(e) DELETE /api/employees/{id} - Delete Non-Existent Employee (404 Not Found)")
    public void testDeleteEmployeeNotFound() throws Exception {
        mockMvc.perform(delete("/api/employees/9999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.status", is(404)))
                .andExpect(jsonPath("$.error", is("Not Found")));
    }
}

