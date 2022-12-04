package hep.crest.server.security;

import hep.crest.server.config.CrestProperties;
import org.keycloak.adapters.springsecurity.authentication.KeycloakAuthenticationProvider;
import org.keycloak.adapters.springsecurity.config.KeycloakWebSecurityConfigurerAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.authority.mapping.SimpleAuthorityMapper;
import org.springframework.security.core.session.SessionRegistryImpl;
import org.springframework.security.web.authentication.session.RegisterSessionAuthenticationStrategy;
import org.springframework.security.web.authentication.session.SessionAuthenticationStrategy;

/**
 * Web security configuration. This is used only with profile keycloak.
 *
 * @version %I%, %G%
 * @author formica
 *
 */
@Profile({"keycloak"})
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(jsr250Enabled = true)
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

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        super.configure(http);
        // Deal with specific configuration.
        if ("active".equals(cprops.getSecurity())) {
            // If active then authorize requests. Role is guest.
            http.authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/**").permitAll()
                    .antMatchers(HttpMethod.POST, "/**").hasAnyRole("guest")
                    .antMatchers(HttpMethod.DELETE, "/**").hasAnyRole("guest")
                    .anyRequest()
                    .permitAll();
            http.csrf().disable();
        }
        else if ("none".equals(cprops.getSecurity())) {
            log.info("No security enabled for this server....");
            http.authorizeRequests().antMatchers("/**").permitAll().and().httpBasic().disable().csrf()
                    .disable();
        }
    }

    /**
     * Registers the KeycloakAuthenticationProvider with the authentication manager.
     *
     * @param auth the builder.
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        KeycloakAuthenticationProvider keycloakAuthenticationProvider = keycloakAuthenticationProvider();
        keycloakAuthenticationProvider.setGrantedAuthoritiesMapper(new SimpleAuthorityMapper());
        auth.authenticationProvider(keycloakAuthenticationProvider);
    }

    @Bean
    @Override
    protected KeycloakAuthenticationProvider keycloakAuthenticationProvider() {
        // Create a custom provider.
        return new CustomKeycloakAuthenticationProvider();
    }

    @Bean
    @Override
    protected SessionAuthenticationStrategy sessionAuthenticationStrategy() {
        // Create a authentication strategy.
        return new RegisterSessionAuthenticationStrategy(new SessionRegistryImpl());
    }

}
