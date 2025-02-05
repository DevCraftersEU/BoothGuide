package com.westers.boothguide.rest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class AbstractRestTest {
    @LocalServerPort
    protected int port;

    @Value("${admin.username}")
    private String adminUsername;

    @Value("${admin.password}")
    private String adminPassword;

    @Autowired
    protected TestRestTemplate restTemplate;

    private TestRestTemplate authenticatedRestTemplate;

    private String getBaseUrl() {
        return "http://localhost:" + port;
    }

    protected String generateUrl(String path) {
        return getBaseUrl() + path;
    }

    TestRestTemplate authRestTemplate() {
        if (authenticatedRestTemplate == null)
            authenticatedRestTemplate = restTemplate.withBasicAuth(adminUsername, adminPassword);
        return authenticatedRestTemplate;
    }
}
