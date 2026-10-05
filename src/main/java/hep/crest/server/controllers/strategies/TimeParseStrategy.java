package hep.crest.server.controllers.strategies;

import java.math.BigInteger;

/**
 * Strategy for parsing time values in different formats.
 */
public interface TimeParseStrategy {
    /**
     * Parse time value and return milliseconds since epoch.
     *
     * @param val the time string value
     * @param customFormat optional custom date format
     * @return BigInteger representing milliseconds since epoch
     */
    BigInteger parse(String val, String customFormat);
}
