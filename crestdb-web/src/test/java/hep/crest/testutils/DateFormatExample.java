package hep.crest.testutils;

import hep.crest.data.serializers.CustomTimeDeserializer;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateFormatExample {

    public static void main(String[] args) {
        try {
            DateTimeFormatter fmt = DateTimeFormatter.ISO_OFFSET_DATE_TIME;
            DateTimeFormatter formatter1 = new DateTimeFormatterBuilder()
                    // date/time
                    .appendPattern("yyyy-MM-dd HH:mm:ss z")
                    // optional fraction of seconds (from 0 to 9 digits)
                    .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd()
                    // offset
                    .appendPattern("xxx")
                    // create formatter
                    .toFormatter();

            DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd()
                    .appendPattern("x")
                    .toFormatter();

            String adatestr = "2013-09-20T07:00:33.09999+0100";
            System.out.println(" - input date is: " + adatestr);
            OffsetDateTime odt = OffsetDateTime.parse(adatestr, formatter);
            System.out.println("odt is: " + odt.toString());
            adatestr = "2013-09-20T07:00:33+0100";
            System.out.println(" - input date is: " + adatestr);
            odt = OffsetDateTime.parse(adatestr, formatter);
            System.out.println("odt is: " + odt.toString());

            adatestr = "2013-09-20 07:00:33";
            DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
            LocalDateTime ldt = LocalDateTime.parse(adatestr, formatter2);
            System.out.println("ldt is: " + ldt.toString());

            final Instant now = Instant.now();
            OffsetDateTime zdt = now.atOffset(ZoneOffset.UTC);
            System.out.println("zdt is: " + zdt.toString());

            DateTimeFormatter formatter3 = new DateTimeFormatterBuilder()
                    .parseCaseInsensitive()
                    .appendPattern("yyyy-MM-dd'T'HH:mm:ss")
                    .optionalStart().appendFraction(ChronoField.NANO_OF_SECOND, 0, 9, true).optionalEnd()
                    .appendPattern("xx")
                    .toFormatter();
            adatestr = "2015-09-20T07:00:33+0100";
            System.out.println(" - fmt3 input date is: " + adatestr);
            odt = OffsetDateTime.parse(adatestr, formatter3);
            System.out.println("odt is: " + odt.toString());
            adatestr = "2015-09-20T07:00:33+02";
            System.out.println(" - fmt3 input date is: " + adatestr);
            odt = OffsetDateTime.parse(adatestr, formatter3);
            System.out.println("odt is: " + odt.toString());

        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}