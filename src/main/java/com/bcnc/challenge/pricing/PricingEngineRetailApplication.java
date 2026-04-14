package com.bcnc.challenge.pricing;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;

@EnableCaching
@SpringBootApplication
public class PricingEngineRetailApplication {

    public static void main(String[] args) {
        SpringApplication.run(PricingEngineRetailApplication.class, args);
    }

}
