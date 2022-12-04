package hep.crest.server.data.pojo;

import hep.crest.server.config.DatabasePropertyConfigurator;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import lombok.experimental.Accessors;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

/**
 * @author formica
 *
 */
@Entity
@Table(name = "CREST_FOLDERS", schema = DatabasePropertyConfigurator.SCHEMA_NAME)
// Set all lombok annotation for method generation.
@Accessors(fluent = true)
@Getter
@Setter
@NoArgsConstructor
@ToString
@EqualsAndHashCode
public class CrestFolders {

    /**
     * The node full path.
     * The primary key for this table.
     */
    @Id
    @Column(name = "CREST_NODE_FULLPATH", unique = true, nullable = false, length = 255)
    private String nodeFullpath;
    /**
     * The schema name.
     */
    @Column(name = "CREST_SCHEMA_NAME", unique = false, nullable = false, length = 255)
    private String schemaName;
    /**
     * The node name.
     */
    @Column(name = "CREST_NODE_NAME", unique = false, nullable = false, length = 255)
    private String nodeName;
    /**
     * The node description.
     */
    @Column(name = "CREST_NODE_DESCRIPTION", unique = false, nullable = false, length = 2000)
    private String nodeDescription;
    /**
     * The tag base name.
     * This is a unique field.
     */
    @Column(name = "CREST_TAG_PATTERN", unique = true, nullable = false, length = 255)
    private String tagPattern;
    /**
     * The group role.
     */
    @Column(name = "CREST_GROUP_ROLE", unique = false, nullable = false, length = 100)
    private String groupRole;

}
