/**
 *
 */
package hep.crest.server.aspects;

import hep.crest.server.config.CrestProperties;
import hep.crest.server.data.pojo.CrestRoles;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.repositories.CrestRolesRepository;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.ws.rs.NotAuthorizedException;

import java.util.List;

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
@Slf4j
public class TagSecurityAspect {
    /**
     * Properties.
     */
    private CrestProperties cprops;
    /**
     * The user info.
     */
    private UserInfo userinfo;
    /**
     * The crest roles.
     */
    private CrestRolesRepository rolesRepository;

    /**
     * Ctor for injection.
     * @param cprops
     * @param userinfo
     * @param rolesRepository
     */
    @Autowired
    TagSecurityAspect(CrestProperties cprops, UserInfo userinfo, CrestRolesRepository rolesRepository) {
        this.cprops = cprops;
        this.userinfo = userinfo;
        this.rolesRepository = rolesRepository;
    }

    /**
     * @param pjp
     *            the joinpoint
     * @param entity
     *            the Tag
     * @return Object
     * @throws Throwable If an Exception occurred
     */
    @Around("execution(* hep.crest.server.services.TagService.insertTag(*)) && args(entity) "
            + " || execution(* hep.crest.server.services.TagService.updateTag(*)) && args(entity)")
    public Object checkRole(ProceedingJoinPoint pjp, Tag entity) throws Throwable {
        log.debug("Tag insertions security control for : {}", entity);
        Object retVal = null;
        // If there is no or weak security activated then return.
        if ("none".equals(cprops.getSecurity()) || "weak".equals(cprops.getSecurity())) {
            log.warn("security checks are disabled in this configuration....");
            retVal = pjp.proceed();
        }
        else {
            // Check the authentication.
            final Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            String clientid = userinfo.getUserId(auth);
            List<CrestRoles> roles = rolesRepository.findMatchingTagPatterns(entity.getName());
            log.debug("Roles found for tag {}: {}", entity, roles);
            // Loop over CrestRoles and check if the user has the role corresponding to the
            // role/tagPattern. If at least one role is found then proceed.
            boolean hasRole = Boolean.FALSE;
            for (CrestRoles crestRole : roles) {
                log.info("Role matching {} for tag {}", crestRole.getRole(), entity);
                String role = crestRole.getRole();
                if (userinfo.isUserInRole(auth, role)) {
                    log.debug("User {} has role {} for tag {}", clientid, role, entity);
                    hasRole = Boolean.TRUE;
                    break;
                }
            }
            if (hasRole || entity.getName().startsWith("TEST")) {
                retVal = pjp.proceed();
            }
            else {
                log.warn("Cannot use tag {} for clientId {}", entity, clientid);
                throw new NotAuthorizedException("Cannot write tag " + entity.getName());
            }
        }
        return retVal;
    }

}
