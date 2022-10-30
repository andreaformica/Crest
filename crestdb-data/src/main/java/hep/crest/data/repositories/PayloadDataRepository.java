/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.pojo.PayloadData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository for Payload DATA.
 *
 * @author formica
 *
 */
@Repository
public interface PayloadDataRepository
        extends CrudRepository<PayloadData, String>, PayloadDataRepositoryCustom {

}
