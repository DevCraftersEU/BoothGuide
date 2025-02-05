package com.westers.boothguide.services;

import com.westers.boothguide.model.ApplicationUser;
import com.westers.boothguide.services.impl.exceptions.ApplicationEntityNotFoundException;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing application users.
 */
public interface ApplicationUserService {
    /**
     * Creates and saves a new {@link ApplicationUser}
     *
     * @param username The username of the new user
     * @param password The <bold>plain</bold> password of the new user. Do not encode the password. The service will do this!
     * @param roles    Collection of all {@link com.westers.boothguide.model.ApplicationUser.Role}s that the new user should own.
     * @return The persisted {@link ApplicationUser}
     */
    ApplicationUser create(String username, String password, Collection<ApplicationUser.Role> roles) throws UsernameExistsException;

    /**
     * Updates an already existent user
     *
     * @param applicationUser The user to update. It is important, that the user's ID is set. The method will <bold>not</bold> not persist new users!
     * @return The updated {@link ApplicationUser}
     * @throws ApplicationEntityNotFoundException If the user's ID is not set or no user with this ID was found
     */
    ApplicationUser update(ApplicationUser applicationUser) throws ApplicationEntityNotFoundException;

    /**
     * Returns all persisted Users
     *
     * @return List of all persisted Users
     */
    List<ApplicationUser> getUsers();

    /**
     * Deletes the persisted user from the database
     *
     * @param id The ID of the user to delete
     * @throws ApplicationEntityNotFoundException If the ID is not set or no user with this ID was found
     */
    void deleteUser(Long id) throws ApplicationEntityNotFoundException;

    /**
     * Returns the user for a given ID or an empty {@link Optional} if no user exists
     *
     * @param id The ID of the user to find
     * @return {@link ApplicationUser} or empty {@link Optional}
     */
    Optional<ApplicationUser> getUser(Long id);

    /**
     * Returns the user for a given ID
     *
     * @param id The ID of the user to find
     * @return {@link ApplicationUser} for the given ID
     * @throws ApplicationEntityNotFoundException if no user with the given ID was found
     * @see #getUsers()
     */
    ApplicationUser getExistentUser(Long id) throws ApplicationEntityNotFoundException;

    class UsernameExistsException extends RuntimeException {
    }

}
