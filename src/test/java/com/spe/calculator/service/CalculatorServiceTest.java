package com.spe.calculator.service;

import com.spe.calculator.exception.DivisionByZeroException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit Tests for CalculatorService using JUnit 5.
 *
 * These are PURE unit tests — no Spring context needed,
 * no HTTP calls. We just instantiate the service and test its methods directly.
 *
 * @DisplayName gives human-readable names in the Jenkins test report.
 */
class CalculatorServiceTest {

    private CalculatorService calculatorService;

    // @BeforeEach runs before every single test method
    @BeforeEach
    void setUp() {
        calculatorService = new CalculatorService();
    }

    // ─── ADD TESTS ────────────────────────────────────────────────────────────

    @Test
    @DisplayName("Add: 2 + 3 should return 5")
    void testAddPositiveNumbers() {
        assertEquals(5.0, calculatorService.add(2, 3), 0.0001);
    }

    @Test
    @DisplayName("Add: -5 + 3 should return -2")
    void testAddWithNegativeNumbers() {
        assertEquals(-2.0, calculatorService.add(-5, 3), 0.0001);
    }

    @Test
    @DisplayName("Add: 0 + 0 should return 0")
    void testAddZeros() {
        assertEquals(0.0, calculatorService.add(0, 0), 0.0001);
    }

    @Test
    @DisplayName("Add: decimal numbers 1.5 + 2.5 should return 4.0")
    void testAddDecimalNumbers() {
        assertEquals(4.0, calculatorService.add(1.5, 2.5), 0.0001);
    }

    // ─── SUBTRACT TESTS ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Subtract: 10 - 4 should return 6")
    void testSubtractPositiveNumbers() {
        assertEquals(6.0, calculatorService.subtract(10, 4), 0.0001);
    }

    @Test
    @DisplayName("Subtract: 3 - 7 should return -4")
    void testSubtractResultNegative() {
        assertEquals(-4.0, calculatorService.subtract(3, 7), 0.0001);
    }

    @Test
    @DisplayName("Subtract: 5 - 0 should return 5")
    void testSubtractZero() {
        assertEquals(5.0, calculatorService.subtract(5, 0), 0.0001);
    }

    // ─── MULTIPLY TESTS ───────────────────────────────────────────────────────

    @Test
    @DisplayName("Multiply: 4 * 5 should return 20")
    void testMultiplyPositiveNumbers() {
        assertEquals(20.0, calculatorService.multiply(4, 5), 0.0001);
    }

    @Test
    @DisplayName("Multiply: -3 * 4 should return -12")
    void testMultiplyNegativeNumbers() {
        assertEquals(-12.0, calculatorService.multiply(-3, 4), 0.0001);
    }

    @Test
    @DisplayName("Multiply: any number * 0 should return 0")
    void testMultiplyByZero() {
        assertEquals(0.0, calculatorService.multiply(99, 0), 0.0001);
    }

    @Test
    @DisplayName("Multiply: 2.5 * 4 should return 10.0")
    void testMultiplyDecimal() {
        assertEquals(10.0, calculatorService.multiply(2.5, 4), 0.0001);
    }

    // ─── DIVIDE TESTS ─────────────────────────────────────────────────────────

    @Test
    @DisplayName("Divide: 10 / 2 should return 5")
    void testDividePositiveNumbers() {
        assertEquals(5.0, calculatorService.divide(10, 2), 0.0001);
    }

    @Test
    @DisplayName("Divide: 7 / 2 should return 3.5")
    void testDivideDecimalResult() {
        assertEquals(3.5, calculatorService.divide(7, 2), 0.0001);
    }

    @Test
    @DisplayName("Divide: -10 / 5 should return -2")
    void testDivideNegativeNumbers() {
        assertEquals(-2.0, calculatorService.divide(-10, 5), 0.0001);
    }

    @Test
    @DisplayName("Divide by zero: should throw DivisionByZeroException")
    void testDivideByZeroThrowsException() {
        // assertThrows verifies that the expected exception IS thrown
        DivisionByZeroException exception = assertThrows(
                DivisionByZeroException.class,
                () -> calculatorService.divide(10, 0)
        );
        assertEquals("Division by zero is not allowed", exception.getMessage());
    }

    @Test
    @DisplayName("Divide by zero: 0/0 should also throw DivisionByZeroException")
    void testZeroDivideByZeroThrowsException() {
        assertThrows(
                DivisionByZeroException.class,
                () -> calculatorService.divide(0, 0)
        );
    }
}
