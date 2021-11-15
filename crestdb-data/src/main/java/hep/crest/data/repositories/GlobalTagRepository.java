/**
 *
 */
package hep.crest.data.repositories;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.pojo.GlobalTag;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * Repository for global tags.
 *
 * @author formica
 * @author rsipos
 *
 */
@Repository
@Transactional(readOnly = true)
public interface GlobalTagRepository extends PagingAndSortingRepository<GlobalTag, String>,
                                             QuerydslPredicateExecutor<GlobalTag> {

    /**
     * @param name
     *            the String
     * @return Optional<GlobalTag>
     */
    @Query("SELECT distinct p FROM GlobalTag p JOIN FETCH p.globalTagMaps maps JOIN FETCH maps.tag "
           + "WHERE maps.id.globalTagName = (:name)")
    Optional<GlobalTag> findByNameAndFetchTagsEagerly(@Param("name") String name) throws AbstractCdbServiceException;

    /**
     * @param name
     *            the String
     * @param record
     *            the String
     * @return Optional<GlobalTag>
     */
    @Query("SELECT distinct p FROM GlobalTag p JOIN FETCH p.globalTagMaps maps JOIN FETCH maps.tag "
           + "WHERE maps.id.globalTagName = (:name) and maps.id.record = (:record)")
    Optional<GlobalTag> findByNameAndFetchRecordTagsEagerly(@Param("name") String name,
                                                            @Param("record") String record)
            throws AbstractCdbServiceException;

    /**
     * @param name
     *            the String
     * @param record
     *            the String
     * @param label
     *            the String
     * @return Optional<GlobalTag>
     */
    @Query("SELECT distinct p FROM GlobalTag p JOIN FETCH p.globalTagMaps maps JOIN FETCH maps.tag "
           + "WHERE maps.id.globalTagName = (:name) and maps.id.record = (:record) and maps.id.label = (:label) ")
    Optional<GlobalTag> findByNameAndFetchSpecifiedTagsEagerly(@Param("name") String name,
                                                               @Param("record") String record,
                                                               @Param("label") String label)
            throws AbstractCdbServiceException;

    /**
     * @param name
     *            the String
     * @param tag
     *            the String
     * @return Optional<GlobalTag>
     */
    @Query("SELECT distinct p FROM GlobalTag p JOIN FETCH p.globalTagMaps maps JOIN FETCH maps.tag "
           + "WHERE maps.id.globalTagName = (:name) and maps.tag.name like (:tag)")
    Optional<GlobalTag> findByNameAndFilterTagsEagerly(@Param("name") String name, @Param("tag") String tag)
            throws AbstractCdbServiceException;

    /**
     * @param name
     *            the String
     * @return Optional<GlobalTag>
     */
    Optional<GlobalTag> findByName(@Param("name") String name) throws AbstractCdbServiceException;

    /**
     * @param name
     *            the String
     * @return List<GlobalTag>
     */
    List<GlobalTag> findByNameLike(@Param("name") String name);

}
