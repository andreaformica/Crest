/**
 *
 */
package hep.crest.server.data.repositories;

import hep.crest.server.data.pojo.CrestUser;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * @author formica
 *
 */
@Repository
public interface CrestUserRepository extends CrudRepository<CrestUser, String> {
    /**
     * @param name
     *            the String
     * @return Optional<CrestUser>
     */
    Optional<CrestUser> findByUsername(@Param("name") String name);

}
