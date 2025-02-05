package com.WealthManager.UserInfo.Validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;


public class PasswordValidator implements ConstraintValidator<ValidPassword, String> {
    private static final Logger logger = LoggerFactory.getLogger(PasswordValidator.class);

    @Value("${app.password.validation.enabled:true}") // Default to true if not found
    private boolean validationEnabled;

    @Override
    public boolean isValid(String password, ConstraintValidatorContext context) {
        if (!validationEnabled) {
            return true;
        }

        // Check pattern first
        if (!PasswordSequenceValidator.matchesPattern(password)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password must be at least 8 characters long and contain at least one uppercase letter, one lowercase letter, one number, and one special character.")
                    .addConstraintViolation();

            return false;
        }

        // Check for sequences
        if (!PasswordSequenceValidator.noSequences(password)) {
            context.disableDefaultConstraintViolation();
            context.buildConstraintViolationWithTemplate("Password contains too many sequential characters.")
                    .addConstraintViolation();
            return false;
        }

        return true; // If both checks pass
    }
}