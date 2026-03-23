package hep.crest.server.timeseries.model.repositories;

import lombok.Data;
import lombok.experimental.Accessors;

import java.math.BigInteger;

@Data
@Accessors(fluent = true)
public class TimeSerieIovQueryArgs {

    /**
     * Pattern to search the channel name.
     */
    private String channelName;
    /**
     * Pattern to search the node fullpath.
     */
    private String tagName;
    /**
     * Search by channel id.
     */
    private Integer channelId;
    /**
     * Search by since.
     */
    private BigInteger since;
    /**
     * Search by until.
     */
    private BigInteger until;
}
