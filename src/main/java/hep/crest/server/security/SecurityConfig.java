package hep.crest.server.security;

import hep.crest.server.config.CrestProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Web security configuration. This is used only with profile keycloak.
 *
 * @author formica
 * @version %I%, %G%
 */
@Profile({"keycloak"})
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@Slf4j
public class SecurityConfig {

    /**
     * Properties.
     */
    private CrestProperties cprops;

    /**
     * Ctor with injected properties.
     *
     * @param cprops the properties.
     */
    @Autowired
    public SecurityConfig(CrestProperties cprops) {
        this.cprops = cprops;
    }

    @Bean
    @Order(1)  // Higher priority than default (which is usually Order(100))
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        if ("active".equals(cprops.getSecurity())) {
            log.info("Security is active for this server....");
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth ->
                            auth.requestMatchers(HttpMethod.GET, "/**").permitAll()
                                    .requestMatchers(HttpMethod.DELETE).hasRole("crest-admin")
                                    .requestMatchers("/admin/**").hasRole("crest-admin")
                                    .requestMatchers("/folders/**").hasRole("crest-admin")
                                    .requestMatchers("/globaltags/**").hasRole("crest-experts")
                                    .requestMatchers("/globaltagmaps/**").hasRole("crest-experts")
                                    .requestMatchers("/runinfo/**").hasRole("crest-trigger")
                                    .anyRequest().authenticated()
                    )
                    .oauth2ResourceServer(oauth2 -> oauth2
                            .jwt(jwt ->
                                    jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())));
        }
        else if ("none".equals(cprops.getSecurity())) {
            log.info("No security enabled for this server....");
            http.securityMatcher("/**")
                    .authorizeHttpRequests(authorize -> authorize.anyRequest()
                            .permitAll())
                    .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults()));
        }

        return http.build();
    }


    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            // Log all claims for debugging
            log.info("JWT Claims: {}", jwt.getClaims());

            // Extract roles from both realm and resource access
            Map<String, Object> realmAccess = jwt.getClaim("realm_access");
            List<String> realmRoles = (List<String>) realmAccess.get("roles");

            Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
            Map<String, Object> clientAccess = (Map<String, Object>)
                    resourceAccess.get("crest-server");
            List<String> clientRoles = clientAccess != null ? (List<String>)
                    clientAccess.get("roles") : Collections.emptyList();

            // Combine all roles
            List<String> allRoles = new ArrayList<>();
            allRoles.addAll(realmRoles);
            allRoles.addAll(clientRoles);

            return allRoles.stream()
                    .map(role -> "ROLE_" + role) // Add prefix
                    .map(SimpleGrantedAuthority::new)
                    .collect(Collectors.toList());
        });
        return converter;
    }
}
