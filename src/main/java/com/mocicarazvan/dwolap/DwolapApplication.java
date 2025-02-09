package com.mocicarazvan.dwolap;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class DwolapApplication {

    public static void main(String[] args) {
        SpringApplication.run(DwolapApplication.class, args);
    }
    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }
}
