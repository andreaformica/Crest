package hep.crest.server.timeseries.services;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.timeseries.model.TagInfo;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
@Slf4j
public class TagInfoSerializer {

    /**
     * Extracts the channel list from the tagInfo string.
     *
     * @param tagInfo
     * @return List of maps representing the channel list
     */
    public List<Map<String, String>> extractChannelList(String tagInfo) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            // Handle possible double-quoting
            String jsonToParse = tagInfo.trim();
            if (jsonToParse.startsWith("\"") && jsonToParse.endsWith("\"")) {
                jsonToParse = jsonToParse.substring(1, jsonToParse.length() - 1);
            }

            // Step 2: Unescape all remaining escaped quotes
            jsonToParse = StringEscapeUtils.unescapeJson(jsonToParse);

            // Parse as JsonNode first
            JsonNode node = objectMapper.readTree(jsonToParse);

            // Then convert to TagInfo
            TagInfo tagInfoObject = objectMapper.treeToValue(node, TagInfo.class);
            return tagInfoObject.getChannelList();
        }
        catch (Exception e) {
            log.error("JSON parsing failed. Input: {}", tagInfo, e);
            return Collections.emptyList();
        }
    }
}
