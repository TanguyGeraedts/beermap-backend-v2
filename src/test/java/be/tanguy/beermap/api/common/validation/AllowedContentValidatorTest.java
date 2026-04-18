package be.tanguy.beermap.api.common.validation;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class AllowedContentValidatorTest {

    private final AllowedContentValidator validator = new AllowedContentValidator();

    @Test
    void allows_null_and_blank_values_by_default() {
        assertThat(validator.isValid(null, null)).isTrue();
        assertThat(validator.isValid("   ", null)).isTrue();
    }

    @Test
    void allows_regular_text() {
        assertThat(validator.isValid("Fresh citrus IPA from Brussels", null)).isTrue();
        assertThat(validator.isValid("Night Shift Lager", null)).isTrue();
        assertThat(validator.isValid("Sniggering Fox Pale Ale", null)).isTrue();
    }

    @Test
    void blocks_disallowed_terms() {
        assertThat(validator.isValid(codePoints(0x6e, 0x69, 0x67, 0x67, 0x61), null)).isFalse();
    }

    @Test
    void blocks_disallowed_terms_with_accents() {
        assertThat(validator.isValid(codePoints(0x6e, 0x69, 0x67, 0x67, 0x00e9, 0x72), null)).isFalse();
    }

    private static String codePoints(int... codePoints) {
        return new String(codePoints, 0, codePoints.length);
    }

}
