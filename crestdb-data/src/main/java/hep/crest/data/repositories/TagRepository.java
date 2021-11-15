/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.pojo.Tag;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author formica
 * This repository is for the moment empty.
 */
@Repository
public interface TagRepository
        extends PagingAndSortingRepository<Tag, String>, QuerydslPredicateExecutor<Tag> {
    /**
     * @param name
     *            the String
     * @return Tag
     */
    Tag findByName(@Param("name") String name);

    /**
     * @param name
     *            the String
     * @return List<Tag>
     */
    List<Tag> findByNameLike(@Param("name") String name);
}
