package com.westers.boothguide.rest;


import com.westers.boothguide.model.ApplicationUser;
import com.westers.boothguide.model.dto.ApplicationUserDTO;
import com.westers.boothguide.services.ApplicationUserService;
import com.westers.boothguide.services.ValidationService;
import com.westers.boothguide.services.impl.ApplicationUserServiceImpl;
import lombok.Getter;
import org.passay.MessageResolver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
public class UserController extends AbstractRestController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);

    private final ApplicationUserService applicationUserService;
    private final ValidationService validationService;
    private final MessageResolver messageResolver;

    public UserController(ApplicationUserService applicationUserService, ValidationService validationService, MessageResolver messageResolver) {
        this.applicationUserService = applicationUserService;
        this.validationService = validationService;
        this.messageResolver = messageResolver;
    }

    @GetMapping("/auth/users")
    public List<ApplicationUserDTO> getUsers() {
        return applicationUserService.getUsers().stream().map(applicationUser -> ApplicationUserDTO.from(applicationUser, true)).toList();
    }

    @PutMapping("/auth/users")
    public ResponseEntity<ApplicationUserDTO> updateUser(@RequestBody ApplicationUserDTO applicationUser, Authentication authentication) {
        logger.info("{} requested update of user: {} (ID: {})", authentication.getName(), applicationUser.getUsername(), applicationUser.getId());
        if (!applicationUser.getRoles().contains(ApplicationUser.Role.ADMIN))
            checkIsNotLastAdminUser(applicationUser.getId());
        if (applicationUser.getPassword() != null && !applicationUser.getPassword().isBlank())
            checkPassword(applicationUser.getUsername(), applicationUser.getPassword());
        var updatedUser = applicationUserService.update(ApplicationUser.fromDTO(applicationUser));
        logger.info("User: {} (ID: {}) was updated by {}", updatedUser.getUsername(), updatedUser.getId(), authentication.getName());
        return ResponseEntity.ok(ApplicationUserDTO.from(updatedUser, true));
    }

    @PostMapping("/auth/users")
    public ResponseEntity<ApplicationUserDTO> createUser(@RequestBody ApplicationUserDTO applicationUser, Authentication authentication) {
        logger.info("{} requested creation of user: {}", authentication.getName(), applicationUser.getUsername());
        if (applicationUser.getId() != null)
            throw new AbstractRestController.WrongMethodException();
        checkPassword(applicationUser.getUsername(), applicationUser.getPassword());
        var createdUser = applicationUserService.create(applicationUser.getUsername(), applicationUser.getPassword(), applicationUser.getRoles());
        logger.info("User: {} (ID: {}) was created by {}", applicationUser.getUsername(), applicationUser.getId(), authentication.getName());
        return ResponseEntity.ok(ApplicationUserDTO.from(createdUser, true));
    }

    @DeleteMapping("/auth/users/{id}")
    public ResponseEntity<Void> deleteUser(@PathVariable("id") Long id, Authentication authentication) {
        logger.info("{} requested deletion of user with id: {}", authentication.getName(), id);
        checkIsNotLastAdminUser(id);
        applicationUserService.deleteUser(id);
        logger.info("User with ID {} was deleted by {}", id, authentication.getName());
        return ResponseEntity.noContent().build();
    }


    private void checkIsNotLastAdminUser(Long id) {
        //Checking if at least one other Admin User exists. Otherwise, the user would lock himself out of any admin functionality
        if (applicationUserService.getUsers().stream().noneMatch(applicationUser1 -> !applicationUser1.getId().equals(id) && applicationUser1.getRoles().contains(ApplicationUser.Role.ADMIN))) {
            throw new NoMoreAdminUsersExistsException();
        }
    }

    private void checkPassword(String username, String password) {
        var result = validationService.isValidPassword(username, password);
        if (!result.isValid()) {
            var exception = new InvalidPasswordException();
            result.getDetails().forEach(detail -> exception.addMessage(messageResolver.resolve(detail)));
            throw exception;
        }
    }

    @ExceptionHandler(NoMoreAdminUsersExistsException.class)
    public ResponseEntity<String> handleNoMoreAdminUsersExistsException() {
        logger.info("Returning last admin can not be removed answer");
        return ResponseEntity.badRequest().body("You tried to remove the last admin user! This is not possible.");
    }

    @ExceptionHandler(ApplicationUserServiceImpl.UsernameExistsException.class)
    public ResponseEntity<String> handleUsernameExistsException() {
        logger.info("Returning username already exist answer");
        return ResponseEntity.badRequest().body("Username already exists!");
    }

    @ExceptionHandler(InvalidPasswordException.class)
    private ResponseEntity<String> handleInvalidPasswordException(InvalidPasswordException e) {
        logger.info("Returning invalid password answer");
        String answer = "The following error(s) occurred:<br />" + String.join("<br />", e.getMessages());
        return ResponseEntity.badRequest().body(answer);
    }

    private static class NoMoreAdminUsersExistsException extends RuntimeException {
    }

    @Getter
    private static class InvalidPasswordException extends RuntimeException {
        private final List<String> messages = new ArrayList<>();

        public void addMessage(String message) {
            messages.add(message);
        }
    }
}
