package be.tanguy.beermap.api.common.validation;

import be.tanguy.beermap.api.users.domain.UserProfileInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class UsernameValidator implements ConstraintValidator<ValidUsername, String> {

    private boolean allowNull;

    @Override
    public void initialize(ValidUsername annotation) {
        this.allowNull = annotation.allowNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return allowNull;
        }

        String trimmed = value.trim();

        if (trimmed.length() < UserProfileInfo.MIN_USERNAME_LENGTH ||
                trimmed.length() > UserProfileInfo.MAX_USERNAME_LENGTH) {
            return false;
        }

        return trimmed.matches(UserProfileInfo.USERNAME_REGEX);
    }
}