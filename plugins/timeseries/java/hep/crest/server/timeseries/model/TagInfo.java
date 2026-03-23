package hep.crest.server.timeseries.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

/**
 * TagInfo class.
 * This is a Java representation of the TagInfo object.
 * The object is the one used by COOL migration for ATLAS CREST usage.
 * In general tagInfo string from tagMeta is not interpreted by CREST.
 */
@Data
public class TagInfo {
    /**
     * The channel list.
     */
    @JsonProperty("channel_list")
    private List<Map<String, String>> channelList;
    /**
     * The node description.
     */
    @JsonProperty("node_description")
    private String nodeDescription;
    /**
     * The payload specifications.
     */
    @JsonProperty("payload_spec")
    private  List<Map<String, String>> payloadSpec;

}
