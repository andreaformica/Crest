package hep.crest.data.repositories;

import hep.crest.data.pojo.Iov;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

public interface IIovQuery {
    /**
     * @param name the String
     * @return List<Iov>
     */
    List<Iov> findByIdTagName(@Param("name") String name);

    /**
     * @param name  the String
     * @param since the BigDecimal
     * @param hash  the String
     * @return Iov
     */
    @Query("SELECT distinct p FROM Iov p JOIN FETCH p.tag tag "
           + "WHERE tag.name = (:name) and p.id.since = :since and p.payloadHash = (:hash)")
    Iov findBySinceAndTagNameAndHash(@Param("name") String name, @Param("since") BigDecimal since,
                                     @Param("hash") String hash);

    /**
     * @param name  the String
     * @param since the BigDecimal
     * @param until the BigDecimal
     * @return List<Iov>
     */
    @Query("SELECT distinct p FROM Iov p JOIN FETCH p.tag tag "
           + "WHERE tag.name = (:name) and p.id.since >= :since AND  p.id.since < :until "
           + "ORDER BY p.id.since ASC, p.id.insertionTime DESC")
    List<Iov> selectLatestByGroup(@Param("name") String name, @Param("since") BigDecimal since,
                                  @Param("until") BigDecimal until);

    /**
     * This method is like the getRange method, but it does not include the IOV before the given since.
     * It will provide the same result as getRange only if the since time provided is equivalent
     * to the first since selected in the DB. For other cases it will not contain the first IOV.
     *
     * @param name     the String
     * @param since    the BigDecimal
     * @param until    the BigDecimal
     * @param snapshot the Date
     * @return List<Iov>
     */
    @Query("SELECT distinct p FROM Iov p JOIN FETCH p.tag tag "
           + "WHERE tag.name = (:name) AND p.id.since >= :since AND  p.id.since < :until "
           + " AND p.id.insertionTime <= :snap "
           + "ORDER BY p.id.since ASC, p.id.insertionTime DESC")
    List<Iov> selectSnapshotByGroup(@Param("name") String name, @Param("since") BigDecimal since,
                                    @Param("until") BigDecimal until, @Param("snap") Date snapshot);

    /**
     * @param name     the String
     * @param since    the BigDecimal
     * @param snapshot the Date
     * @return List<Iov>
     */
    @Query("SELECT distinct p FROM Iov p JOIN FETCH p.tag tag "
           + "WHERE tag.name = (:name) AND p.id.since = ("
           + "SELECT max(pi.id.since) FROM Iov pi JOIN pi.tag pt "
           + "WHERE pt.name = (:name) AND pi.id.since <= :since AND pi.id.insertionTime <= :snap) "
           + "ORDER BY p.id.since ASC, p.id.insertionTime DESC")
    List<Iov> selectAtTime(@Param("name") String name, @Param("since") BigDecimal since,
                           @Param("snap") Date snapshot);

    /**
     * @param name     the String
     * @param since    the BigDecimal
     * @param until    the BigDecimal
     * @param snapshot the Date
     * @return List<Iov>
     */
    @Query("SELECT distinct p FROM Iov p JOIN FETCH p.tag tag "
           + "WHERE tag.name = (:name) AND p.id.since >= ("
           + "SELECT max(pi.id.since) FROM Iov pi JOIN pi.tag pt "
           + "WHERE pt.name = (:name) AND pi.id.since <= :since AND pi.id.insertionTime <= :snap) "
           + "AND p.id.since <= :until AND p.id.insertionTime <= :snap "
           + "ORDER BY p.id.since ASC, p.id.insertionTime DESC")
    List<Iov> getRange(@Param("name") String name, @Param("since") BigDecimal since,
                       @Param("until") BigDecimal until,
                       @Param("snap") Date snapshot);

    /**
     * @param name the String
     * @return List<Iov>
     */
    @Query("SELECT distinct p FROM Iov p JOIN FETCH p.tag tag " + "WHERE tag.name = (:name) "
           + "ORDER BY p.id.since ASC, p.id.insertionTime DESC")
    List<Iov> selectLatestByTag(@Param("name") String name);

    /**
     * @param tagname  the String
     * @param snapshot the Date
     * @return List<Iov>
     */
    @Query("SELECT distinct p FROM Iov p "
           + "WHERE p.id.tagName = (:tagname) AND p.id.insertionTime <= :snap "
           + "ORDER BY p.id.since ASC, p.id.insertionTime DESC")
    List<Iov> selectSnapshot(@Param("tagname") String tagname, @Param("snap") Date snapshot);
}
