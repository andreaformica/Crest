package hep.crest.server.data.runinfo.pojo;
// Generated Aug 2, 2016 3:50:25 PM by Hibernate Tools 3.2.2.GA

import hep.crest.server.config.DatabasePropertyConfigurator;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import lombok.experimental.Accessors;

import jakarta.persistence.AttributeOverride;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Date;

/**
 * GlobalTag generated by hbm2java.
 */
@Entity
@Table(name = "RUN_LUMI_INFO", schema = DatabasePropertyConfigurator.SCHEMA_NAME)
@Data
@Accessors(chain = true)
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class RunLumiInfo implements java.io.Serializable {

    /**
     * The RunLumi ID.
     */
    @EmbeddedId
    @AttributeOverride(name = "RUN",
            column = @Column(name = "RUN", nullable = false))
    @AttributeOverride(name = "LUMI_BLOCK",
            column = @Column(name = "LUMI_BLOCK", nullable = false))
    private RunLumiId id;

    /**
     * The start time of this run.
     */
    @Column(name = "START_TIME", nullable = false, precision = 38, scale = 0)
    private BigInteger starttime;
    /**
     * The end time of this run.
     */
    @Column(name = "END_TIME", nullable = false, precision = 38, scale = 0)
    private BigInteger endtime;
    /**
     * The insertion time.
     */
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "INSERTION_TIME", nullable = false, updatable = true, length = 11)
    @EqualsAndHashCode.Exclude
    private Date insertionTime = null;

    /**
     * Before saving.
     *
     * @return
     */
    @PrePersist
    public void prePersist() {
        if (this.insertionTime == null) {
            final Timestamp now = Timestamp.from(Instant.now());
            this.insertionTime = now;
        }
    }
}
