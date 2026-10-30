package com.spe.calculator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Main entry point for the Calculator REST API.
 *
 * @SpringBootApplication combines:
 *   - @Configuration: marks this as a Spring config class
 *   - @EnableAutoConfiguration: auto-configures Spring beans
 *   - @ComponentScan: scans for components in this package and sub-packages
 */
@SpringBootApplication
public class CalculatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(CalculatorApplication.class, args);
    }
}
