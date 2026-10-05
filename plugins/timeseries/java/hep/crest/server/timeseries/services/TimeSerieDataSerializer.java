package hep.crest.server.timeseries.services;

import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.timeseries.model.TimeSerieData;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class TimeSerieDataSerializer {

    /**
     * Extracts the timeserie data from the JSON in the payload.
     *
     * @param data
     * @return TimeSerieData object
     */
    public TimeSerieData extractTimeSerieData(String data) {
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            return objectMapper.readValue(data, TimeSerieData.class);
        }
        catch (Exception e) {
            // Handle exception (e.g., log error)
            log.error("Failed to parse data: {}", data, e);
            return null; // Return an empty list or handle as needed
        }
    }
}
