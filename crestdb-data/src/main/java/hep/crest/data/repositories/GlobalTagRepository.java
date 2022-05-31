/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.pojo.GlobalTag;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for global tags.
 *
 * @author formica
 * @author rsipos
 *
 */
@Repository
@Transactional(readOnly = true)
public interface GlobalTagRepository extends PagingAndSortingRepository<GlobalTag, String>,
                                             GlobalTagRepositoryCustom {


    /**
     * @param name
     *            the String
     * @return Optional<GlobalTag>
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    Optional<GlobalTag> findByName(@Param("name") String name) throws AbstractCdbServiceException;

    /**
     * @param name
     *            the String
     * @return List<GlobalTag>
     */
    List<GlobalTag> findByNameLike(@Param("name") String name);

}
