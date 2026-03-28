package org.amts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
@DisplayName("Application Context Tests")
class AppTest {

    /**
     * A basic bridge test to ensure that the Spring application context
     * loads correctly without any configuration or dependency issues.
     */
    @Test
    @DisplayName("contextLoads - Verifies Spring Boot context starts successfully")
    void contextLoads() {
    }
}
