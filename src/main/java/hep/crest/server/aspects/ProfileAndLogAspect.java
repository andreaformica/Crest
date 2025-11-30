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
 * @author formica
 * @version %I%, %G%
 *
 */
@Aspect
@Component
@Slf4j
public class ProfileAndLogAspect {

    /**
     * @param joinPoint the ProceedingJoinPoint
     * @return Object
     * @throws Throwable If an Exception occurred
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

        net.logstash.logback.marker.LogstashMarker marker = buildParameterMarkers(parameters,
                paramTypes, args);

        final Object proceed = joinPoint.proceed();
        final long executionTime = System.currentTimeMillis() - start;

        marker = addExecutionMetadata(marker, executionTime, joinPoint);

        // Set MDC context data for backward compatibility
        MDC.put("token", "crest");
        MDC.put("crest_method", joinPoint.getSignature().getName());

        log.info(marker, "Method execution completed: {}", joinPoint.getSignature().getName());

        MDC.clear();
        return proceed;
    }

    /**
     * Build markers from method parameters.
     *
     * @param parameters the parameter names
     * @param paramTypes the parameter types
     * @param args       the argument values
     * @return LogstashMarker with all parameters or null
     */
    private net.logstash.logback.marker.LogstashMarker buildParameterMarkers(
            String[] parameters, Class[] paramTypes, Object[] args) {
        net.logstash.logback.marker.LogstashMarker marker = null;

        for (int i = 0; i < parameters.length; i++) {
            if (!shouldIncludeParameter(parameters[i], paramTypes[i], args[i])) {
                continue;
            }

            log.debug("Set parameter {} of type {} with value {}", parameters[i], paramTypes[i],
                    args[i]);

            marker = marker == null
                    ? Markers.append(parameters[i], args[i])
                    : marker.and(Markers.append(parameters[i], args[i]));
        }

        return marker;
    }

    /**
     * Check if parameter should be included in markers.
     *
     * @param paramName the parameter name
     * @param paramType the parameter type
     * @param value     the parameter value
     * @return true if parameter should be included, false otherwise
     */
    private boolean shouldIncludeParameter(String paramName, Class paramType, Object value) {
        if (value == null) {
            return false;
        }

        if ("securityContext".equals(paramName)
                || "info".equals(paramName)
                || "filesBodypart".equals(paramName)) {
            return false;
        }

        return !paramType.getName().contains("List");
    }

    /**
     * Add execution metadata to markers.
     *
     * @param marker        the existing marker
     * @param executionTime the execution time in milliseconds
     * @param joinPoint     the ProceedingJoinPoint
     * @return LogstashMarker with execution metadata
     */
    private net.logstash.logback.marker.LogstashMarker addExecutionMetadata(
            net.logstash.logback.marker.LogstashMarker marker,
            long executionTime,
            ProceedingJoinPoint joinPoint) {
        if (marker == null) {
            marker = Markers.append("executionTime", executionTime);
        }
        else {
            marker = marker.and(Markers.append("executionTime", executionTime));
        }

        return marker
                .and(Markers.append("crest_execution_time", String.valueOf(executionTime)))
                .and(Markers.append("token", "crest"))
                .and(Markers.append("crest_method", joinPoint.getSignature().getName()))
                .and(Markers.append("crest_profile", joinPoint.toShortString()));
    }

}
