package com.akillibutce;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class AkilliButceApplication {

    public static void main(String[] args) {
        SpringApplication.run(AkilliButceApplication.class, args);
    }
}
