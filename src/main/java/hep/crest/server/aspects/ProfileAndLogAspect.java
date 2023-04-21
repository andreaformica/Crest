/**
 * 
 */
package hep.crest.server.aspects;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;

/**
 * @version %I%, %G%
 * @author formica
 *
 */
@Aspect
@Component
public class ProfileAndLogAspect {
    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(ProfileAndLogAspect.class);

    /**
     * @param joinPoint
     *            the ProceedingJoinPoint
     * @return Object
     * @throws Throwable
     *             If an Exception occurred
     */
    @Around("@annotation(hep.crest.server.annotations.ProfileAndLog)")
    public Object profileAndLog(ProceedingJoinPoint joinPoint) throws Throwable {
        final long start = System.currentTimeMillis();
        final Object[] args = joinPoint.getArgs();
        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameters = codeSignature.getParameterNames();
        Class[] paramTypes = codeSignature.getParameterTypes();
        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].equals("securityContext") || parameters[i].equals("info")) {
                continue;
            }
            if (args[i] == null) {
                continue;
            }
            MDC.put(parameters[i], args[i].toString());
        }
        final Object proceed = joinPoint.proceed();
        final long executionTime = System.currentTimeMillis() - start;
        MDC.put("crest_execution_time", String.valueOf(executionTime));
        log.info("Profile {} ", joinPoint.toShortString());
        MDC.clear();
        return proceed;
    }

}
