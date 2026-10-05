package hep.crest.server.timeseries.model;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.math.BigInteger;
import java.util.Date;

/**
 * IovId.
 */
// This object represents an ID and it is embedded in Iov.
// Define default constructors.
// Define equals and hashcode methods.
// These are essential for ID comparison.
@Embeddable
@Data
public class TimeSerieIovId implements java.io.Serializable {

    /**
     * Serializer.
     */
    private static final long serialVersionUID = -2770785371714771938L;
    /**
     * The channel Id. Here we use the PK of channels table.
     * This column should reference an existing channel.
     */
    @Column(name = "CHANNEL_ID", nullable = false, precision = 15, scale = 0)
    private Integer channelId;
    /**
     * The tag ID. Here we use the numeric ID of the tag.
     * This column should reference an existing tag ID.
     */
    @Column(name = "TAG_ID", nullable = false, precision = 15, scale = 0)
    private Long tagId;
    /**
     * The since time.
     * The concept of time is flexible: can be any kind of Long.
     */
    @Column(name = "SINCE", nullable = false, precision = 22, scale = 0)
    private BigInteger since;
    /**
     * The insertion time.
     * This time is used to determine Iovs which were inserted before a snapshot time.
     */
    @Column(name = "INSERTION_TIME", nullable = false, length = 11)
    @EqualsAndHashCode.Exclude
    private Date insertionTime;

}
