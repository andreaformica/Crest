/**
 *
 */
package hep.crest.server.data.repositories;

import hep.crest.server.data.pojo.Tag;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * @author formica
 * This repository is for the moment empty.
 */
@Repository
public interface TagRepository
        extends PagingAndSortingRepository<Tag, Long>,
        CrudRepository<Tag, Long>, TagRepositoryCustom {

    /**
     * @param name
     *            the String
     * @return List<Tag>
     */
    List<Tag> findByNameLike(@Param("name") String name);

    /**
     * @param name
     *            the String
     * @return Optional Tag
     */
    Optional<Tag> findByName(@Param("name") String name);
}
