/**
 *
 */
package hep.crest.server.data.repositories;

import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Repository for IOVs.
 *
 * @author formica
 *
 */
@Repository
public interface IovRepository
        extends PagingAndSortingRepository<Iov, IovId>, IovRepositoryCustom {

    /**
     * Retrieve all iovs for a tag. Used when deleting.
     *
     * @param name
     * @param preq the Page request
     * @return List of Iovs
     */
    Page<Iov> findByIdTagName(@Param("name") String name, Pageable preq);

    /**
     * Retrieve all iovs for a given hash. Used when deleting.
     *
     * @param hash
     * @return List of Iovs
     */
    List<Iov> findByPayloadHash(@Param("hash") String hash);

    /**
     * Check existence of IOV by unique fields.
     * @param name     the String
     * @param since    the BigDecimal
     * @param hash the Hash
     * @return Iov
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    @Query("SELECT distinct p FROM Iov p "
           + "WHERE p.id.tagName = (:name) AND p.id.since = (:since) AND p.payloadHash = (:hash)")
    Iov exists(@Param("name") String name, @Param("since") BigInteger since, @Param("hash") String hash)
            throws CdbNotFoundException;

    /**
     * @param name     the String
     * @param since    the BigDecimal
     * @param snapshot the Date
     * @return List<Iov>
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    @Query("SELECT distinct p FROM Iov p JOIN FETCH p.tag tag "
           + "WHERE tag.name = (:name) AND p.id.since = ("
           + "SELECT max(pi.id.since) FROM Iov pi JOIN pi.tag pt "
           + "WHERE pt.name = (:name) AND pi.id.since <= :since AND pi.id.insertionTime <= :snap) "
           + "ORDER BY p.id.since ASC, p.id.insertionTime DESC")
    List<Iov> selectAtTime(@Param("name") String name, @Param("since") BigInteger since,
                           @Param("snap") Date snapshot) throws CdbNotFoundException;

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
    List<Iov> getRange(@Param("name") String name, @Param("since") BigInteger since,
                       @Param("until") BigInteger until,
                       @Param("snap") Date snapshot);

}
