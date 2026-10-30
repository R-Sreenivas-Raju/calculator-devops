package com.spe.calculator.model;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * Response model returned by all calculator endpoints.
 *
 * Example success response:
 *   { "operation": "add", "a": 2.0, "b": 3.0, "result": 5.0 }
 *
 * Example error response:
 *   { "error": "Division by zero is not allowed" }
 *
 * @JsonInclude(NON_NULL) means null fields are omitted from JSON output,
 * so success responses don't show "error: null" and vice versa.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CalculatorResponse {

    private String operation;
    private Double a;
    private Double b;
    private Double result;
    private String error;

    // ─── Constructors ─────────────────────────────────────────────────────────

    public CalculatorResponse() {}

    /** Success constructor */
    public CalculatorResponse(String operation, double a, double b, double result) {
        this.operation = operation;
        this.a = a;
        this.b = b;
        this.result = result;
    }

    /** Error constructor */
    public CalculatorResponse(String error) {
        this.error = error;
    }

    // ─── Getters & Setters ────────────────────────────────────────────────────

    public String getOperation() { return operation; }
    public void setOperation(String operation) { this.operation = operation; }

    public Double getA() { return a; }
    public void setA(Double a) { this.a = a; }

    public Double getB() { return b; }
    public void setB(Double b) { this.b = b; }

    public Double getResult() { return result; }
    public void setResult(Double result) { this.result = result; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }
}
