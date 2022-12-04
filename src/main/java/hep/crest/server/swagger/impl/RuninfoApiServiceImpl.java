package hep.crest.server.swagger.impl;

import hep.crest.server.data.runinfo.pojo.RunLumiInfo;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.runinfo.services.RunInfoService;
import hep.crest.server.swagger.api.NotFoundException;
import hep.crest.server.swagger.api.RuninfoApiService;
import hep.crest.server.swagger.model.CrestBaseResponse;
import hep.crest.server.swagger.model.GenericMap;
import hep.crest.server.swagger.model.RespPage;
import hep.crest.server.swagger.model.RunLumiInfoDto;
import hep.crest.server.swagger.model.RunLumiSetDto;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.math.BigInteger;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Rest endpoint for run information.
 *
 * @author formica
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen",
        date = "2017-11-07T14:29:18.354+01:00")
@Component
@Slf4j
public class RuninfoApiServiceImpl extends RuninfoApiService {
    /**
     * Helper.
     */
    @Autowired
    private PageRequestHelper prh;
    /**
     * Helper.
     */
    @Autowired
    EntityDtoHelper edh;

    /**
     * Service.
     */
    @Autowired
    private RunInfoService runinfoService;

    /*
     * (non-Javadoc)
     *
     * @see hep.crest.server.swagger.api.RuninfoApiService#createRunLumiInfo(hep.crest.
     * swagger.model.RunLumiSetDto, javax.ws.rs.core.SecurityContext,
     * javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response createRunInfo(RunLumiSetDto body, SecurityContext securityContext, UriInfo info)
            throws NotFoundException {
        log.info("RunInfoRestController processing request for creating a run info entry using "
                 + body);
        // Create a list of resources
        final List<RunLumiInfoDto> dtolist = body.getResources();
        final List<RunLumiInfoDto> savedlist = new ArrayList<>();
        for (final RunLumiInfoDto runInfoDto : dtolist) {
            // Create a RunInfo resource.
            final RunLumiInfoDto saved = runinfoService.insertRunInfo(runInfoDto);
            // Add to the saved list for the response.
            savedlist.add(saved);
        }
        final CrestBaseResponse respdto = new RunLumiSetDto().resources(savedlist)
                .size((long) savedlist.size()).datatype("runs");
        return Response.created(info.getRequestUri()).entity(respdto).build();

    }

    /*
     * (non-Javadoc)
     *
     * @see hep.crest.server.swagger.api.RuninfoApiService#selectRunInfo(java.lang.
     * String, java.lang.String, java.lang.String, java.lang.Integer,
     * java.lang.Integer, java.lang.String, javax.ws.rs.core.SecurityContext,
     * javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response listRunInfo(String from, String to, String format, String mode,
                                Integer page, Integer size, String sort,
                                SecurityContext securityContext, UriInfo info) throws NotFoundException {
        log.debug("Search resource list using from={}, to={}, format={}, mode={}", from, to,
                format, mode);
        // Select RunInfo in a range.
        // Create pagination request
        final PageRequest preq = prh.createPageRequest(page, size, sort);
        Page<RunLumiInfo> entitypage = null;
        if (mode.equalsIgnoreCase("runrange")) {
            // Interpret as Runs
            final BigInteger bfrom = BigInteger.valueOf(Long.valueOf(from));
            final BigInteger bto = BigInteger.valueOf(Long.valueOf(to));
            // Inclusive selection.
            entitypage = runinfoService.selectInclusiveByRun(bfrom, bto, preq);
        }
        else if (mode.equalsIgnoreCase("daterange")) {
            // Interpret as Dates
            Timestamp tsfrom = null;
            Timestamp tsto = null;
            if (format.equals("iso")) {
                log.debug("Using from and to as times in yyyymmddhhmiss");
                final DateTimeFormatter locFormatter = DateTimeFormatter
                        .ofPattern("yyyyMMddHHmmss");
                final ZonedDateTime zdtfrom = LocalDateTime.parse(from, locFormatter)
                        .atZone(ZoneId.of("Z"));
                final ZonedDateTime zdtto = LocalDateTime.parse(to, locFormatter)
                        .atZone(ZoneId.of("Z"));
                tsfrom = new Timestamp(zdtfrom.toInstant().toEpochMilli());
                tsto = new Timestamp(zdtto.toInstant().toEpochMilli());
            }
            else if (format.equals("number")) {
                tsfrom = new Timestamp(new Long(from));
                tsto = new Timestamp(new Long(to));
            }
            // Inclusive selection.
            entitypage = runinfoService.selectInclusiveByDate(new Date(tsfrom.getTime()),
                    new Date(tsto.getTime()), preq);
        }
        // Create filters
        GenericMap filters = new GenericMap();
        filters.put("from", from.toString());
        filters.put("to", to.toString());
        filters.put("mode", mode);

        // Search for global tags using where conditions.
        RespPage respPage = new RespPage().size(entitypage.getSize())
                .totalElements(entitypage.getTotalElements()).totalPages(entitypage.getTotalPages())
                .number(entitypage.getNumber());

        final List<RunLumiInfoDto> dtolist = edh.entityToDtoList(entitypage.toList(), RunLumiInfoDto.class);
        Response.Status rstatus = Response.Status.OK;
        // Prepare the Set.
        final CrestBaseResponse saveddto = buildEntityResponse(dtolist, filters);
        saveddto.page(respPage);
        // Send a response and status 200.
        return Response.status(rstatus).entity(saveddto).build();
    }

    /**
     * Factorise code to build the RunLumiSetDto.
     *
     * @param dtolist the List<RunLumiInfoDto>
     * @param filters the GenericMap
     * @return RunLumiSetDto
     */
    protected RunLumiSetDto buildEntityResponse(List<RunLumiInfoDto> dtolist, GenericMap filters) {
        final RunLumiSetDto respdto = new RunLumiSetDto();
        // Create the Set for the response.
        ((RunLumiSetDto) respdto.datatype("runs")).resources(dtolist)
                .size((long) dtolist.size());
        respdto.filter(filters);
        return respdto;
    }
}
