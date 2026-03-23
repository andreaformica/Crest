package hep.crest.server.timeseries.model;

import lombok.Data;
import java.math.BigInteger;

/**
 * TimeSerieData.
 * This class is used to store the data of a time series.
 */
@Data
public class TimeSerieData {
    /**
     * The channelId.
     */
    private Integer channelId;
    /**
     * The since value of the time series.
     */
    private BigInteger until;
    /**
     * The data of the time series.
     */
    private String data;
}
