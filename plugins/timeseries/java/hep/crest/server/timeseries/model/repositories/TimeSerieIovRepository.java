package hep.crest.server.timeseries.model.repositories;

import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import hep.crest.server.timeseries.model.TimeSerieIov;
import hep.crest.server.timeseries.model.TimeSerieIovId;

import java.math.BigInteger;
import java.util.List;

/**
 * Repository for TimeSerieIov.
 *
 * @author formica
 *
 */
@Repository
public interface TimeSerieIovRepository extends CrudRepository<TimeSerieIov, TimeSerieIovId>,
        TimeSerieIovRepositoryCustom {

    /**
     * Get the list of iovs for a given node.
     * @param tagName the tag name
     * @return List of Iov
     */
    List<TimeSerieIov> findByTagName(@Param("tag_name") String tagName);

    /**
     * @param tagName the tagName
     * @param chanId  the id of the channel provided by experts
     * @param since    the BigDecimal
     * @return Iov
     */
    @Query("SELECT distinct p FROM TimeSerieIov p "
            + "WHERE p.tag.name = (:tagName) AND p.id.channelId = (:chanId) "
            + "AND p.id.since < (:since) AND p.until > (:since) ")
    TimeSerieIov selectAtTime(@Param("tagName") String tagName, @Param("chanId") Integer chanId,
                              @Param("since") BigInteger since);

    /**
     * Get the max since for a given tag.
     * @param tagName the tag name
     * @param chanId  the id of the channel provided by experts
     * @return  BigInteger
     */
    @Query("SELECT max(p.id.since) FROM TimeSerieIov p "
            + "WHERE p.tag.name = (:tagName) AND p.id.channelId = (:chanId) ")
    BigInteger checkLast(@Param("tagName") String tagName, @Param("chanId") Integer chanId);

    /**
     * Get the min since among the last entries for a given node.
     * @param tagName the tag name
     * @return  BigInteger
     */
    @Query(nativeQuery = true,
            value = "SELECT min(msince) from ("
                    + "SELECT max(p.since) as msince, p.channel_id FROM TS_IOV p "
                    + "WHERE p.tag.name = (:tagName) group by p.channel_id ) group by msince")
    BigInteger checkMinLastIov(@Param("tagName") String tagName);

    /**
     * Delete all entried for tag tagName.
     * @param tagName the tag name
     * @return number of deleted entries
     */
    @Modifying
    @Query("DELETE FROM TimeSerieIov p "
            + "WHERE p.tag.name = (:tagName) ")
    Integer deleteByTagName(@Param("tagName") String tagName);
}
