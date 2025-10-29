/**
 *
 */
package hep.crest.server.aspects;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.CodeSignature;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import net.logstash.logback.marker.Markers;


/**
 * @version %I%, %G%
 * @author formica
 *
 */
@Aspect
@Component
@Slf4j
public class ProfileAndLogAspect {

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
        log.debug("Start profiling at time: {}", start);
        log.debug("Profile method {} ", joinPoint.getSignature().getName());
        final Object[] args = joinPoint.getArgs();

        CodeSignature codeSignature = (CodeSignature) joinPoint.getSignature();
        String[] parameters = codeSignature.getParameterNames();
        Class[] paramTypes = codeSignature.getParameterTypes();
        log.debug("Parameters {} and types {}", parameters, paramTypes);

        // Build markers dynamically for available parameters
        net.logstash.logback.marker.LogstashMarker marker = null;

        for (int i = 0; i < parameters.length; i++) {
            if (parameters[i].equals("securityContext")
                    || parameters[i].equals("info")
                    || parameters[i].equals("filesBodypart")
                    || paramTypes[i].getName().contains("List")) {
                continue;
            }
            if (args[i] == null) {
                continue;
            }
            log.debug("Set parameter {} of type {} with value {}", parameters[i], paramTypes[i],
                    args[i]);

            // Add parameter to markers
            if (marker == null) {
                marker = Markers.append(parameters[i], args[i]);
            }
            else {
                marker = marker.and(Markers.append(parameters[i], args[i]));
            }
        }

        final Object proceed = joinPoint.proceed();
        final long executionTime = System.currentTimeMillis() - start;

        // Add execution metadata to markers
        if (marker == null) {
            marker = Markers.append("executionTime", executionTime);
        }
        else {
            marker = marker.and(Markers.append("executionTime", executionTime));
        }

        marker = marker.and(Markers.append("crest_execution_time", String.valueOf(executionTime)))
                .and(Markers.append("token", "crest"))
                .and(Markers.append("crest_method", joinPoint.getSignature().getName()))
                .and(Markers.append("crest_profile", joinPoint.toShortString()));

        // Set MDC context data for backward compatibility
        MDC.put("token", "crest");
        MDC.put("crest_method", joinPoint.getSignature().getName());

        log.info(marker, "Method execution completed: {}", joinPoint.getSignature().getName());

        MDC.clear();
        return proceed;
    }

}
