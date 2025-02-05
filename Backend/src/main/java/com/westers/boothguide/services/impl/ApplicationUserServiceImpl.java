package com.westers.boothguide.services.impl;

import com.westers.boothguide.model.ApplicationUser;
import com.westers.boothguide.model.jpa.ApplicationUserRepository;
import com.westers.boothguide.services.ApplicationUserService;
import com.westers.boothguide.services.impl.exceptions.ApplicationEntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ApplicationUserServiceImpl implements ApplicationUserService {

    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;


    @Override
    public ApplicationUser create(String username, String password, Collection<ApplicationUser.Role> roles) throws UsernameExistsException {
        checkUsernameAlreadyExists(username);
        return applicationUserRepository.save(new ApplicationUser(null, username, passwordEncoder.encode(password), roles));
    }

    @Override
    public ApplicationUser update(ApplicationUser applicationUser) throws ApplicationEntityNotFoundException {
        var persistentUser = getExistentUser(applicationUser.getId());
        checkIdExists(persistentUser.getId());
        checkUsernameAlreadyExists(applicationUser.getUsername(), persistentUser.getId());
        persistentUser.setUsername(applicationUser.getUsername());
        persistentUser.setRoles(applicationUser.getRoles());
        if (applicationUser.getPassword() != null && !applicationUser.getPassword().isBlank())
            persistentUser.setPassword(passwordEncoder.encode(applicationUser.getPassword()));
        return applicationUserRepository.save(persistentUser);
    }

    @Override
    public List<ApplicationUser> getUsers() {
        return applicationUserRepository.findAll();
    }

    @Override
    public void deleteUser(Long id) throws ApplicationEntityNotFoundException {
        checkIdExists(id);
        applicationUserRepository.deleteById(id);
    }

    @Override
    public Optional<ApplicationUser> getUser(Long id) {
        return applicationUserRepository.findById(id);
    }

    @Override
    public ApplicationUser getExistentUser(Long id) throws ApplicationEntityNotFoundException {
        if (id == null)
            throw new ApplicationEntityNotFoundException();
        return applicationUserRepository.findById(id).orElseThrow(ApplicationEntityNotFoundException::new);
    }

    private void checkIdExists(Long id) throws ApplicationEntityNotFoundException {
        if (id == null || !applicationUserRepository.existsById(id))
            throw new ApplicationEntityNotFoundException();
    }

    private void checkUsernameAlreadyExists(String username) {
        if (username == null || username.isBlank() || applicationUserRepository.findByUsernameIgnoreCase(username).isPresent())
            throw new UsernameExistsException();
    }

    private void checkUsernameAlreadyExists(String username, Long idToIgnore) {
        if (username == null || username.isBlank() || applicationUserRepository.existsByUsernameIgnoreCaseAndIdNot(username, idToIgnore))
            throw new UsernameExistsException();
    }


}
