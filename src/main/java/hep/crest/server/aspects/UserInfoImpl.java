package hep.crest.server.aspects;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

/**
 * Implementation of UserInfo interface.
 * @author formica
 */
@Slf4j
@Component
public class UserInfoImpl implements UserInfo {


    @Override
    public String getUserId(Authentication auth) {
        if (auth == null) {
            log.warn("No authentication present");
            return "TEST";
        }

        return auth.getName(); // Use the subject (sub claim) as user ID
    }

    @Override
    public Boolean isUserInRole(Authentication auth, String resourceType) {
        if (auth == null) {
            log.warn("No authentication present");
            return false;
        }

        // Expected format: "crest-{resourceType}" (e.g., "crest-tag")
        String requiredRole = "crest-" + resourceType.toLowerCase();
        log.debug("Checking role: {}", requiredRole);
        // Print the authorities for debugging
        auth.getAuthorities().forEach(r -> log.debug("Available role: {}", r.getAuthority()));

        return auth.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(authority ->
                        authority.equalsIgnoreCase("ROLE_" + requiredRole)
                                || authority.equalsIgnoreCase("ROLE_crest-admin")
                                || authority.equalsIgnoreCase("ROLE_crest-developers")
                );
    }

}
