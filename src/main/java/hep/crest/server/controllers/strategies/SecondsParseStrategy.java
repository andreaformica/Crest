package hep.crest.server.controllers.strategies;

import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;

/**
 * Strategy for parsing seconds format.
 */
@Slf4j
public class SecondsParseStrategy implements TimeParseStrategy {
    @Override
    public BigInteger parse(String val, String customFormat) {
        log.trace("Use SEC to parse {} (*1000 to get epoch)", val);
        return BigInteger.valueOf(Long.parseLong(val) * 1000L);
    }
}
