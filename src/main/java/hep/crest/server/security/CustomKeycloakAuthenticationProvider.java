package hep.crest.server.security;

import org.keycloak.adapters.springsecurity.account.KeycloakRole;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.token.KeycloakAuthenticationToken;
import org.keycloak.representations.AccessToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.mapping.GrantedAuthoritiesMapper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Authentication provider to add realm roles.
 */
public class CustomKeycloakAuthenticationProvider extends KeycloakAuthenticationProvider {
    /**
     * The granted authorities.
     */
    private GrantedAuthoritiesMapper grantedAuthoritiesMapper;

    /**
     * Default ctor.
     */
    public CustomKeycloakAuthenticationProvider() {
        // the initialization is taken care of by keycloak.
    }

    /**
     * @param grantedAuthoritiesMapper
     */
    public void setGrantedAuthoritiesMapper(GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
        // Set the authorities mapper.
        this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        // Get the token.
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        // Init authorities list.
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();
        // Get the roles from the token.
        for (String role : token.getAccount().getRoles()) {
            // Add to granted authorities.
            grantedAuthorities.add(new KeycloakRole(role));
        }
        // For Svom, check if the security context contains a FSC_VHFMGR info.
        AccessToken.Access fsctoken = token.getAccount().getKeycloakSecurityContext().getToken().getResourceAccess(
                "FSC_VHFMGR");
        if (fsctoken != null) {
            for (String role : token.getAccount().getKeycloakSecurityContext().getToken().getResourceAccess(
                    "FSC_VHFMGR").getRoles()) {
                grantedAuthorities.add(new KeycloakRole(role));
            }
        }
        AccessToken.Access realmtoken = token.getAccount().getKeycloakSecurityContext().getToken().getRealmAccess();
        if (realmtoken != null) {
            for (String role : token.getAccount().getKeycloakSecurityContext().getToken().getRealmAccess().getRoles()) {
                grantedAuthorities.add(new KeycloakRole(role));
            }
        }

        return new KeycloakAuthenticationToken(token.getAccount(), token.isInteractive(),
                mapCustomAuthorities(grantedAuthorities));
    }

    /**
     * @param authorities
     * @return Collection
     */
    private Collection<? extends GrantedAuthority> mapCustomAuthorities(
            Collection<? extends GrantedAuthority> authorities) {
        return grantedAuthoritiesMapper != null
                ? grantedAuthoritiesMapper.mapAuthorities(authorities)
                : authorities;
    }

    @Override
    public boolean supports(Class<?> aClass) {
        return KeycloakAuthenticationToken.class.isAssignableFrom(aClass);
    }
}
