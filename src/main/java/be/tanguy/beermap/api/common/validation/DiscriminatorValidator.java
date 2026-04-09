package be.tanguy.beermap.api.common.validation;

import be.tanguy.beermap.api.users.domain.UserProfileInfo;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DiscriminatorValidator implements ConstraintValidator<ValidDiscriminator, String> {

    private boolean allowNull;

    @Override
    public void initialize(ValidDiscriminator annotation) {
        this.allowNull = annotation.allowNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return allowNull;
        }

        return value.trim().matches(UserProfileInfo.DISCRIMINATOR_REGEX);
    }
}