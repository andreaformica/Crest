/**
 *
 */
package hep.crest.data.security.pojo;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * @author formica
 *
 */
@Repository
public interface CrestFoldersRepository extends CrudRepository<CrestFolders, String> {
    /**
     * @param group
     *            the String
     * @return List<CrestFolders>
     */
    List<CrestFolders> findByGroupRole(@Param("group") String group);

    /**
     * @param schema
     *            the String
     * @return List<CrestFolders>
     */
    List<CrestFolders> findBySchemaName(@Param("schema") String schema);

}
