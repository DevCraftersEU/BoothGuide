package com.westers.boothguide.rest;

import com.westers.boothguide.model.ApplicationUser;
import com.westers.boothguide.model.dto.ApplicationUserDTO;
import com.westers.boothguide.model.jpa.ApplicationUserRepository;
import org.assertj.core.util.Lists;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.passay.CharacterRule;
import org.passay.EnglishCharacterData;
import org.passay.PasswordGenerator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class UserControllerTest extends AbstractRestTest {

    private static final String USERS_PATH = "/auth/users";
    private static final String CHECK_AUTH_PATH = "/auth/checkAuth";

    //German "Umlauts" like äöü do make problems when used within the rest template. Therefore, we only use english here
    private static final CharacterRule lowerCharRule = new CharacterRule(EnglishCharacterData.LowerCase, 1);
    private static final CharacterRule upperCharRule = new CharacterRule(EnglishCharacterData.UpperCase, 1);
    private static final CharacterRule digitCharRule = new CharacterRule(EnglishCharacterData.Digit, 1);
    private static final CharacterRule specialCharRule = new CharacterRule(EnglishCharacterData.Special, 1);
    private final PasswordGenerator passwordGenerator = new PasswordGenerator();

    @Autowired
    private ApplicationUserRepository applicationUserRepository;
    @Value("${admin.username}")
    private String adminUsername;

    @BeforeEach
    public void cleanUp() {
        var delUser = applicationUserRepository.findAll().stream().filter(e -> !e.getUsername().equals(adminUsername)).toList();
        applicationUserRepository.deleteAll(delUser);
    }

    @Test
    public void testAuth() {
        var unauthorizedRequest = restTemplate.getForEntity(generateUrl(CHECK_AUTH_PATH), String[].class);
        assertEquals(HttpStatus.UNAUTHORIZED, unauthorizedRequest.getStatusCode());
        var authorizedRequest = authRestTemplate().getForEntity(generateUrl(CHECK_AUTH_PATH), String[].class);
        assertEquals(HttpStatus.OK, authorizedRequest.getStatusCode());
        assertTrue(Arrays.asList(Objects.requireNonNull(authorizedRequest.getBody())).contains("ROLE_ADMIN"));
    }

    @Test
    public void testCreateUser() {
        var moderator = generateNewUser(ApplicationUser.Role.MODERATOR);
        var response = authRestTemplate().postForEntity(generateUrl(USERS_PATH), moderator, ApplicationUserDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getId() > -1);
        var response2 = restTemplate.withBasicAuth(moderator.getUsername(), moderator.getPassword()).getForEntity(CHECK_AUTH_PATH, String[].class);
        assertEquals(HttpStatus.OK, response2.getStatusCode());
        assertTrue(Arrays.asList(Objects.requireNonNull(response2.getBody())).contains("ROLE_MODERATOR") && response.getBody().getRoles().size() == 1);
        authRestTemplate().postForEntity(generateUrl(USERS_PATH), generateNewUser(ApplicationUser.Role.DESIGNER), ApplicationUserDTO.class);
        authRestTemplate().postForEntity(generateUrl(USERS_PATH), generateNewUser(ApplicationUser.Role.ADMIN), ApplicationUserDTO.class);
        authRestTemplate().postForEntity(generateUrl(USERS_PATH), generateNewUser(), ApplicationUserDTO.class);
        var newUsersRequest = authRestTemplate().getForEntity(USERS_PATH, ApplicationUserDTO[].class);
        assertEquals(HttpStatus.OK, newUsersRequest.getStatusCode());
        assertEquals(5, Objects.requireNonNull(newUsersRequest.getBody()).length);
    }

    @Test
    public void testUpdateUser() {
        var user = generateNewUser(ApplicationUser.Role.DESIGNER);
        var originalUsername = user.getUsername();
        var originalPass = user.getPassword();
        var createResponse = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user, ApplicationUserDTO.class);
        assertEquals(HttpStatus.OK, createResponse.getStatusCode());
        user = createResponse.getBody();
        assertNotNull(user);
        assertNull(user.getPassword());
        String newUsername = UUID.randomUUID().toString();
        user.setUsername(newUsername);

        var putRequest = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(user), ApplicationUserDTO.class);
        assertEquals(HttpStatus.OK, putRequest.getStatusCode());
        assertEquals(newUsername, Objects.requireNonNull(putRequest.getBody()).getUsername());
        assertEquals(user.getId(), Objects.requireNonNull(putRequest.getBody()).getId());

        var newCountRequest = authRestTemplate().getForEntity(generateUrl(USERS_PATH), ApplicationUserDTO[].class);
        assertEquals(HttpStatus.OK, newCountRequest.getStatusCode());
        assertEquals(2, Objects.requireNonNull(newCountRequest.getBody()).length);

        var newCheckAuthRequest = restTemplate.withBasicAuth(user.getUsername(), originalPass).getForEntity(generateUrl(CHECK_AUTH_PATH), String[].class);
        assertEquals(HttpStatus.OK, newCheckAuthRequest.getStatusCode());
        assertEquals("ROLE_DESIGNER", Objects.requireNonNull(newCheckAuthRequest.getBody())[0]);
        assertEquals(1, newCheckAuthRequest.getBody().length);

        var wrongCheckAuthRequest = restTemplate.withBasicAuth(originalUsername, originalPass).getForEntity(generateUrl(CHECK_AUTH_PATH), String.class);
        assertEquals(HttpStatus.UNAUTHORIZED, wrongCheckAuthRequest.getStatusCode());

        var newPassword = passwordGenerator.generatePassword(15, List.of(upperCharRule, lowerCharRule, digitCharRule));
        user.setPassword(newPassword);

        putRequest = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(user), ApplicationUserDTO.class);
        assertEquals(HttpStatus.OK, putRequest.getStatusCode());
        assertEquals(newUsername, Objects.requireNonNull(putRequest.getBody()).getUsername());
        assertEquals(user.getId(), Objects.requireNonNull(putRequest.getBody()).getId());
        assertNull(putRequest.getBody().getPassword());

        newCheckAuthRequest = restTemplate.withBasicAuth(newUsername, newPassword).getForEntity(generateUrl(CHECK_AUTH_PATH), String[].class);
        assertEquals(HttpStatus.OK, newCheckAuthRequest.getStatusCode());
        assertEquals("ROLE_DESIGNER", Objects.requireNonNull(newCheckAuthRequest.getBody())[0]);
        assertEquals(1, newCheckAuthRequest.getBody().length);
    }

    @Test
    public void testDeleteUser() {
        var user1 = generateNewUser(ApplicationUser.Role.DESIGNER);
        var user2 = generateNewUser(ApplicationUser.Role.MODERATOR);
        var user3 = generateNewUser(ApplicationUser.Role.ADMIN);
        for (var user : List.of(user1, user2, user3)) {
            var res = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user, ApplicationUserDTO.class);
            assertEquals(HttpStatus.OK, res.getStatusCode());
            user.setId(Objects.requireNonNull(res.getBody()).getId());
        }
        var countCheck = authRestTemplate().getForEntity(generateUrl(USERS_PATH), ApplicationUserDTO[].class);
        assertEquals(HttpStatus.OK, countCheck.getStatusCode());
        assertEquals(4, Objects.requireNonNull(countCheck.getBody()).length);
        for (var user : List.of(user1, user2, user3)) {
            var res = authRestTemplate().exchange(generateUrl(USERS_PATH + "/" + user.getId()), HttpMethod.DELETE, HttpEntity.EMPTY, Void.class);
            assertEquals(HttpStatus.NO_CONTENT, res.getStatusCode());
        }
        countCheck = authRestTemplate().getForEntity(generateUrl(USERS_PATH), ApplicationUserDTO[].class);
        assertEquals(HttpStatus.OK, countCheck.getStatusCode());
        assertEquals(1, Objects.requireNonNull(countCheck.getBody()).length);
    }

    @Test
    public void testCreateInvalidUser() {
        var user = generateNewUser(ApplicationUser.Role.DESIGNER);
        user.setUsername("");
        var res = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());

        user.setUsername("      ");
        res = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());

        user.setId(1L);
        user.setUsername(UUID.randomUUID().toString());
        res = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());

        user.setId(null);
        res = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user, Void.class);
        assertEquals(HttpStatus.OK, res.getStatusCode());
        res = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user, Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void testUpdateInvalidUser() {
        var user = generateNewUser(ApplicationUser.Role.DESIGNER);
        var res = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user, ApplicationUserDTO.class);
        user = res.getBody();
        assertNotNull(user);

        user.setUsername("   ");
        var invalidRes = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(user), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, invalidRes.getStatusCode());

        user.setUsername("");
        invalidRes = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(user), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, invalidRes.getStatusCode());

        user.setUsername(UUID.randomUUID().toString());
        user.setId(103030404L);
        invalidRes = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(user), Void.class);
        assertEquals(HttpStatus.NOT_FOUND, invalidRes.getStatusCode());

        user.setId(null);
        invalidRes = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(user), Void.class);
        assertEquals(HttpStatus.NOT_FOUND, invalidRes.getStatusCode());

        var user2 = generateNewUser(ApplicationUser.Role.ADMIN);
        invalidRes = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(user2), Void.class);
        assertEquals(HttpStatus.NOT_FOUND, invalidRes.getStatusCode());

        user2.setId(17L);
        invalidRes = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(user2), Void.class);
        assertEquals(HttpStatus.NOT_FOUND, invalidRes.getStatusCode());

        user2.setId(null);
        var creationProcess = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user2, ApplicationUserDTO.class);
        assertEquals(HttpStatus.OK, creationProcess.getStatusCode());

        var user3 = generateNewUser(ApplicationUser.Role.MODERATOR);
        creationProcess = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user3, ApplicationUserDTO.class);
        user3 = creationProcess.getBody();
        Objects.requireNonNull(user3).setUsername(user2.getUsername());
        invalidRes = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(user3), Void.class);
        assertEquals(HttpStatus.BAD_REQUEST, invalidRes.getStatusCode());
    }

    @Test
    public void testRemoveLastAdminUser() {
        var countCheck = authRestTemplate().getForEntity(generateUrl(USERS_PATH), ApplicationUserDTO[].class);
        assertEquals(HttpStatus.OK, countCheck.getStatusCode());
        assertEquals(1, Objects.requireNonNull(countCheck.getBody()).length);
        var onlyUser = countCheck.getBody()[0];
        var res = authRestTemplate().exchange(generateUrl(USERS_PATH + "/" + onlyUser.getId()), HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
        onlyUser.setRoles(List.of(ApplicationUser.Role.MODERATOR));
        res = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(onlyUser), String.class);
        assertEquals(HttpStatus.BAD_REQUEST, res.getStatusCode());
    }

    @Test
    public void testDeleteUpdateNotExistingUser() {
        var res = authRestTemplate().exchange(generateUrl(USERS_PATH + "/100"), HttpMethod.DELETE, HttpEntity.EMPTY, String.class);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
        var notExistingUser = generateNewUser(ApplicationUser.Role.DESIGNER);
        notExistingUser.setId(100L);
        res = authRestTemplate().exchange(generateUrl(USERS_PATH), HttpMethod.PUT, new HttpEntity<>(notExistingUser), String.class);
        assertEquals(HttpStatus.NOT_FOUND, res.getStatusCode());
    }

    @Test
    public void testValidPassword() {
        testCreateUserWithRules(8, List.of(lowerCharRule, upperCharRule, digitCharRule));
        testCreateUserWithRules(9, List.of(lowerCharRule, upperCharRule, specialCharRule));
        testCreateUserWithRules(10, List.of(lowerCharRule, digitCharRule, specialCharRule));
        testCreateUserWithRules(11, List.of(upperCharRule, digitCharRule, specialCharRule));
        testCreateUserWithRules(12, List.of(upperCharRule, digitCharRule, specialCharRule));
        testCreateUserWithRules(15, List.of(lowerCharRule, upperCharRule));
        testCreateUserWithRules(16, List.of(lowerCharRule, digitCharRule));
        testCreateUserWithRules(17, List.of(lowerCharRule, specialCharRule));
        testCreateUserWithRules(18, List.of(upperCharRule, digitCharRule));
        testCreateUserWithRules(19, List.of(upperCharRule, specialCharRule));
        testCreateUserWithRules(20, List.of(digitCharRule, specialCharRule));
    }

    private void testCreateUserWithRules(int length, List<CharacterRule> rules) {
        var user = generateNewUser(ApplicationUser.Role.MODERATOR);
        user.setPassword(passwordGenerator.generatePassword(length, rules));
        var response = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user, ApplicationUserDTO.class);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertTrue(Objects.requireNonNull(response.getBody()).getId() >= 0);
    }

    @Test
    public void testInvalidPassword() {
        var invalidPasswords = Lists.list("1234", "aaabbbcccdddeee", "abcdefghijklmnopABCDEFGHIJK", "123456789", "password", "", "                 ");
        var user = generateNewUser(ApplicationUser.Role.DESIGNER);
        invalidPasswords.add(user.getUsername());
        invalidPasswords.add(passwordGenerator.generatePassword(7, List.of(lowerCharRule, upperCharRule, specialCharRule, digitCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(8, List.of(lowerCharRule, upperCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(9, List.of(lowerCharRule, specialCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(10, List.of(lowerCharRule, digitCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(11, List.of(upperCharRule, specialCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(12, List.of(upperCharRule, digitCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(14, List.of(specialCharRule, digitCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(15, List.of(lowerCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(16, List.of(upperCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(17, List.of(specialCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(18, List.of(digitCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(5000, List.of(digitCharRule)));
        invalidPasswords.add(passwordGenerator.generatePassword(128, List.of(digitCharRule, specialCharRule)));

        for (String invalidPassword : invalidPasswords) {
            user.setPassword(invalidPassword);
            var response = authRestTemplate().postForEntity(generateUrl(USERS_PATH), user, String.class);
            assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        }
    }

    private ApplicationUserDTO generateNewUser(ApplicationUser.Role... roles) {
        return new ApplicationUserDTO(null, UUID.randomUUID().toString(), UUID.randomUUID().toString(), Arrays.stream(roles).toList());
    }
}
