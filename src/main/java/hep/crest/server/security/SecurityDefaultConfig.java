package hep.crest.server.security;

import hep.crest.server.config.CrestProperties;
import org.keycloak.representations.AccessToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.context.annotation.Scope;
import org.springframework.context.annotation.ScopedProxyMode;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.web.context.WebApplicationContext;

import javax.ws.rs.HttpMethod;

/**
 * Web security configuration. This is used only with profile different from keycloak.
 *
 * @version %I%, %G%
 * @author formica
 *
 */
@Profile({"!keycloak"})
@Configuration
@EnableWebSecurity
public class SecurityDefaultConfig {

    /**
     * Properties.
     */
    @Autowired
    private CrestProperties cprops;


    /**
     * Security filter chain.
     * @param http
     * @return SecurityFilterChain
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if ("active".equals(cprops.getSecurity())) {
            http
                    .authorizeRequests()
                    .antMatchers(HttpMethod.GET, "/**").permitAll()
                    .antMatchers(HttpMethod.HEAD, "/**").permitAll()
                    .anyRequest().denyAll(); // Deny all other methods (POST, DELETE, etc.)
        }
        else {
            http.authorizeRequests().antMatchers("/**").permitAll();
        }
        http.headers().frameOptions().disable();
        http.csrf().disable();

        return http.build();
    }

    /**
     * Web security customizer.
     * @return WebSecurityCustomizer
     */
    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers(
                "/static/**",
                "/css/**",
                "/js/**",
                "/images/**"
        );
    }

    @Bean
    @Scope(scopeName = WebApplicationContext.SCOPE_REQUEST, proxyMode = ScopedProxyMode.TARGET_CLASS)
    public AccessToken accessToken() {
        // Provide a fake access token object.
        AccessToken accessToken = new AccessToken();
        accessToken.setSubject("abc");
        accessToken.setName("Tester");
        return accessToken;
    }
}
