/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.pojo.TagMeta;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import javax.transaction.Transactional;
import java.util.Optional;

/**
 * Repository for tagmeta information.
 *
 * @author formica
 * This repository is for the moment empty.
 */
@Repository
public interface TagMetaRepository
        extends PagingAndSortingRepository<TagMeta, String> {

    /**
     * @param name
     *            the String
     * @return Optional<TagMeta>
     */
    @Transactional
    Optional<TagMeta> findByTagName(@Param("name") String name);

}
