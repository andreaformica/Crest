package hep.crest.data.security.pojo;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import hep.crest.data.config.DatabasePropertyConfigurator;
import lombok.Data;

/**
 * @author formica
 *
 */
@Entity
@Table(name = "CREST_ROLES", schema = DatabasePropertyConfigurator.SCHEMA_NAME)
@Data
public class CrestRoles {

    /**
     * The role ID.
     */
    @Id
    @Column(name = "CREST_USRID", unique = true, nullable = false, length = 100)
    private String id;
    /**
     * The role name.
     */
    @Column(name = "CREST_USRROLE", unique = false, nullable = false, length = 100)
    private String role;

}
