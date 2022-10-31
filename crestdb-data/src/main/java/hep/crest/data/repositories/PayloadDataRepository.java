/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.pojo.PayloadData;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository for Payload DATA.
 *
 * @author formica
 *
 */
public interface PayloadDataRepository
        extends CrudRepository<PayloadData, String>, PayloadDataRepositoryCustom {

}
