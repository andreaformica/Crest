package hep.crest.server.controllers.strategies;

import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;

/**
 * Strategy for parsing run number format.
 */
@Slf4j
public class RunParseStrategy implements TimeParseStrategy {
    private static final int COOLIOV_RUN_MASK = 32;

    @Override
    public BigInteger parse(String val, String customFormat) {
        log.trace("Use run...{}", val);
        BigInteger tepoch = BigInteger.valueOf(Long.parseLong(val));
        return tepoch.shiftLeft(COOLIOV_RUN_MASK);
    }
}
