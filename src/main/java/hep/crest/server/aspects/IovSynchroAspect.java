/**
 * <<<<<<< HEAD
 */
package hep.crest.server.aspects;

import hep.crest.server.config.CrestProperties;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.services.IovService;
import hep.crest.server.services.TagService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import javax.ws.rs.NotAuthorizedException;
import java.math.BigInteger;

/**
 * This class is an aspect: to see where it is called you should look to the annotation.
 *
 * @author formica
 *
 */
@Aspect
@Component
@Slf4j
public class IovSynchroAspect {
    /**
     * Properties.
     */
    @Autowired
    private CrestProperties cprops;

    /**
     * The user info.
     */
    @Autowired
    private UserInfo userinfo;

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
     * @throws Throwable If an Exception occurred
     */
    @Around("execution(* hep.crest.server.services.IovService.insertIov(*)) && args(entity)")
    public Object checkSynchro(ProceedingJoinPoint pjp, Iov entity) throws Throwable {
        log.debug("Iov insertion should verify the tag synchronization type : {}", entity);
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
            String clientid = userinfo.getUserId(auth);
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
        else if (Boolean.TRUE.equals(allowedOperation)) {
            // Synchronization aspect is enabled.
            Tag tagentity = null;
            tagentity = tagService.findOne(entity.tag().name());
            // Get synchro type from tag.
            acceptTime = evaluateCondition(tagentity, entity);
        }
        if (acceptTime && allowedOperation) {
            retVal = pjp.proceed();
        }
        else {
            log.warn("Not authorized, either you cannot write in this tag or synchro type is wrong: auth={} "
                     + "synchro={}", allowedOperation, acceptTime);
            throw new NotAuthorizedException("You cannot write iov {}", entity);
        }
        return retVal;
    }

    /**
     * Method to evaluate condition based on Tag synchronization type.
     * For the moment we always accept insertions. This shall change.
     *
     * @param tagentity the tag
     * @param entity the iov
     * @return Boolean : True if the Iov should be accepted for insertion. False otherwise.
     */
    protected boolean evaluateCondition(Tag tagentity, Iov entity) {
        final String synchro = tagentity.synchronization();
        Boolean acceptTime = Boolean.FALSE;
        Iov latest = iovService.latest(tagentity.name());
        switch (synchro) {
            case "SV":
                log.warn("Can only append IOVs....");
                if (latest == null || latest.id().since().compareTo(entity.id().since()) <= 0) {
                    // Latest is before the new one.
                    log.info("IOV in insert has correct time respect to last IOV : {} > {}", entity, latest);
                    acceptTime = true;
                }
                else {
                    // Latest is after the new one.
                    log.warn("IOV in insert has WRONG time respect to last IOV : {} < {}", entity, latest);
                    acceptTime = false;
                }
                break;
            case "APPEND":
                log.warn("Can append data in case the since is after the end time of the tag");
                BigInteger endofval = tagentity.endOfValidity();
                if (endofval  == null || endofval.compareTo(entity.id().since()) <= 0) {
                    log.info("The since is after end of validity of the Tag");
                    acceptTime = true;
                }
                break;
            default:
                // Nothing here, synchro type is not implemented.
                log.debug("Synchro type not found....Insertion is accepted by default");
                acceptTime = true;
                break;
        }
        return acceptTime;
    }
}
