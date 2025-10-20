package hep.crest.server.security;


import hep.crest.server.config.CrestProperties;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import static org.springframework.security.config.Customizer.withDefaults;

@Profile({"dbsecurity"})
@Configuration
@EnableWebSecurity
@Slf4j
public class SecurityDbConfig {

    /**
     * Custom authentication provider.
     */
    private final CustomAuthenticationProvider customAuthenticationProvider;
    /**
     * Custom user details service.
     */
    private final CustomUserDetailsService customUserDetailsService;
    /**
     * Properties.
     */
    private CrestProperties cprops;

    /**
     * Constructor with injected properties.
     *
     * @param customAuthenticationProvider the custom authentication provider
     * @param customUserDetailsService     the custom user details service
     * @param cprops
     */
    @Autowired
    public SecurityDbConfig(CustomAuthenticationProvider customAuthenticationProvider,
                            CustomUserDetailsService customUserDetailsService,
                            CrestProperties cprops) {
        this.customAuthenticationProvider = customAuthenticationProvider;
        this.customUserDetailsService = customUserDetailsService;
        this.cprops = cprops;
    }

    @Bean
    @Order(1)  // Higher priority than default (which is usually Order(100))
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        if ("active".equals(cprops.getSecurity())) {
            log.info("Security is active for this server....Use DB for authentication");
            http.csrf(AbstractHttpConfigurer::disable)
                    .authorizeHttpRequests(auth ->
                            auth.requestMatchers(HttpMethod.GET, "/**").permitAll()
                                    .requestMatchers("/admin/**").hasRole("ADMIN")
                                    .requestMatchers("/folders/**").hasRole("ADMIN")
                                    .anyRequest().authenticated()
                    )
                    .httpBasic(withDefaults()) // Optionally add basic auth
                    .authenticationProvider(customAuthenticationProvider)
                    .userDetailsService(customUserDetailsService);
        }
        else if ("none".equals(cprops.getSecurity())) {
            log.info("No security enabled for this server....");
            http.securityMatcher("/**")
                    .authorizeHttpRequests(authorize -> authorize.anyRequest()
                            .permitAll());
        }

        return http.build();
    }

    /**
     * Password encoder bean.
     *
     * @return the password encoder
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
