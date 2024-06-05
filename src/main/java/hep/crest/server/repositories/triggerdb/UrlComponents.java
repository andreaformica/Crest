package hep.crest.server.repositories.triggerdb;

import lombok.Data;

/**
 * Class to hold the components of a trigger DB URL.
 */
@Data
public class UrlComponents {
    /**
     * The schema.
     */
    private final String schema;
    /**
     * The table.
     */
    private final String table;
    /**
     * The id.
     */
    private final Long id;

    /**
     * Get the full table name from the alias.
     *
     * @return the full table name.
     */
    public String getFullTable() throws IllegalArgumentException {
        switch (this.table) {
            case "L1PSK":
                return "l1_prescale_set";
            case "HLTMK":
                return "hlt_menu";
            case "L1MK":
                return "l1_menu";
            case "HLTPSK":
                return "hlt_prescale_set";
            case "BGK":
                return "l1_bunch_group_set";
            case "MGK":
                return "hlt_monitoring_groups";
            case "JOK":
                return "hlt_joboptions";
            default:
                throw new IllegalArgumentException("Unknown table: " + this.table);
        }
    }
}
