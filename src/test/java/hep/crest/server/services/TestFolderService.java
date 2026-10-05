package hep.crest.server.services;

import hep.crest.server.data.pojo.CrestFolders;
import hep.crest.server.data.pojo.CrestRoles;
import hep.crest.server.data.repositories.CrestFoldersRepository;
import hep.crest.server.data.repositories.CrestRolesRepository;
import hep.crest.server.exceptions.ConflictException;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for FolderService.
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class TestFolderService {

    @Autowired
    private FolderService folderService;

    @Autowired
    private CrestFoldersRepository crestFoldersRepository;

    @Autowired
    private CrestRolesRepository crestRolesRepository;

    /**
     * Test successful insertion of a folder.
     */
    @Test
    public void testInsertFolderSuccess() {
        log.info("=======> testInsertFolderSuccess");

        String fullPath = "/test/folder/path-" + System.currentTimeMillis();
        String tagPattern = "TEST-TAG-PATTERN-" + System.currentTimeMillis();

        CrestFolders folder = new CrestFolders();
        folder.setNodeFullpath(fullPath);
        folder.setSchemaName("TEST_SCHEMA");
        folder.setNodeName("test-node");
        folder.setNodeDescription("Test folder for FolderService tests");
        folder.setTagPattern(tagPattern);
        folder.setGroupRole("test-role");

        // Insert the folder
        CrestFolders saved = folderService.insertFolder(folder);

        assertThat(saved).isNotNull();
        assertThat(saved.getNodeFullpath()).isEqualTo(fullPath);
        assertThat(saved.getSchemaName()).isEqualTo("TEST_SCHEMA");
        assertThat(saved.getNodeName()).isEqualTo("test-node");
        assertThat(saved.getTagPattern()).isEqualTo(tagPattern);
        assertThat(saved.getGroupRole()).isEqualTo("test-role");

        log.info("Successfully inserted folder: {}", saved);

        // Verify role was also created
        List<CrestRoles> roles = StreamSupport
                .stream(crestRolesRepository.findAll().spliterator(), false)
                .toList();
        boolean roleFound = roles.stream()
                .anyMatch(r -> r.getRole().equals("test-role") && r.getTagPattern().equals(tagPattern));
        assertThat(roleFound).isTrue();
        log.info("Role was also created for the folder");

        // Cleanup
        crestFoldersRepository.deleteById(fullPath);
    }

    /**
     * Test that inserting a duplicate folder throws ConflictException.
     */
    @Test
    public void testInsertFolderDuplicate() {
        log.info("=======> testInsertFolderDuplicate");

        String fullPath = "/test/folder/duplicate-" + System.currentTimeMillis();
        String tagPattern1 = "TEST-TAG-DUP-1-" + System.currentTimeMillis();
        String tagPattern2 = "TEST-TAG-DUP-2-" + System.currentTimeMillis();

        CrestFolders folder1 = new CrestFolders();
        folder1.setNodeFullpath(fullPath);
        folder1.setSchemaName("TEST_SCHEMA");
        folder1.setNodeName("test-node-1");
        folder1.setNodeDescription("First folder");
        folder1.setTagPattern(tagPattern1);
        folder1.setGroupRole("test-role-1");

        // Insert the first folder
        CrestFolders saved = folderService.insertFolder(folder1);
        assertThat(saved).isNotNull();
        log.info("First folder inserted: {}", saved);

        // Try to insert a folder with the same fullPath - should fail
        CrestFolders folder2 = new CrestFolders();
        folder2.setNodeFullpath(fullPath); // Same path
        folder2.setSchemaName("TEST_SCHEMA_2");
        folder2.setNodeName("test-node-2");
        folder2.setNodeDescription("Second folder with same path");
        folder2.setTagPattern(tagPattern2);
        folder2.setGroupRole("test-role-2");

        assertThatThrownBy(() -> folderService.insertFolder(folder2))
                .isInstanceOf(ConflictException.class)
                .hasMessageContaining("Folder already exists for name");

        log.info("Duplicate folder insertion correctly rejected");

        // Cleanup
        crestFoldersRepository.deleteById(fullPath);
    }

    /**
     * Test finding folders by schema.
     */
    @Test
    public void testFindFoldersBySchema() {
        log.info("=======> testFindFoldersBySchema");

        String schemaName = "FIND_TEST_SCHEMA_" + System.currentTimeMillis();
        String fullPath1 = "/test/schema/folder1-" + System.currentTimeMillis();
        String fullPath2 = "/test/schema/folder2-" + System.currentTimeMillis();
        String tagPattern1 = "FIND-TAG-1-" + System.currentTimeMillis();
        String tagPattern2 = "FIND-TAG-2-" + System.currentTimeMillis();

        // Create two folders with the same schema
        CrestFolders folder1 = new CrestFolders();
        folder1.setNodeFullpath(fullPath1);
        folder1.setSchemaName(schemaName);
        folder1.setNodeName("folder1");
        folder1.setNodeDescription("Folder 1");
        folder1.setTagPattern(tagPattern1);
        folder1.setGroupRole("role1");

        CrestFolders folder2 = new CrestFolders();
        folder2.setNodeFullpath(fullPath2);
        folder2.setSchemaName(schemaName);
        folder2.setNodeName("folder2");
        folder2.setNodeDescription("Folder 2");
        folder2.setTagPattern(tagPattern2);
        folder2.setGroupRole("role2");

        folderService.insertFolder(folder1);
        folderService.insertFolder(folder2);

        // Find folders by schema
        List<CrestFolders> foundFolders = folderService.findFoldersBySchema(schemaName);

        assertThat(foundFolders).isNotNull();
        assertThat(foundFolders).hasSize(2);
        assertThat(foundFolders.stream().map(CrestFolders::getNodeFullpath))
                .contains(fullPath1, fullPath2);

        log.info("Found {} folders for schema {}", foundFolders.size(), schemaName);

        // Cleanup
        crestFoldersRepository.deleteById(fullPath1);
        crestFoldersRepository.deleteById(fullPath2);
    }

    /**
     * Test finding folders by non-existent schema returns empty list.
     */
    @Test
    public void testFindFoldersBySchemaNotFound() {
        log.info("=======> testFindFoldersBySchemaNotFound");

        List<CrestFolders> foundFolders = folderService.findFoldersBySchema("NON_EXISTENT_SCHEMA_12345");

        assertThat(foundFolders).isNotNull();
        assertThat(foundFolders).isEmpty();

        log.info("Correctly returned empty list for non-existent schema");
    }

    /**
     * Test that inserting a folder also creates a CrestRole.
     */
    @Test
    public void testInsertFolderCreatesRole() {
        log.info("=======> testInsertFolderCreatesRole");

        String fullPath = "/test/folder/with-role-" + System.currentTimeMillis();
        String tagPattern = "ROLE-TEST-PATTERN-" + System.currentTimeMillis();
        String groupRole = "special-role-" + System.currentTimeMillis();

        CrestFolders folder = new CrestFolders();
        folder.setNodeFullpath(fullPath);
        folder.setSchemaName("ROLE_TEST_SCHEMA");
        folder.setNodeName("role-test-node");
        folder.setNodeDescription("Folder to test role creation");
        folder.setTagPattern(tagPattern);
        folder.setGroupRole(groupRole);

        // Insert the folder
        CrestFolders saved = folderService.insertFolder(folder);
        assertThat(saved).isNotNull();

        // Verify the role was created with correct values
        List<CrestRoles> allRoles = StreamSupport
                .stream(crestRolesRepository.findAll().spliterator(), false)
                .toList();
        CrestRoles createdRole = allRoles.stream()
                .filter(r -> r.getRole().equals(groupRole) && r.getTagPattern().equals(tagPattern))
                .findFirst()
                .orElse(null);

        assertThat(createdRole).isNotNull();
        assertThat(createdRole.getRole()).isEqualTo(groupRole);
        assertThat(createdRole.getTagPattern()).isEqualTo(tagPattern);

        log.info("Role created with folder: role={}, tagPattern={}",
                createdRole.getRole(), createdRole.getTagPattern());

        // Cleanup
        crestFoldersRepository.deleteById(fullPath);
    }
}

