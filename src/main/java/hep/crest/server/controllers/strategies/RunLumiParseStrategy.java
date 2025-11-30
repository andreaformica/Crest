package hep.crest.server.controllers.strategies;

import hep.crest.server.controllers.PageRequestHelper;
import lombok.extern.slf4j.Slf4j;
import java.math.BigInteger;

/**
 * Strategy for parsing run-lumi format.
 */
@Slf4j
public class RunLumiParseStrategy implements TimeParseStrategy {
    private final PageRequestHelper pageRequestHelper;

    public RunLumiParseStrategy(PageRequestHelper pageRequestHelper) {
        this.pageRequestHelper = pageRequestHelper;
    }

    @Override
    public BigInteger parse(String val, String customFormat) {
        log.trace("Use run-lumi...{}", val);
        String[] rl = val.split("-");
        return pageRequestHelper.getCoolRunLumi(rl[0], rl[1]);
    }
}
