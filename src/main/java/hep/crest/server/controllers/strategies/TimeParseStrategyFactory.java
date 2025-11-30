package hep.crest.server.controllers.strategies;

import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.serializers.ArgTimeUnit;
import lombok.extern.slf4j.Slf4j;

/**
 * Factory for creating time parse strategies.
 */
@Slf4j
public class TimeParseStrategyFactory {
    /**
     * Page request helper to get COOL run-lumi IOV.
     */
    private final PageRequestHelper pageRequestHelper;

    /**
     * Constructor injecting PageRequestHelper.
     *
     * @param pageRequestHelper the page request helper
     */
    public TimeParseStrategyFactory(PageRequestHelper pageRequestHelper) {
        this.pageRequestHelper = pageRequestHelper;
    }

    /**
     * Get the appropriate time parse strategy based on the input format.
     *
     * @param inputFormat the input time unit format
     * @return the corresponding TimeParseStrategy
     */
    public TimeParseStrategy getStrategy(ArgTimeUnit inputFormat) {
        if (inputFormat == null) {
            inputFormat = ArgTimeUnit.MS;
        }

        return switch (inputFormat) {
            case MS -> new MillisecondsParseStrategy();
            case SEC -> new SecondsParseStrategy();
            case RUN -> new RunParseStrategy();
            case RUN_LUMI -> new RunLumiParseStrategy(pageRequestHelper);
            case ISO -> new IsoDateParseStrategy();
            case CUSTOM -> new CustomDateParseStrategy();
            case NUMBER -> new NumberParseStrategy();
            default -> {
                log.error("Cannot process argument time parsing");
                yield new MillisecondsParseStrategy();
            }
        };
    }
}
