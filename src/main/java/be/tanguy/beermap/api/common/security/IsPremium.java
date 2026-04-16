package be.tanguy.beermap.api.common.security;

import org.springframework.security.access.prepost.PreAuthorize;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Custom security annotation to restrict access to users with either
 * ADMIN or PREMIUM account types.
 * Maps via UserIdentityAuthenticationToken.
 */
@Target({ ElementType.METHOD, ElementType.TYPE })
@Retention(RetentionPolicy.RUNTIME)
@PreAuthorize("hasRole('ADMIN') or hasRole('PREMIUM')")
public @interface IsPremium {
}