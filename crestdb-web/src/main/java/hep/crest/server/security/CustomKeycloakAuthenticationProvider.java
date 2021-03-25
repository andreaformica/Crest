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
     * @param grantedAuthoritiesMapper
     */
    public void setGrantedAuthoritiesMapper(GrantedAuthoritiesMapper grantedAuthoritiesMapper) {
        this.grantedAuthoritiesMapper = grantedAuthoritiesMapper;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        KeycloakAuthenticationToken token = (KeycloakAuthenticationToken) authentication;
        List<GrantedAuthority> grantedAuthorities = new ArrayList<GrantedAuthority>();

        for (String role : token.getAccount().getRoles()) {
            grantedAuthorities.add(new KeycloakRole(role));
        }

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
                mapAuthorities(grantedAuthorities));
    }

    /**
     * @param authorities
     * @return Collection
     */
    private Collection<? extends GrantedAuthority> mapAuthorities(
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
