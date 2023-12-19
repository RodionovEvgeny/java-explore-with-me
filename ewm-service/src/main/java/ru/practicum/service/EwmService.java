package ru.practicum.service;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
//@ComponentScan(basePackages = {"ru.practicum"})
public class EwmService {

    public static void main(String[] args) {
        SpringApplication.run(EwmService.class, args);
    }
}
