package com.westers.boothguide.services;

import org.passay.RuleResult;

/**
 * Service interface for validating user credentials.
 */
public interface ValidationService {
    /**
     * Validates the given password for the specified username.
     *
     * @param username The username for which the password is being validated.
     * @param password The password to validate.
     * @return The result of the validation as a RuleResult.
     */
    RuleResult isValidPassword(String username, String password);
}
