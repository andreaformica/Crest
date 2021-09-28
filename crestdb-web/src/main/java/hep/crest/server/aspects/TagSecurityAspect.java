/**
 *
 */
package hep.crest.server.aspects;

import hep.crest.data.config.CrestProperties;
import hep.crest.data.pojo.Tag;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.IDToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.NotAuthorizedException;
import java.security.Principal;
import java.util.Collection;
import java.util.Map;

/**
 * Aspect to be used for security.
 * It checks the role of the user when executing some insertion actions.
 *
 * @version %I%, %G%
 * @author formica
 *
 */
@Aspect
@Component
public class TagSecurityAspect {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(TagSecurityAspect.class);

    /**
     * Properties.
     */
    @Autowired
    private CrestProperties cprops;

    /**
     * @param pjp
     *            the joinpoint
     * @param entity
     *            the Tag
     * @return Object
     */
    @Around("execution(* hep.crest.server.services.TagService.insertTag(*)) && args(entity) "
            + " || execution(* hep.crest.server.services.TagService.updateTag(*)) && args(entity)")
    public Object checkRole(ProceedingJoinPoint pjp, Tag entity) throws Throwable {
        log.debug("Tag insertion should verify the tag name : {}", entity.name());
        Object retVal = null;
        // If there is no or weak security activated then return.
        if ("none".equals(cprops.getSecurity()) || "weak".equals(cprops.getSecurity())) {
            log.warn("security checks are disabled in this configuration....");
            retVal = pjp.proceed();
        }
        else {
            // Check the authentication.
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String clientid = getUserId(auth);
            if (entity.name().startsWith(clientid) || entity.name().startsWith("TEST")) {
                retVal = pjp.proceed();
            }
            else {
                log.warn("Cannot insert tag {} for clientid {}", entity, clientid);
                throw new NotAuthorizedException("You cannot write tag " + entity.name());
            }
        }
        return retVal;
    }

    /**
     *
     * @param auth
     * @return String
     */
    protected String getUserId(Authentication auth) {
        String clientid = "TEST";
        // Check the authentication.
        if (auth == null) {
            // No authentication is present. It will be used to reject the request.
            log.warn(
                    "Stop execution....for the moment it only print this message...no action is taken");
        }
        else {
            // Retrieve user details.
            final Principal user = (Principal) auth.getPrincipal();
            log.debug("Found user : {}", user);
            if (user instanceof KeycloakPrincipal) {
                KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) user;
                // Use IDToken in Svom
                IDToken token = kp.getKeycloakSecurityContext().getIdToken();
                // Use AccessToken with CERN crest implementation
                //AccessToken token = kp.getKeycloakSecurityContext().getToken();
                log.debug("Found principal as Keycloak : {} - token {}!", kp, token);
                if (token != null) {
                    log.debug("Got token for {}", token.getAudience()[0]);
                    Map<String, Object> otherClaims = token.getOtherClaims();
                    if (otherClaims != null) {
                        for (String key : otherClaims.keySet()) {
                            log.info("Found claim : {} = {}", key, otherClaims.get(key));
                            if ("clientId".equals(key)) {
                                clientid = (String) otherClaims.get(key);
                            }
                        }
                    }
                }
            }
            log.info("Tag insertion should verify the role for user : {} with clientid {}",
                    user == null ? "none" : user, clientid);
            log.debug("For the moment we print all roles and filter on one role as an example...");
            if (user != null) {
                // User details are available.
                String username = user.getName();
                Collection<? extends GrantedAuthority> roles = auth.getAuthorities();
                // If ATLAS-CONDITIONS role is present, then it should allow the method.
                final GrantedAuthority[] tagroles = roles.stream()
                        .toArray(GrantedAuthority[]::new);
                log.debug("Found list of roles of length {} for user {}", tagroles.length, username);
                // For the moment just print the roles.
                roles.stream()
//                        .filter(s -> s.getAuthority().startsWith("ATLAS-CONDITIONS"))
                        .forEach(s -> log.debug("Selected role is {}", s.getAuthority()));
            }
        }
        return clientid;
    }

}
