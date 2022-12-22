package hep.crest.server.data.pojo;

import hep.crest.server.config.DatabasePropertyConfigurator;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author formica
 */
@Entity
@Table(name = "CREST_USERS", schema = DatabasePropertyConfigurator.SCHEMA_NAME)
@Data
public class CrestUser {

    /**
     * The id of the user.
     */
    @Id
    @Column(name = "CREST_USRID", unique = true, nullable = false, length = 100)
    private String id;
    /**
     * The user name.
     */
    @Column(name = "CREST_USRNAME", unique = true, nullable = false, length = 100)
    private String username;
    /**
     * The password.
     */
    @Column(name = "CREST_USRPSS", unique = true, nullable = false, length = 100)
    private String password;

}
