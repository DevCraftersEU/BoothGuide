package com.westers.boothguide.model.jpa;

import com.westers.boothguide.model.ApplicationUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.lang.NonNull;

import java.util.List;
import java.util.Optional;

public interface ApplicationUserRepository extends CrudRepository<ApplicationUser, Long> {
    Optional<ApplicationUser> getApplicationUserByUsername(String username);

    @NonNull
    List<ApplicationUser> findAll();

    Optional<ApplicationUser> findByUsernameIgnoreCase(@NonNull String username);

    boolean existsByUsernameIgnoreCaseAndIdNot(String username, Long id);

    boolean existsById(@NonNull Long id);
}
