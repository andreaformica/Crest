/**
 *
 */
package hep.crest.server.data.repositories;

import hep.crest.server.data.pojo.TagMeta;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.Modifying;

/**
 * Repository for tagmeta information.
 *
 * @author formica
 * CrudRepository already provides findById() which uses the @Id field (tagName)
 */
@Repository
public interface TagMetaRepository
        extends PagingAndSortingRepository<TagMeta, String>,
        CrudRepository<TagMeta, String> {


    /**
     * Delete tag meta by tag name.
     *
     * @param tagName
     *            the tag name
     * @return the number of removed rows
     */
    @Transactional
    @Modifying
    int deleteByTagName(@Param("tagName") String tagName);

}
