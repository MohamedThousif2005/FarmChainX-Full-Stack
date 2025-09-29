package com.farmchainx.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.farmchainx.backend.repository")
public class FarmChainXBackendApplication {
    public static void main(String[] args) {
        SpringApplication.run(FarmChainXBackendApplication.class, args);
    }
}