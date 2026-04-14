package com.bcnc.challenge.pricing;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.test.context.SpringBootTest;

import static org.mockito.Mockito.mockStatic;

@SpringBootTest
class PricingEngineRetailApplicationTest {

    @Test
    void contextLoads() {
    }

    @Test
    void shouldRunSpringApplicationMain() {
        try (MockedStatic<SpringApplication> springApplicationMock = mockStatic(SpringApplication.class)) {
            PricingEngineRetailApplication.main(new String[]{});

            springApplicationMock.verify(
                    () -> SpringApplication.run(PricingEngineRetailApplication.class, new String[]{})
            );
        }
    }
}