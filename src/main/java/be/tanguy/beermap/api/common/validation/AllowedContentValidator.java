package be.tanguy.beermap.api.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class AllowedContentValidator implements ConstraintValidator<AllowedContent, String> {

    private static final Pattern DIACRITICS = Pattern.compile("\\p{M}");
    private static final Pattern NON_ALPHANUMERIC = Pattern.compile("[^a-z0-9]+");

    private static final Set<String> BLOCKED_WORDS = Set.of(
            codePoints(0x6e, 0x69, 0x67, 0x67, 0x61),
            codePoints(0x6e, 0x69, 0x67, 0x67, 0x65, 0x72)
    );

    private boolean allowNull = true;

    @Override
    public void initialize(AllowedContent annotation) {
        this.allowNull = annotation.allowNull();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {
        if (value == null || value.isBlank()) {
            return allowNull;
        }

        return normalizedWords(value).stream()
                .noneMatch(BLOCKED_WORDS::contains);
    }

    private static Set<String> normalizedWords(String value) {
        String lowerCase = value.toLowerCase(Locale.ROOT);
        String withoutDiacritics = DIACRITICS.matcher(
                Normalizer.normalize(lowerCase, Normalizer.Form.NFD)
        ).replaceAll("");

        String normalized = NON_ALPHANUMERIC.matcher(withoutDiacritics).replaceAll(" ").trim();
        if (normalized.isEmpty()) {
            return Set.of();
        }

        return Arrays.stream(normalized.split("\\s+"))
                .collect(Collectors.toSet());
    }

    private static String codePoints(int... codePoints) {
        return new String(codePoints, 0, codePoints.length);
    }
}