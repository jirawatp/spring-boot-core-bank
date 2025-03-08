package com.pattanayutanachot.jirawat.core.bank.config;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@WebMvcTest(SwaggerConfig.class)
@ActiveProfiles("test")
class SwaggerConfigTest {
    @Test
    void testOpenAPIBean() {
        assertNotNull(new SwaggerConfig().openAPI());
    }
}