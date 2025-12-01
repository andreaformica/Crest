/**
 *
 */
package hep.crest.server.controllers;

import hep.crest.server.config.CrestProperties;
import hep.crest.server.controllers.strategies.TimeParseStrategy;
import hep.crest.server.controllers.strategies.TimeParseStrategyFactory;
import hep.crest.server.exceptions.CdbBadRequestException;
import hep.crest.server.serializers.ArgTimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.domain.Sort.Order;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * PageRequestHelper is a utility class to treat input parameter from HTTP
 * requests. It generated then list of criterias and ordering statement. Those
 * will be used in SQL requests.
 *
 * @author formica
 * @version %I%, %G%
 *
 */
@Component
@Slf4j
public class PageRequestHelper {

    /**
     * The sort pattern.
     */
    private static final String SORT_PATTERN = "([a-zA-Z0-9\\.]+?)(:)(ASC|DESC),";

    /**
     * Maximum page size.
     */
    private static final Integer MAX_PAGE_SIZE = 10000;
    /**
     * Cool Max data in COOL format.
     */
    public static final long COOL_MAX_DATE = 9223372036854775807L;
    /**
     * Max sort field name length.
     */
    private static final int MAX_FIELD_NAME_LENGTH = 20;
    /**
     * Cool Max run in COOL format.
     */
    public static final long COOL_MAX_RUN = 2147483647L;
    /**
     * Cool Max lumi block in COOL format.
     */
    public static final long COOL_MAX_LUMIBLOCK = 4294967295L;
    /**
     *
     */
    private static final int COOLIOV_RUN_MASK = 32;
    /**
     *
     */
    public static final BigDecimal TO_NANO_SECONDS = new BigDecimal(1000000L);
    /**
     * The strategy pattern for parsing time arguments.
     */
    private TimeParseStrategyFactory strategyFactory;

    /**
     * Default ctor.
     */
    public PageRequestHelper() {
        super();
        this.strategyFactory = new TimeParseStrategyFactory(this);
    }

    /**
     * @param page the Integer
     * @param size the Integer
     * @param sort the Integer
     * @return PageRequest
     */
    public PageRequest createPageRequest(Integer page, Integer size, String sort) {

        if (size > MAX_PAGE_SIZE) {
            log.warn("Requested size exceed maximum page size...change it to {}", MAX_PAGE_SIZE);
            size = MAX_PAGE_SIZE;
        }
        final Pattern sortpattern = Pattern.compile(SORT_PATTERN);
        final Matcher sortmatcher = sortpattern.matcher(sort + ",");
        final List<Order> orderlist = new ArrayList<>();
        while (sortmatcher.find()) {
            Direction direc = Direction.ASC;
            if ("DESC".equals(sortmatcher.group(3))) {
                direc = Direction.DESC;
            }
            // Take care of SQL injection.
            final String field = sortmatcher.group(1).trim().replaceAll("\\s+", "");
            log.debug("Creating new order if field is valid: {} {}", direc, field);
            if (!isValidFieldName(field)) {
                throw new CdbBadRequestException(
                        "Invalid field name for sorting: " + field);
            }
            orderlist.add(new Order(direc, field));
        }
        log.debug("Created list of sorting orders of size {}", orderlist.size());
        final Order[] orders = new Order[orderlist.size()];
        int i = 0;
        for (final Order order : orderlist) {
            log.debug("Order @ {} = {} ", i, order);
            orders[i] = order;
            i++;
        }
        final Sort msort = Sort.by(orders);
        return PageRequest.of(page, size, msort);
    }

    /**
     * Perform basic validation of field name. This should avoid SQL injection.
     * @param field the field name
     * @return boolean
     */
    private boolean isValidFieldName(String field) {
        if (field == null || field.isEmpty() || field.length() > MAX_FIELD_NAME_LENGTH) {
            return false;
        }
        for (int i = 0; i < field.length(); i++) {
            char c = field.charAt(i);
            if (!(Character.isLetterOrDigit(c) || c == '_' || c == '.' || c == '-')) {
                return false;
            }
        }

        // Additional blacklist checks for common SQLi / suspicious tokens (case-insensitive)
        String low = field.toLowerCase();
        String[] forbidden = {
                "--", ";", "/*", "*/", "'", "\"", "`",
                " or ", " and ", " union ", " select ", " insert ", " update ",
                " delete ", " drop ", " create ", " exec ", " xp_", "information_schema",
                "benchmark(", "sleep("
        };
        // Check for forbidden substrings
        for (String token : forbidden) {
            if (low.contains(token)) {
                return false;
            }
        }
        return true;
    }

