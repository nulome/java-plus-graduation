package ru;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;

@CircuitBreaker(name = "compilations-server")
@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan
public class CompilationServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(CompilationServiceApp.class, args);
    }

}
