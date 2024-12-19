package ru;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.retry.annotation.CircuitBreaker;

@CircuitBreaker
@EnableFeignClients
@SpringBootApplication
@ConfigurationPropertiesScan
public class EventsServiceApp {
    public static void main(String[] args) {
        SpringApplication.run(EventsServiceApp.class, args);
    }

}
