package hep.crest.server.security;

import hep.crest.server.config.CrestProperties;
import org.keycloak.adapters.springsecurity.KeycloakSecurityComponents;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.web.authentication.session.NullAuthenticatedSessionStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * Web security configuration. This is used only with profile keycloak.
 *
 * @author formica
 * @version %I%, %G%
 */
@Profile({"keycloak"})
@Configuration
@EnableWebSecurity
@ComponentScan(basePackageClasses = KeycloakSecurityComponents.class)
public class SecurityConfig extends KeycloakWebSecurityConfigurerAdapter {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(SecurityConfig.class);

    /**
     * Properties.
     */
    @Autowired
    private CrestProperties cprops;

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider =
                keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    /**
     * Defines the session authentication strategy.
     */
    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        // Stateless server: do not create a session
        return new NullAuthenticatedSessionStrategy();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        // Deal with specific configuration.
        if ("active".equals(cprops.getSecurity())) {
            // If active then authorize requests. Role is guest.
            http
                // stateless, sessionless: no need for csrf
                .csrf(AbstractHttpConfigurer::disable)
                // enforce stateless-ness on the spring side
                .sessionManagement((sessionManagement) ->
                        sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                // the rules
                .authorizeRequests((authorizeRequests) -> authorizeRequests
                        .antMatchers(HttpMethod.GET, "/actuator/health/**").permitAll()
                        .antMatchers(HttpMethod.GET, "/**").hasRole("crest-viewer")
                        // POST and PUT are more restricted
                        .antMatchers(HttpMethod.POST, "/**").hasAnyRole("crest-expert", "crest-admin")
                        .antMatchers(HttpMethod.PUT, "/**").hasAnyRole("crest-expert", "crest-admin")
                        // DELETE is only for admin
                        .antMatchers(HttpMethod.DELETE, "/**").hasRole("crest-admin")
                        .anyRequest().denyAll()
                );
        }
        else if ("none".equals(cprops.getSecurity())) {
            log.info("No security enabled for this server....");
            http.authorizeRequests().antMatchers("/**").permitAll().and().httpBasic().disable().csrf()
                    .disable();
        }
    }
}
