package hep.crest.server.controllers.strategies;

import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Strategy for parsing ISO format dates.
 */
@Slf4j
public class IsoDateParseStrategy implements TimeParseStrategy {
    private static final String ISO_PATTERN = "yyyyMMdd'T'HHmmssz";

    @Override
    public BigInteger parse(String val, String customFormat) {
        log.trace("Use ISO pattern {} to parse {}", ISO_PATTERN, val);
        DateTimeFormatter dtformatter = DateTimeFormatter.ofPattern(ISO_PATTERN);
        ZonedDateTime zdtInstanceAtOffset = ZonedDateTime.parse(val, dtformatter);
        ZonedDateTime zdtInstanceAtUTC = zdtInstanceAtOffset.withZoneSameInstant(ZoneOffset.UTC);
        BigInteger tepoch = BigInteger.valueOf(zdtInstanceAtUTC.toInstant().toEpochMilli());
        log.trace("Parsed date using -iso- format {}; time from epoch is {}", zdtInstanceAtUTC, tepoch);
        return tepoch;
    }
}