    /**
     * Return time in ArgTimeUnit since epoch, using the format provided in dateformat.
     * If the dateformat is msec or sec then it will just interpret the string as a long.
     * The value will then depend on the provided input argument.
     *
     * @param val              the time string.
     * @param inputformat      the time format (sec, msec, iso, run).
     * @param outunit          the time unit (sec or msec).
     * @param customdateformat a user defined date format.
     * @return BigInteger
     */
    public BigInteger getTimeFromArg(String val, ArgTimeUnit inputformat, ArgTimeUnit outunit,
                                     String customdateformat) {
        try {
            log.debug("Get time from args: {} {} {} {}", val, inputformat, outunit,
                    customdateformat);

            if (val == null) {
                return null;
            }

            if (val.equalsIgnoreCase("INF")) {
                log.warn("The time will be set to INF : {}", CrestProperties.INFINITY);
                return CrestProperties.INFINITY.toBigInteger();
            }

            TimeParseStrategy strategy = strategyFactory.getStrategy(inputformat);
            BigInteger tepoch = strategy.parse(val, customdateformat);

            return convertOutputUnit(tepoch, outunit, inputformat);
        }
        catch (RuntimeException e) {
            throw new CdbBadRequestException("Error while parsing time", e);
        }
    }


    /**
     * Convert parsed time to requested output unit.
     *
     * @param tepoch      the time in milliseconds
     * @param outunit     the output unit
     * @param inputformat the input format
     * @return converted time value
     */
    private BigInteger convertOutputUnit(BigInteger tepoch, ArgTimeUnit outunit,
                                         ArgTimeUnit inputformat) {
        if (tepoch == null) {
            return null;
        }

        boolean isCoolFormat = inputformat == ArgTimeUnit.RUN || inputformat == ArgTimeUnit.RUN_LUMI
                || inputformat == ArgTimeUnit.NUMBER;

        if (outunit != null && outunit.equals(ArgTimeUnit.SEC)) {
            return tepoch.divide(BigInteger.valueOf(1000L));
        }
        else if (outunit != null && outunit.equals(ArgTimeUnit.COOL) && !isCoolFormat) {
            return tepoch.multiply(TO_NANO_SECONDS.toBigInteger());
        }
        return tepoch;
    }

    /**
     * @param arun The run number as a String.
     * @param lb   The lumi block as a String.
     * @return The COOL time.
     */
    public BigInteger getCoolRunLumi(final String arun, final String lb) {
        Long runlong = null;
        Long lblong = null;
        if (arun == null) {
            return null;
        }
        if (arun.equalsIgnoreCase("INF")) {
            runlong = COOL_MAX_RUN;
            lblong = COOL_MAX_LUMIBLOCK;
        }
        else {
            if (lb.equals("MAXLB")) {
                lblong = COOL_MAX_LUMIBLOCK;
            }
            else {
                lblong = Long.parseLong(lb);
            }
            runlong = Long.parseLong(arun);
        }
        return getCoolRunLumi(runlong, lblong);
    }

    /**
     * @param arun The run in long.
     * @param lb   The lb in long.
     * @return The COOL time.
     */
    public BigInteger getCoolRunLumi(final Long arun, final Long lb) {
        BigInteger irun = null;
        BigInteger ilb = null;
        BigInteger runlumi = null;
        BigInteger run = null;
        if (arun == null) {
            return null;
        }
        else {
            irun = BigInteger.valueOf(arun);
            if (lb == null) {
                ilb = BigInteger.valueOf(0L);
            }
            else {
                ilb = BigInteger.valueOf(lb);
            }
            run = irun.shiftLeft(COOLIOV_RUN_MASK);
            runlumi = run.or(ilb);
        }
        return runlumi;
    }
}
