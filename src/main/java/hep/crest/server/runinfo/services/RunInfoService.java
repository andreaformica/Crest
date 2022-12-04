/**
 *
 */
package hep.crest.server.runinfo.services;

import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.data.runinfo.pojo.RunLumiInfo;
import hep.crest.server.data.runinfo.repositories.RunLumiInfoRepository;
import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.swagger.model.RunLumiInfoDto;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.math.BigInteger;
import java.util.Date;

/**
 * @author formica
 *
 */
@Service
public class RunInfoService {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(RunInfoService.class);

    /**
     * Repository.
     */
    @Autowired
    private RunLumiInfoRepository runinfoRepository;

    /**
     * Mapper.
     */
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;
    /**
     * Helper.
     */
    @Autowired
    private PageRequestHelper prh;

    /**
     * @param dto
     *            the RunInfoDto
     * @return RunLumiInfoDto
     * @throws AbstractCdbServiceException
     *             If an Exception occurred
     */
    @Transactional
    public RunLumiInfoDto insertRunInfo(RunLumiInfoDto dto) throws AbstractCdbServiceException {
        log.debug("Create runinfo from dto {}", dto);
        final RunLumiInfo entity = mapper.map(dto, RunLumiInfo.class);
        final RunLumiInfo saved = runinfoRepository.save(entity);
        log.debug("Saved entity: {}", saved);
        return mapper.map(saved, RunLumiInfoDto.class);
    }

    /**
     * @param from
     *            the BigInteger.
     * @param to
     *            the BigInteger.
     * @param preq the PageRequest
     * @throws AbstractCdbServiceException
     *             If an Exception occurred.
     * @return List<RunLumiInfo>
     */
    public Page<RunLumiInfo> selectInclusiveByRun(BigInteger from, BigInteger to, Pageable preq) {
        Page<RunLumiInfo> entitylist = null;
        if (preq == null) {
            String sort = "runNumber:ASC";
            preq = prh.createPageRequest(0, 1000, sort);
        }
        entitylist = runinfoRepository.findByRunNumberInclusive(from, to, preq);
        log.trace("Retrieved list of runs {}", entitylist.getNumberOfElements());
        return entitylist;
    }

    /**
     * @param from
     *            the Date.
     * @param to
     *            the Date.
     * @param preq the PageRequest
     * @throws AbstractCdbServiceException
     *             If an Exception occurred.
     * @return Page<RunLumiInfo>
     */
    public Page<RunLumiInfo> selectInclusiveByDate(Date from, Date to, Pageable preq) {
        Page<RunLumiInfo> entitylist = null;
        if (preq == null) {
            String sort = "runNumber:ASC";
            preq = prh.createPageRequest(0, 1000, sort);
        }
        entitylist = runinfoRepository.findByDateInclusive(BigInteger.valueOf(from.getTime()),
                BigInteger.valueOf(to.getTime()), preq);
        log.trace("Retrieved list of runs {}", entitylist.getNumberOfElements());
        return entitylist;
    }
}
