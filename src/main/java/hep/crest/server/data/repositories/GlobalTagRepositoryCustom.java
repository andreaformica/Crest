package hep.crest.server.data.repositories;

import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.data.pojo.GlobalTag;
import hep.crest.server.data.repositories.args.GtagQueryArgs;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

/**
 * The main method to retrieve IOVs.
 */
public interface GlobalTagRepositoryCustom {

    /**
     * General purpose GlobalTag retrieval.
     *
     * @param args
     * @param preq
     * @return Page of GlobalTag.
     * @throws CdbNotFoundException
     */
    Page<GlobalTag> findGlobalTagList(GtagQueryArgs args, Pageable preq) throws CdbNotFoundException;

    /**
     * General purpose GlobalTag Fetching tags retrieval.
     *
     * @param name
     * @param record
     * @param label
     * @return Optional of GlobalTag.
     * @throws CdbNotFoundException
     */
    Optional<GlobalTag> findGlobalTagFetchTags(String name, String record, String label)
            throws CdbNotFoundException;

}
