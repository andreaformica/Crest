package hep.crest.server.controllers.strategies;

import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;

/**
 * Strategy for parsing big number format.
 */
@Slf4j
public class NumberParseStrategy implements TimeParseStrategy {
    @Override
    public BigInteger parse(String val, String customFormat) {
        log.debug("Use number to parse {}", val);
        return new BigInteger(val);
    }
}
