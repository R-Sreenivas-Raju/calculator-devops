package com.spe.calculator.controller;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Integration Tests using MockMvc — hits the REAL REST endpoints.
 *
 * @SpringBootTest: loads the full Spring Boot application context
 * @AutoConfigureMockMvc: auto-configures MockMvc to perform HTTP requests
 *
 * These tests simulate real HTTP calls without starting an actual server.
 * MockMvc is perfect for testing controllers end-to-end in Jenkins CI.
 */
@SpringBootTest
@AutoConfigureMockMvc
class CalculatorControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    // ─── ADD Integration Tests ────────────────────────────────────────────────

    @Test
    @DisplayName("Integration: GET /api/v1/add?a=2&b=3 → 200 OK, result=5.0")
    void testAddEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "2").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("add"))
                .andExpect(jsonPath("$.a").value(2.0))
                .andExpect(jsonPath("$.b").value(3.0))
                .andExpect(jsonPath("$.result").value(5.0));
    }

    @Test
    @DisplayName("Integration: GET /api/v1/add with negative numbers → 200 OK")
    void testAddNegativeNumbers() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "-5").param("b", "3"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.result").value(-2.0));
    }

    // ─── SUBTRACT Integration Tests ───────────────────────────────────────────

    @Test
    @DisplayName("Integration: GET /api/v1/subtract?a=10&b=4 → result=6.0")
    void testSubtractEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/subtract").param("a", "10").param("b", "4"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("subtract"))
                .andExpect(jsonPath("$.result").value(6.0));
    }

    // ─── MULTIPLY Integration Tests ───────────────────────────────────────────

    @Test
    @DisplayName("Integration: GET /api/v1/multiply?a=4&b=5 → result=20.0")
    void testMultiplyEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/multiply").param("a", "4").param("b", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("multiply"))
                .andExpect(jsonPath("$.result").value(20.0));
    }

    // ─── DIVIDE Integration Tests ─────────────────────────────────────────────

    @Test
    @DisplayName("Integration: GET /api/v1/divide?a=10&b=2 → result=5.0")
    void testDivideEndpoint() throws Exception {
        mockMvc.perform(get("/api/v1/divide").param("a", "10").param("b", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.operation").value("divide"))
                .andExpect(jsonPath("$.result").value(5.0));
    }

    @Test
    @DisplayName("Integration: GET /api/v1/divide?a=10&b=0 → 400 Bad Request with error")
    void testDivideByZeroReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/divide").param("a", "10").param("b", "0"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Division by zero is not allowed"));
    }

    @Test
    @DisplayName("Integration: GET /api/v1/add missing param → 400 Bad Request")
    void testMissingParameterReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "5"))
                // b is missing → should return 400
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Integration: GET /api/v1/add invalid param type → 400 Bad Request")
    void testInvalidParameterTypeReturns400() throws Exception {
        mockMvc.perform(get("/api/v1/add").param("a", "hello").param("b", "3"))
                .andExpect(status().isBadRequest());
    }

    // ─── Actuator Health Check ────────────────────────────────────────────────

    @Test
    @DisplayName("Integration: GET /actuator/health → 200 OK, status=UP")
    void testActuatorHealthEndpoint() throws Exception {
        mockMvc.perform(get("/actuator/health"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("UP"));
    }
}
