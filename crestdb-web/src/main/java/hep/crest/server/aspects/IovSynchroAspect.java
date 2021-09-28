/**
 *
 */
package hep.crest.server.aspects;

import hep.crest.data.config.CrestProperties;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Tag;
import hep.crest.server.exceptions.NotExistsPojoException;
import hep.crest.server.services.IovService;
import hep.crest.server.services.TagService;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.keycloak.KeycloakPrincipal;
import org.keycloak.KeycloakSecurityContext;
import org.keycloak.representations.AccessToken;
import org.keycloak.representations.IDToken;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.NotAuthorizedException;
import java.security.Principal;
import java.util.Map;

/**
 * @author formica
 *
 */
@Aspect
@Component
public class IovSynchroAspect {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(IovSynchroAspect.class);

    /**
     * Properties.
     */
    @Autowired
    private CrestProperties cprops;

    /**
     * Service.
     */
    @Autowired
    private TagService tagService;
    /**
     * Service.
     */
    @Autowired
    private IovService iovService;

    /**
     * Check synchronization.
     * @param pjp
     *            the joinpoint
     * @param entity
     *            the Tag
     * @return Object
     */
    @Around("execution(* hep.crest.server.services.IovService.insertIov(*)) && args(entity)")
    public Object checkSynchro(ProceedingJoinPoint pjp, Iov entity) throws Throwable {
        log.debug("Iov insertion should verify the tag synchronization type : {}",
                entity.tag().name());
        Object retVal = null;
        Boolean allowedOperation = false;
        // If there is no or weak security activated then set allowed to true.
        if ("none".equals(cprops.getSecurity()) || "weak".equals(cprops.getSecurity())) {
            log.warn("security checks are disabled in this configuration....");
            allowedOperation = true;
        }
        else {
            // Check the authentication.
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String clientid = getUserId(auth);
            if (entity.tag().name().startsWith(clientid) || entity.tag().name().startsWith("TEST")) {
                allowedOperation = true;
            }
        }
        Boolean acceptTime = false;
        // Get synchro property
        if ("none".equals(cprops.getSynchro())) {
            log.warn("synchronization checks are disabled in this configuration....");
            acceptTime = true;
        }
        else if (allowedOperation) {
            // Synchronization aspect is enabled.
            Tag tagentity = null;
            try {
                tagentity = tagService.findOne(entity.tag().name());
                // Get synchro type from tag.
                final String synchro = tagentity.synchronization();
                // Synchro for single version type. Can only append IOVs.
                if ("SV".equalsIgnoreCase(synchro)) {
                    log.warn("Can only append IOVs....");
                    Iov latest = null;
                    // Get latest IOV.
                    latest = iovService.latest(tagentity.name(), "now", "ms");
                    if (latest == null) {
                        // No latest is present.
                        log.info("No iov could be retrieved");
                        acceptTime = true;
                    }
                    else if (latest.id().since().compareTo(entity.id().since()) <= 0) {
                        // Latest is before the new one.
                        log.info("IOV in insert has correct time respect to last IOV : {} > {}", entity, latest);
                        acceptTime = true;
                    }
                    else {
                        // Latest is after the new one.
                        log.warn("IOV in insert has WRONG time respect to last IOV : {} < {}", entity, latest);
                        acceptTime = false;
                    }
                }
                else {
                    // Nothing here, synchro type is not implemented.
                    log.debug("Synchro type not found....Insertion is accepted by default");
                    acceptTime = true;
                    // throw new NotFoundException("Cannot find synchro type " + synchro);
                }
            }
            catch (final NotExistsPojoException e) {
                log.error("Error checking synchronization, tag does not exists : {}", e.getMessage());
                throw e;
            }
        }
        if (acceptTime && allowedOperation) {
            retVal = pjp.proceed();
        }
        else {
            log.warn("Not authorized, either you cannot write in this tag or synchro type is wrong: auth={} "
                     + "synchro={}", allowedOperation, acceptTime);
            throw new NotAuthorizedException("You cannot write iov in tag " + entity.tag().name());
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
            if (user instanceof KeycloakPrincipal) {
                KeycloakPrincipal<KeycloakSecurityContext> kp = (KeycloakPrincipal<KeycloakSecurityContext>) user;
                //IDToken token = kp.getKeycloakSecurityContext().getIdToken();
                log.info("Keycloak principal: {}", kp);
                AccessToken token = kp.getKeycloakSecurityContext().getToken();
                log.debug("Found token : token {}!", token);
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
        }
        return clientid;
    }
}
