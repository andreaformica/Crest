/**
 *
 */
package hep.crest.server.data.runinfo.repositories;

import hep.crest.server.data.runinfo.pojo.RunLumiInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigInteger;

/**
 * @author formica
 *
 */
@Transactional(readOnly = true)
public interface RunLumiInfoRepository
        extends PagingAndSortingRepository<RunLumiInfo, BigInteger> {

    /**
     * @param run
     *            the BigInteger
     * @return RunLumiInfo
     */
    RunLumiInfo findByRunNumber(@Param("runNumber") BigInteger run);

    /**
     * @param lower
     *            the BigInteger
     * @param upper
     *            the BigInteger
     * @param preq
     *            the PageRequest
     * @return Page<RunLumiInfo>
     */
    @Query("SELECT distinct p FROM RunLumiInfo p " + "WHERE p.runNumber <= ("
           + "SELECT min(pi.runNumber) FROM RunLumiInfo pi " + "WHERE pi.runNumber >= (:upper)) "
           + "AND p.runNumber >= (:lower)" + "ORDER BY p.runNumber ASC")
    Page<RunLumiInfo> findByRunNumberInclusive(@Param("lower") BigInteger lower,
                                               @Param("upper") BigInteger upper, Pageable preq);

    /**
     * @param lower
     *            the Date
     * @param upper
     *            the Date
     * @param preq
     *            the PageRequest
     * @return Page<RunLumiInfo>
     */
    @Query("SELECT distinct p FROM RunLumiInfo p "
           + "WHERE p.starttime <= ("
           + "SELECT min(pi.starttime) FROM RunLumiInfo pi "
           + "WHERE pi.starttime >= (:upper)) "
           + "AND p.endtime >= (:lower)"
           + "ORDER BY p.runNumber ASC")
    Page<RunLumiInfo> findByDateInclusive(@Param("lower") BigInteger lower, @Param("upper") BigInteger upper,
                                          Pageable preq);
}
