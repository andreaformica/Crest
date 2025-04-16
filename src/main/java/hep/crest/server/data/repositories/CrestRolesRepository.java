/**
 *
 */
package hep.crest.server.data.repositories;

import hep.crest.server.data.pojo.CrestRoles;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author formica
 *
 */
@Repository
public interface CrestRolesRepository extends CrudRepository<CrestRoles, String> {
    /**
     * @param role
     *            the String
     * @return List<CrestFolders>
     */
    List<CrestRoles> findByRole(@Param("role") String role);


    /**
     * Find by role and tag pattern.
     * @param tag
     * @return the list of roles
     */
    @Query("SELECT distinct p FROM CrestRoles p "
            + "WHERE (:tag) like p.tagPattern || '%'")
    List<CrestRoles> findMatchingTagPatterns(@Param("tag") String tag);
}
