/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.exceptions.CdbNotFoundException;
import hep.crest.data.pojo.Payload;
import hep.crest.data.repositories.args.PayloadQueryArgs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

/**
 * Repository for Payloads metadata.
 *
 * @author formica
 *
 */
public interface PayloadRepositoryCustom {

    /**
     * Retrieve all payloads metadata with a size greater than "size".
     *
     * @param args
     * @param preq the Page request
     * @return List of Payloads
     */
    Page<Payload> findPayloadsList(@Param("args") PayloadQueryArgs args, Pageable preq) throws CdbNotFoundException;

}
