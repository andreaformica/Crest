/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.pojo.Payload;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repository for IOVs.
 *
 * @author formica
 *
 */
@Repository
public interface PayloadRepository
        extends PagingAndSortingRepository<Payload, String> {

    @Override
    Optional<Payload> findById(String s);

    /**
     * Retrieve all payloads metadata with a size greater than "size".
     *
     * @param size
     * @param preq the Page request
     * @return List of Payloads
     */
    Page<Payload> findBySizeGreaterThan(@Param("size") Integer size, Pageable preq);

    /**
     * Retrieve all payloads metadata for object type.
     *
     * @param objtype
     * @param preq
     * @return List of Payloads
     */
    Page<Payload> findByObjectType(@Param("objtype") String objtype, Pageable preq);
}
