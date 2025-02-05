package com.westers.boothguide.security;

import com.westers.boothguide.model.ApplicationUser;
import com.westers.boothguide.model.jpa.ApplicationUserRepository;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Objects;

@Configuration
@RequiredArgsConstructor
public class SetupController {
    private final static Logger logger = LoggerFactory.getLogger(SetupController.class);

    @Value("${admin.username}")
    private String username;
    @Value("${admin.password:}")
    private String password;

    private final ApplicationUserRepository userRepo;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void setup() {
        logger.debug("Checking for existing users");
        if (userRepo.count() > 0) {
            logger.debug("Existing users found, nothing to do!");
            return;
        }
        logger.info("No existing users found. Generating default one.");
        var usernameToUse = (username != null && !username.isBlank()) ? username : "admin";
        var passwordToUse = password;
        if (passwordToUse == null || passwordToUse.isBlank()) {
            passwordToUse = new PasswordGenerator().generatePassword(12, List.of(
                    new CharacterRule(EnglishCharacterData.LowerCase),
                    new CharacterRule(EnglishCharacterData.UpperCase),
                    new CharacterRule(EnglishCharacterData.Digit)));
            logger.info("No default password generating one");
            logger.info("The default password is {}", passwordToUse);
            logger.warn("The default password should be changed after login!");
        }
        var defUser = new ApplicationUser(null, usernameToUse, passwordEncoder.encode(passwordToUse), List.of(ApplicationUser.Role.ADMIN, ApplicationUser.Role.DESIGNER, ApplicationUser.Role.MODERATOR));
        logger.info("Creating new user {}", defUser.getUsername());
        userRepo.save(defUser);
        logger.info("Default user created: {}", userRepo.getApplicationUserByUsername(usernameToUse).map(Objects::toString).orElse("UNKNOWN"));
    }

}
