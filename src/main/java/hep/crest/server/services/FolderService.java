/**
 *
 */
package hep.crest.server.services;

import hep.crest.server.data.pojo.CrestFolders;
import hep.crest.server.data.pojo.CrestRoles;
import hep.crest.server.data.repositories.CrestFoldersRepository;
import hep.crest.server.data.repositories.CrestRolesRepository;
import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.ConflictException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * A service to handle folders. This is not yet used in CMS nor ATLAS
 * implementation.
 *
 * @author formica
 *
 */
@Service
@Slf4j
public class FolderService {

    /**
     * Repository.
     */
    private CrestFoldersRepository crestFoldersRepository;

    /**
     * Repository.
     */
    private CrestRolesRepository crestRolesRepository;

    /**
     * Ctor with injection.
     * @param crestFoldersRepository
     * @param crestRolesRepository
     */
    @Autowired
    public FolderService(CrestFoldersRepository crestFoldersRepository,
            CrestRolesRepository crestRolesRepository) {
        this.crestFoldersRepository = crestFoldersRepository;
        this.crestRolesRepository = crestRolesRepository;
    }

    /**
     * @param entity
     *            the CrestFolders
     * @return CrestFolders
     * @throws AbstractCdbServiceException
     *             If an Exception occurred because pojo exists
     */
    @Transactional
    public CrestFolders insertFolder(CrestFolders entity) throws AbstractCdbServiceException {
        log.debug("Create CrestFolder from  {}", entity);
        final Optional<CrestFolders> tmpgt = crestFoldersRepository
                .findById(entity.getNodeFullpath());
        if (tmpgt.isPresent()) {
            log.debug("Cannot store folder {}  : resource already exists.. ", entity);
            throw new ConflictException(
                    "Folder already exists for name " + entity.getNodeFullpath());
        }
        CrestRoles role = new CrestRoles();
        role.setRole(entity.getGroupRole());
        role.setTagPattern(entity.getTagPattern());
        log.debug("Saving folder entity {}", entity);
        final CrestFolders saved = crestFoldersRepository.save(entity);
        crestRolesRepository.save(role);
        log.trace("Saved entity for folder and role: {}", saved);
        return saved;
    }

    /**
     *
     * @param schema
     * @return List of CrestFolders
     */
    public List<CrestFolders> findFoldersBySchema(String schema) {
        return crestFoldersRepository.findBySchemaName(schema);
    }
}
