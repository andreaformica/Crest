package hep.crest.testutils;

import hep.crest.data.serializers.CustomTimeDeserializer;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;

public class DateFormatExample {

    public static void main(String[] args) {
        try {
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
                    .append(DateTimeFormatter.ISO_LOCAL_DATE_TIME)
                    .optionalStart()
                    .appendPattern(".SSS")
                    .optionalEnd()
                    .optionalStart()
                    .appendZoneOrOffsetId()
                    .optionalEnd()
                    .optionalStart()
                    .appendOffset("+HHMM", "0000")
                    .optionalEnd()
                    .toFormatter();

            String adatestr = "2013-09-20T07:00:33+0000";
            System.out.println("input date is: " + adatestr);
            OffsetDateTime odt = OffsetDateTime.parse(adatestr, formatter);
            System.out.println("odt is: " + odt.toString());
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}