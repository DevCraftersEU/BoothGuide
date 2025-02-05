package com.westers.boothguide;

import com.westers.boothguide.model.jpa.ApplicationUserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


@SpringBootTest
@ActiveProfiles("test")
class BoothGuideBackendApplicationTests {

    @Value("${admin.username}")
    private String username;
    @Value("${admin.password}")
    private String password;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private ApplicationUserRepository applicationUserRepository;

    @Test
    void contextLoads() {
    }

    @Test
    void defaultUserIsGenerated() {
        assertEquals(1, applicationUserRepository.count(), "After starting a new instance, the count of users must be 1");
        var persistedApplicationUser = applicationUserRepository.findAll().getFirst();
        assertEquals(persistedApplicationUser.getUsername(), username, "The persisted user does not have the correct username");
        assertTrue(passwordEncoder.matches(password, persistedApplicationUser.getPassword()), "The password does not match");
    }

}
