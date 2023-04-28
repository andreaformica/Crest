package hep.crest.server.aspects;

import lombok.extern.slf4j.Slf4j;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Component;

import java.security.Principal;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;

/**
 * Implementation of UserInfo interface.
 * @author formica
 */
@Slf4j
@Component
public class UserInfoImpl implements UserInfo {

    @Override
    public String getUserId(Authentication auth) {
        String clientid = "TEST";
        // Check the authentication.
        if (auth == null) {
            // No authentication is present. It will be used to reject the request.
            log.warn(
                    "Stop execution....for the moment we only print this message...no action is "
                    + "taken");
        }
        else {
            // Retrieve user details.
            final Principal user = (Principal) auth.getPrincipal();
            if (user instanceof KeycloakPrincipal) {
                KeycloakPrincipal<KeycloakSecurityContext> kp =
                        (KeycloakPrincipal<KeycloakSecurityContext>) user;
                // Use IDToken in Svom : getIdToken
                // Use AccessToken with CERN : getAccessToken
                // example: kp .dot. getKeycloakSecurityContext() .dot. getToken()
                log.info("Keycloak principal: {}", kp);
                AccessToken token = kp.getKeycloakSecurityContext().getToken();
                log.debug("Found token : token {}!", token);
                if (token != null) {
                    log.debug("Got token for {}", token.getAudience()[0]);
                    clientid = getClientId(token.getOtherClaims());
                }
            }
        }
        return clientid;
    }

    @Override
    public Boolean isUserInRole(Authentication auth, String role) {
        final Principal user = (Principal) auth.getPrincipal();
        log.info("Verify the role for user : {} ",
                user == null ? "none" : user);
        Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
        if (user != null) {
            // Search if tagname is in list of roles.
            String crestrole = "ROLE_crest-" + role;
            Optional<? extends GrantedAuthority> userRole = roles.stream()
                    .filter(r -> r.getAuthority().equalsIgnoreCase(crestrole)).findFirst();
            if (userRole.isPresent()) {
                log.info("User is in role {} : he can create tag with name {}",
                        userRole.get().getAuthority(),
                        role);
                return Boolean.TRUE;
            }
            else {
                log.info("check if user is an admin");
                Optional<? extends GrantedAuthority> adminRole = roles.stream()
                        .filter(r -> r.getAuthority().contains("admin")).findFirst();
                if (adminRole.isPresent()) {
                    log.info("User is an admin: he can create any tag");
                    return Boolean.TRUE;
                }
            }
        }
        // Seems that the user is not in the role.
        roles.stream()
                .forEach(s -> log.debug("Selected role is {}", s.getAuthority()));
        return Boolean.FALSE;
    }

    /**
     * Get the client ID from other claims.
     *
     * @param otherClaims
     * @return String
     */
    protected String getClientId(Map<String, Object> otherClaims) {
        String clientid = "TEST";
        if (otherClaims != null) {
            for (Map.Entry<String, Object> entry : otherClaims.entrySet()) {
                log.info("Found claim : {} ", entry);
                if ("clientId".equals(entry.getKey())) {
                    clientid = (String) entry.getValue();
                }
            }
        }
        return clientid;
    }
}
