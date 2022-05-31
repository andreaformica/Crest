package hep.crest.server.repositories.monitoring;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.server.swagger.model.PayloadTagInfoDto;

import java.util.List;

/**
 * @author formica
 *
 */
public interface IMonitoringRepository {

    /**
     * @param tagpattern
     *            the String
     * @return List<PayloadTagInfoDto>
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    List<PayloadTagInfoDto> selectTagInfo(String tagpattern);

}
