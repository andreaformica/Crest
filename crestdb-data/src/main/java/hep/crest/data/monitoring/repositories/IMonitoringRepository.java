package hep.crest.data.monitoring.repositories;

import java.util.List;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.swagger.model.PayloadTagInfoDto;

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
