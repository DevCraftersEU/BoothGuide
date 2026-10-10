package com.westers.boothguide;

import org.junit.jupiter.api.Test;
import org.springframework.core.env.MapPropertySource;
import org.springframework.core.env.StandardEnvironment;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ApplicationPropertiesTest {

    @Test
    void defaultDesignEnableUsesDedicatedEnvironmentVariable() throws IOException {
        var properties = new Properties();
        try (InputStream input = getClass().getResourceAsStream("/application.properties")) {
            assertNotNull(input);
            properties.load(input);
        }

        var environment = new StandardEnvironment();
        environment.getPropertySources().addFirst(new MapPropertySource("test", Map.of(
                "DEFAULT_DESIGN_ENABLE", "true",
                "DEFAULT_DESIGN_BACKGROUND", "#F7F9FF"
        )));

        var resolvedValue = environment.resolveRequiredPlaceholders(
                properties.getProperty("design.default.enable")
        );

        assertEquals("true", resolvedValue);
    }
}
