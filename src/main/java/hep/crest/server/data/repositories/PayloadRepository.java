/**
 *
 */
package hep.crest.server.data.repositories;

import hep.crest.server.data.pojo.Payload;
import org.springframework.data.repository.PagingAndSortingRepository;
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
        extends PagingAndSortingRepository<Payload, String>, PayloadRepositoryCustom {

    @Override
    Optional<Payload> findById(String s);
}
