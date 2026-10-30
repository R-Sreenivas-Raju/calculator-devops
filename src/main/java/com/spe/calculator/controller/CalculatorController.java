package com.spe.calculator.controller;

import com.spe.calculator.exception.DivisionByZeroException;
import com.spe.calculator.model.CalculatorResponse;
import com.spe.calculator.service.CalculatorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

/**
 * REST Controller for Calculator operations.
 *
 * @RestController = @Controller + @ResponseBody (auto-converts return values to JSON)
 * @RequestMapping("/api/v1") sets the base path for all endpoints in this class.
 *
 * All endpoints return ResponseEntity<CalculatorResponse> so we can control
 * both the HTTP status code and the JSON body.
 */
@RestController
@RequestMapping("/api/v1")
public class CalculatorController {

    // @Autowired injects the CalculatorService bean managed by Spring
    @Autowired
    private CalculatorService calculatorService;

    /**
     * ADD: GET /api/v1/add?a=2&b=3  →  { "operation": "add", "a": 2.0, "b": 3.0, "result": 5.0 }
     */
    @GetMapping("/add")
    public ResponseEntity<CalculatorResponse> add(
            @RequestParam double a,
            @RequestParam double b) {
        double result = calculatorService.add(a, b);
        return ResponseEntity.ok(new CalculatorResponse("add", a, b, result));
    }

    /**
     * SUBTRACT: GET /api/v1/subtract?a=5&b=3  →  { "operation": "subtract", "a": 5.0, "b": 3.0, "result": 2.0 }
     */
    @GetMapping("/subtract")
    public ResponseEntity<CalculatorResponse> subtract(
            @RequestParam double a,
            @RequestParam double b) {
        double result = calculatorService.subtract(a, b);
        return ResponseEntity.ok(new CalculatorResponse("subtract", a, b, result));
    }

    /**
     * MULTIPLY: GET /api/v1/multiply?a=4&b=5  →  { "operation": "multiply", "a": 4.0, "b": 5.0, "result": 20.0 }
     */
    @GetMapping("/multiply")
    public ResponseEntity<CalculatorResponse> multiply(
            @RequestParam double a,
            @RequestParam double b) {
        double result = calculatorService.multiply(a, b);
        return ResponseEntity.ok(new CalculatorResponse("multiply", a, b, result));
    }

    /**
     * DIVIDE: GET /api/v1/divide?a=10&b=2  →  { "operation": "divide", "a": 10.0, "b": 2.0, "result": 5.0 }
     *         GET /api/v1/divide?a=10&b=0  →  HTTP 400 { "error": "Division by zero is not allowed" }
     */
    @GetMapping("/divide")
    public ResponseEntity<CalculatorResponse> divide(
            @RequestParam double a,
            @RequestParam double b) {
        double result = calculatorService.divide(a, b);
        return ResponseEntity.ok(new CalculatorResponse("divide", a, b, result));
    }

    // ─── Exception Handlers ───────────────────────────────────────────────────

    /**
     * Catches DivisionByZeroException and returns HTTP 400 with an error JSON.
     * @ExceptionHandler means Spring automatically calls this when the exception is thrown.
     */
    @ExceptionHandler(DivisionByZeroException.class)
    public ResponseEntity<CalculatorResponse> handleDivisionByZero(DivisionByZeroException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CalculatorResponse(ex.getMessage()));
    }

    /**
     * Catches cases where a or b are not valid numbers.
     * E.g.: GET /api/v1/add?a=hello&b=3  →  HTTP 400
     */
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<CalculatorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CalculatorResponse("Invalid parameter '" + ex.getName() + "': must be a valid number"));
    }

    /**
     * Catches missing required @RequestParam (Spring throws MissingServletRequestParameterException).
     */
    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<CalculatorResponse> handleMissingParam(MissingServletRequestParameterException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new CalculatorResponse("Missing required parameter: " + ex.getParameterName()));
    }
}
