/**
 *
 */
package hep.crest.server.runinfo.services;

import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.data.runinfo.pojo.RunLumiInfo;
import hep.crest.server.data.runinfo.repositories.RunLumiInfoRepository;
import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbNotFoundException;
import hep.crest.server.swagger.model.RunLumiInfoDto;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
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
@Slf4j
public class RunInfoService {

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
     * Can update the starttime or endtime fields of a RunLumi entity.
     * @param dto
     * @return RunLumiInfoDto
     */
    @Transactional
    public RunLumiInfoDto updateRunInfo(RunLumiInfoDto dto) throws AbstractCdbServiceException {
        log.debug("Update runinfo from dto {}", dto);
        final RunLumiInfo entity = mapper.map(dto, RunLumiInfo.class);
        RunLumiInfo dbentry = runinfoRepository.findById(entity.id());
        if (dbentry == null) {
            log.error("Cannot find runinfo with id {}", entity.id());
            throw new CdbNotFoundException("Cannot find runinfo with id " + entity.id());
        }
        if (!entity.starttime().equals(dbentry.starttime())) {
            dbentry.starttime(entity.starttime());
        }
        if (!entity.endtime().equals(dbentry.endtime())) {
            dbentry.endtime(entity.endtime());
        }
        log.debug("Update runinfo with id {}", entity.id());
        final RunLumiInfo saved = runinfoRepository.save(dbentry);
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
