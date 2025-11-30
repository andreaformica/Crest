package hep.crest.server.controllers.strategies;

import lombok.extern.slf4j.Slf4j;

import java.math.BigInteger;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Strategy for parsing custom date formats.
 */
@Slf4j
public class CustomDateParseStrategy implements TimeParseStrategy {
    /**
     * Default ISO pattern if no custom format is provided.
     */
    private static final String ISO_PATTERN = "yyyyMMdd'T'HHmmssz";

    @Override
    public BigInteger parse(String val, String customFormat) {
        log.debug("Use CUSTOM pattern {} to parse {}", customFormat, val);
        String format = customFormat != null ? customFormat : ISO_PATTERN;
        DateTimeFormatter dtformatter = DateTimeFormatter.ofPattern(format);
        ZonedDateTime customzdtInstanceAtOffset = ZonedDateTime.parse(val, dtformatter);
        ZonedDateTime customzdtInstanceAtUTC =
                customzdtInstanceAtOffset.withZoneSameInstant(ZoneOffset.UTC);
        BigInteger tepoch = BigInteger.valueOf(customzdtInstanceAtUTC.toInstant().toEpochMilli());
        log.trace("Parsed date using custom format {} - {}; time from epoch is {}",
                format, customzdtInstanceAtUTC, tepoch);
        return tepoch;
    }
}
