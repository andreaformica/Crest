/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.pojo.PayloadInfoData;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Repository for Streamer Info.
 *
 * @author formica
 *
 */
@Repository
public interface PayloadInfoDataRepository
        extends CrudRepository<PayloadInfoData, String> {

    @Transactional
    Optional<PayloadInfoData> findById(String s);

}
