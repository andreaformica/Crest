/**
 *
 */
package hep.crest.data.security.pojo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * @author formica
 *
 */
@Repository
public interface UserRepository extends CrudRepository<CrestUser, String> {

}
