package hep.crest.server.security;
/**
 *
 */

import hep.crest.data.config.CrestProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpMethod;
import org.springframework.ldap.core.ContextSource;
import org.springframework.ldap.core.LdapTemplate;
import org.springframework.ldap.core.support.LdapContextSource;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.ldap.DefaultSpringSecurityContextSource;
import org.springframework.security.ldap.userdetails.DefaultLdapAuthoritiesPopulator;
import org.springframework.security.ldap.userdetails.LdapAuthoritiesPopulator;

/**
 * @author formica
 *
 */
@EnableWebSecurity
@PropertySource("classpath:ldap.properties")
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    /**
     * The logger.
     */
    private Logger log = LoggerFactory.getLogger(this.getClass());

    /**
     * The properties.
     */
    @Autowired
    private CrestProperties cprops;

    /**
     * User details.
     */
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * LDAP search.
     */
    @Value("${USER_SEARCH_BASE}")
    private String userSearchBase;
    /**
     * LDAP search.
     */
    @Value("${USER_DN_PATTERNS}")
    private String userDnPatterns;
    /**
     * LDAP search.
     */
    @Value("${USER_SEARCH_FILTER}")
    private String userSearchFilter;

    /**
     * LDAP search.
     */
    @Value("${GROUP_SEARCH_BASE}")
    private String groupSearchBase;
    /**
     * LDAP search.
     */
    @Value("${GROUP_SEARCH_FILTER}")
    private String groupSearchFilter;
    /**
     * LDAP search.
     */
    @Value("${GROUP_ROLE_ATTRIBUTE}")
    private String groupRoleAttribute;
    /**
     * LDAP search.
     */
    @Value("${MANAGER_DN}")
    private String managerDn;
    /**
     * LDAP search.
     */
    @Value("${MANAGER_PASSWORD}")
    private String managerPassword;
    /**
     * LDAP search.
     */
    @Value("${LDAP_AUTHENTICATOR_URL}")
    private String url;
    /**
     * LDAP search.
     */
    @Value("${ACCESS}")
    private String access;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        log.debug("Configure http security rules");

        if (cprops.getSecurity().equals("active")) {
            http.authorizeRequests().antMatchers(HttpMethod.GET, "/**").permitAll().antMatchers(HttpMethod.POST, "/**")
                    .access("hasAuthority('ATLAS-CONDITIONS')")
                    .antMatchers(HttpMethod.DELETE, "/**").hasRole("GURU").and().httpBasic().and()
                    .csrf().disable();

        }
        else if (cprops.getSecurity().equals("none")) {
            log.info("No security enabled for this server....");
            http.authorizeRequests().antMatchers("/**").permitAll().and().httpBasic().and().csrf().disable();
        }
        else if (cprops.getSecurity().equals("reco")) {
            http.authorizeRequests().antMatchers(HttpMethod.POST, "/**").denyAll()
                    .antMatchers(HttpMethod.PUT, "/**").denyAll()
                    .antMatchers(HttpMethod.DELETE, "/**").denyAll()
                    .and().httpBasic().and().csrf()
                    .disable();
        }
        else if (cprops.getSecurity().equals("weak")) {
            log.info("Low security enabled for this server....");
            http.authorizeRequests().antMatchers(HttpMethod.DELETE, "/**").hasRole("GURU")
                    .antMatchers(HttpMethod.OPTIONS, "/**").permitAll().antMatchers(HttpMethod.HEAD, "/**").permitAll()
                    .antMatchers(HttpMethod.GET, "/**").permitAll().antMatchers(HttpMethod.POST, "/**").permitAll()
                    .antMatchers(HttpMethod.DELETE, "/admin/**").hasRole("GURU")
                    .antMatchers(HttpMethod.PUT, "/admin/**").hasRole("GURU").and().httpBasic().and().csrf().disable();
        }
    }

    /**
     * @param auth
     * @throws Exception
     */
    @Autowired
    public void configureGlobal(AuthenticationManagerBuilder auth) throws Exception {
        log.debug("Configure authentication manager");
        if (cprops.getAuthenticationtype().equals("database")) {
            auth.userDetailsService(userDetailsService).passwordEncoder(passwordEncoder());
        }
        else if (cprops.getAuthenticationtype().equals("ldap")) {
            // here put LDAP
            log.debug("Use ldap authentication: {} {} {} {} ", url, managerDn, userSearchBase, userDnPatterns);
            LdapAuthoritiesPopulator ldapAuthoritiesPopulator = authoritiesPopulator(contextSource());
            auth.ldapAuthentication()
                    .ldapAuthoritiesPopulator(ldapAuthoritiesPopulator)
                    .contextSource(contextSource())
                    .userSearchBase(userSearchBase).userDnPatterns(userDnPatterns).userSearchFilter(userSearchFilter)
                    .rolePrefix("");
        }
        else {
            auth.inMemoryAuthentication().withUser("userusr").password("password").roles("user").and().withUser(
                    "adminusr")
                    .password("password").roles("admin", "user").and().withUser("guru").password("guru")
                    .roles("admin", "user", "GURU");
        }
        // for this check
        // http://www.programming-free.com/2015/09/spring-security-password-encryption.html?showComment=1502891898256
        // auth.userDetailsService(accountRepository)

    }

    /**
     * @return LdapContextSource
     */
    @Bean
    public LdapContextSource contextSource() {
        String ldapurl = url;
        DefaultSpringSecurityContextSource context = new DefaultSpringSecurityContextSource(ldapurl);
        context.setUserDn(managerDn);
        context.setPassword(managerPassword);
        context.setReferral("follow");
        context.afterPropertiesSet();
        return context;
    }

    /**
     * @return LdapTemplate
     */
    @Bean
    public LdapTemplate ldapTemplate() {
        return new LdapTemplate(contextSource());
    }

    /**
     * @param context
     * @return LdapAuthoritiesPopulator
     */
    @Bean(name = "ldapAuthoritiesPopulator")
    public LdapAuthoritiesPopulator authoritiesPopulator(ContextSource context) {
        log.debug("Instantiate authorities populator....");
        LdapAuthoritiesPopulator ldp = new DefaultLdapAuthoritiesPopulator(context, groupSearchBase);
        ((DefaultLdapAuthoritiesPopulator) ldp).setSearchSubtree(true);
        ((DefaultLdapAuthoritiesPopulator) ldp).setGroupRoleAttribute(groupRoleAttribute);
        ((DefaultLdapAuthoritiesPopulator) ldp).setGroupSearchFilter(groupSearchFilter);
        ((DefaultLdapAuthoritiesPopulator) ldp).setRolePrefix("");
        return ldp;
    }

    /**
     * @return PasswordEncoder
     */
    @Bean(name = "dbPasswordEncoder")
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}
