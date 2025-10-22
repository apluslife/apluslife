package com.apluslife;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class AplusLifeApplication {

    public static void main(String[] args) {
        SpringApplication.run(AplusLifeApplication.class, args);
    }
}
