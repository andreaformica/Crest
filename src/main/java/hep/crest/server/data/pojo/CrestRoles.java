package hep.crest.server.data.pojo;

import hep.crest.server.config.DatabasePropertyConfigurator;
import lombok.Data;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.EqualsAndHashCode;

/**
 * @author formica
 *
 */
@Entity
@Table(name = "CREST_MANAGER_ROLES", schema = DatabasePropertyConfigurator.SCHEMA_NAME)
@Data
public class CrestRoles {

    /**
     * The role ID.
     */
    @Id
    @EqualsAndHashCode.Exclude
    @Column(name = "CREST_ROLE_ID", unique = true, nullable = false)
    private Long id;
    /**
     * The role name.
     */
    @Column(name = "CREST_ROLE", unique = false, nullable = false, length = 100)
    private String role;
    /**
     * The role name.
     */
    @Column(name = "CREST_TAG_PATTERN", unique = false, nullable = false, length = 100)
    private String tagPattern;

}
