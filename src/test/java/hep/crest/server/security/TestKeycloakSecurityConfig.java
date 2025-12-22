package hep.crest.server.security;

import hep.crest.server.config.CrestProperties;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.Instant;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test class for SecurityConfig with Keycloak profile.
 * This test verifies the security configuration beans are created correctly
 * without requiring a full Keycloak server.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles({"test", "keycloak"})
@Slf4j
public class TestKeycloakSecurityConfig {

    @Autowired(required = false)
    private SecurityConfig securityConfig;

    @Autowired(required = false)
    private JwtAuthenticationConverter jwtAuthenticationConverter;

    @Autowired
    private CrestProperties crestProperties;

    @MockitoBean
    private JwtDecoder jwtDecoder;

    /**
     * Test that SecurityConfig is instantiated when keycloak profile is active.
     */
    @Test
    public void testSecurityConfigBeanCreated() {
        log.info("=======> testSecurityConfigBeanCreated");
        assertThat(securityConfig).isNotNull();
        log.info("SecurityConfig bean is properly created: {}", securityConfig.getClass().getName());
    }

    /**
     * Test that JwtAuthenticationConverter bean is created.
     */
    @Test
    public void testJwtAuthenticationConverterBeanCreated() {
        log.info("=======> testJwtAuthenticationConverterBeanCreated");
        assertThat(jwtAuthenticationConverter).isNotNull();
        log.info("JwtAuthenticationConverter bean is properly created");
    }

    /**
     * Test that CrestProperties is available.
     */
    @Test
    public void testCrestPropertiesAvailable() {
        log.info("=======> testCrestPropertiesAvailable");
        assertThat(crestProperties).isNotNull();
        log.info("CrestProperties: security={}", crestProperties.getSecurity());
    }

    /**
     * Test that JwtAuthenticationConverter correctly extracts roles from JWT claims.
     */
    @Test
    public void testJwtAuthenticationConverterExtractsRoles() {
        log.info("=======> testJwtAuthenticationConverterExtractsRoles");

        // Create a mock JWT with realm_access and resource_access claims
        Map<String, Object> claims = new HashMap<>();

        // Add realm_access with roles
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("crest-admin", "crest-expert"));
        claims.put("realm_access", realmAccess);

        // Add resource_access with client roles
        Map<String, Object> clientAccess = new HashMap<>();
        clientAccess.put("roles", List.of("crest-trigger"));
        Map<String, Object> resourceAccess = new HashMap<>();
        resourceAccess.put("crest-server", clientAccess);
        claims.put("resource_access", resourceAccess);

        // Create a mock JWT
        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .claim("realm_access", realmAccess)
                .claim("resource_access", resourceAccess)
                .claim("sub", "test-user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        // Convert JWT to authorities
        Collection<GrantedAuthority> authorities = jwtAuthenticationConverter
                .convert(jwt)
                .getAuthorities();

        log.info("Extracted authorities: {}", authorities);

        // Verify that roles are correctly extracted with ROLE_ prefix
        assertThat(authorities).isNotEmpty();
        assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                .contains("ROLE_crest-admin", "ROLE_crest-expert", "ROLE_crest-trigger");
    }

    /**
     * Test that JwtAuthenticationConverter handles missing client roles gracefully.
     */
    @Test
    public void testJwtAuthenticationConverterHandlesMissingClientRoles() {
        log.info("=======> testJwtAuthenticationConverterHandlesMissingClientRoles");

        // Create a mock JWT with only realm_access (no resource_access for crest-server)
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of("crest-admin"));

        Map<String, Object> resourceAccess = new HashMap<>();
        // No crest-server client access

        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .claim("realm_access", realmAccess)
                .claim("resource_access", resourceAccess)
                .claim("sub", "test-user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        // Convert JWT to authorities
        Collection<GrantedAuthority> authorities = jwtAuthenticationConverter
                .convert(jwt)
                .getAuthorities();

        log.info("Extracted authorities (no client roles): {}", authorities);

        // Verify that realm roles are still extracted
        assertThat(authorities).isNotEmpty();
        assertThat(authorities.stream().map(GrantedAuthority::getAuthority))
                .contains("ROLE_crest-admin");
    }

    /**
     * Test that JwtAuthenticationConverter handles empty roles.
     */
    @Test
    public void testJwtAuthenticationConverterHandlesEmptyRoles() {
        log.info("=======> testJwtAuthenticationConverterHandlesEmptyRoles");

        // Create a mock JWT with empty roles
        Map<String, Object> realmAccess = new HashMap<>();
        realmAccess.put("roles", List.of());

        Map<String, Object> resourceAccess = new HashMap<>();

        Jwt jwt = Jwt.withTokenValue("mock-token")
                .header("alg", "RS256")
                .claim("realm_access", realmAccess)
                .claim("resource_access", resourceAccess)
                .claim("sub", "test-user")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(3600))
                .build();

        // Convert JWT to authorities
        Collection<GrantedAuthority> authorities = jwtAuthenticationConverter
                .convert(jwt)
                .getAuthorities();

        log.info("Extracted authorities (empty roles): {}", authorities);

        // Verify that an empty collection is returned
        assertThat(authorities).isEmpty();
    }
}

