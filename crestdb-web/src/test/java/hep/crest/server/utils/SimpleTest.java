package hep.crest.server.utils;

import hep.crest.server.serializers.ArgTimeUnit;
import lombok.extern.slf4j.Slf4j;

import java.util.Locale;

@Slf4j
public class SimpleTest {

    public static void main(String[] args) {
        try {
            ArgTimeUnit tu = ArgTimeUnit.valueOf("ms".toUpperCase(Locale.ROOT));
            log.info("Time unit ms is: {}", tu);
            ArgTimeUnit tu1 = ArgTimeUnit.valueOf("number".toUpperCase(Locale.ROOT));
            log.info("Time unit number is: {}", tu1);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
