package hep.crest.server.security;

import hep.crest.server.data.pojo.CrestRoles;
import hep.crest.server.data.pojo.CrestUser;
import hep.crest.server.data.repositories.CrestUserRepository;
import hep.crest.server.data.repositories.CrestRolesRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Service to load user details from the database.
 */
@Service
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    /**
     * The user repository.
     */
    private final CrestUserRepository userRepository;

    /**
     * The role repository.
     */
    private final CrestRolesRepository roleRepository;

    /**
     * Constructor.
     *
     * @param userRepository the user repository
     * @param roleRepository the role repository
     */
    @Autowired
    public CustomUserDetailsService(CrestUserRepository userRepository,
                                    CrestRolesRepository roleRepository) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        log.info("Loading user by username: {}", username);
        CrestUser user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // Retrieve roles from the database
        // The User ID should be identical to the role in the CrestRoles table.
        List<CrestRoles> roles = roleRepository.findByRole(user.getId());
        // Map roles to GrantedAuthority with ROLE_ prefix
        List<SimpleGrantedAuthority> authorities = roles.stream()
                .map(role -> new SimpleGrantedAuthority("ROLE_" + role.getRole()))
                .toList();


        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
