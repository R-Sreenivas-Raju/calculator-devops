package com.spe.calculator.service;

import com.spe.calculator.exception.DivisionByZeroException;
import org.springframework.stereotype.Service;

/**
 * CalculatorService contains the core business logic.
 *
 * @Service tells Spring to manage this class as a bean (singleton by default).
 * The Controller will inject this service and call its methods.
 *
 * Separation of concerns: Controller handles HTTP, Service handles logic.
 * This is the standard Spring MVC pattern (good for marks!).
 */
@Service
public class CalculatorService {

    /**
     * Adds two numbers.
     * @param a first operand
     * @param b second operand
     * @return a + b
     */
    public double add(double a, double b) {
        return a + b;
    }

    /**
     * Subtracts b from a.
     * @param a first operand
     * @param b second operand
     * @return a - b
     */
    public double subtract(double a, double b) {
        return a - b;
    }

    /**
     * Multiplies two numbers.
     * @param a first operand
     * @param b second operand
     * @return a * b
     */
    public double multiply(double a, double b) {
        return a * b;
    }

    /**
     * Divides a by b.
     * @param a numerator
     * @param b denominator — must not be zero
     * @return a / b
     * @throws DivisionByZeroException if b == 0
     */
    public double divide(double a, double b) {
        if (b == 0) {
            throw new DivisionByZeroException("Division by zero is not allowed");
        }
        return a / b;
    }
}
