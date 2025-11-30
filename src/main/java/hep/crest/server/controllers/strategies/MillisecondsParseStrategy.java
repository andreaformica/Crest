package hep.crest.server.controllers.strategies;

import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;

/**
 * Strategy for parsing milliseconds format.
 */
@Slf4j
public class MillisecondsParseStrategy implements TimeParseStrategy {
    @Override
    public BigInteger parse(String val, String customFormat) {
        log.trace("Use MS to parse {}", val);
        return BigInteger.valueOf(Long.parseLong(val));
    }
}
