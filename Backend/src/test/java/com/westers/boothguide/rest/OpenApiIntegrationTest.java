package com.westers.boothguide.rest;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@TestPropertySource(properties = {
        "springdoc.api-docs.enabled=true",
        "springdoc.swagger-ui.enabled=true",
        "springdoc.secure=false"
})
class OpenApiIntegrationTest extends AbstractRestTest {

    @Test
    void exposesOpenApiDocument() {
        var response = restTemplate.getForEntity(generateUrl("/v3/api-docs"), String.class);

        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertTrue(response.getBody().contains("\"openapi\""));
        assertTrue(response.getBody().contains("\"paths\""));
    }
}
