package hep.crest.server.swagger.api.impl;

import com.querydsl.core.types.dsl.BooleanExpression;
import hep.crest.data.repositories.querydsl.IFilteringCriteria;
import hep.crest.data.security.pojo.CrestFolders;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.server.controllers.PageRequestHelper;
import hep.crest.server.services.FolderService;
import hep.crest.server.swagger.api.FoldersApiService;
import hep.crest.server.swagger.api.NotFoundException;
import hep.crest.swagger.model.CrestBaseResponse;
import hep.crest.swagger.model.FolderDto;
import hep.crest.swagger.model.FolderSetDto;
import hep.crest.swagger.model.GenericMap;
import hep.crest.swagger.model.RespPage;
import lombok.extern.slf4j.Slf4j;
import ma.glasnost.orika.MapperFacade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Component;

import javax.ws.rs.core.Response;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.UriInfo;
import java.util.List;

/**
 * Rest endpoint for folder administration.
 * The folders do not exist in CMS environment.
 * They can be used in ATLAS as a way to map old COOL nodes and for authorization purposes.
 * An important element for this authorization is the base tag name pattern, which impose
 * a string for all tag names of a given system.
 *
 * @author formica
 */
@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaJerseyServerCodegen",
        date = "2018-05-10T14:57:11.305+02:00")
@Component
@Slf4j
public class FoldersApiServiceImpl extends FoldersApiService {
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
     * Mapper.
     */
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;

    /**
     * Filtering.
     */
    @Autowired
    @Qualifier("folderFiltering")
    private IFilteringCriteria filtering;

    /**
     * Service.
     */
    @Autowired
    private FolderService folderService;

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.server.swagger.api.FoldersApiService#createFolder(hep.crest.swagger
     * .model.FolderDto, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response createFolder(FolderDto body, SecurityContext securityContext, UriInfo info)
            throws NotFoundException {
        log.info("FolderRestController processing request for creating a folder");
        // Insert the new folder.
        CrestFolders entity = mapper.map(body, CrestFolders.class);
        final CrestFolders saved = folderService.insertFolder(entity);
        FolderDto dto = mapper.map(saved, FolderDto.class);
        return Response.created(info.getRequestUri()).entity(dto).build();
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * hep.crest.server.swagger.api.FoldersApiService#listFolders(java.lang.String,
     * java.lang.String, javax.ws.rs.core.SecurityContext, javax.ws.rs.core.UriInfo)
     */
    @Override
    public Response listFolders(String by, String sort, SecurityContext securityContext,
                                UriInfo info) throws NotFoundException {
        log.debug("Search resource list using by={}, sort={}", by, sort);
        // Create filters
        GenericMap filters = prh.getFilters(prh.createMatcherCriteria(by));
        // Create a default page requests with 10000 size for retrieval.
        // This method does not allow to set pagination.
        final PageRequest preq = prh.createPageRequest(0, 10000, sort);
        BooleanExpression wherepred = null;
        if (!"none".equals(by)) {
            // Create search conditions for where statement in SQL
            wherepred = prh.buildWhere(filtering, by);
        }
        // Search for global tags using where conditions.
        Page<CrestFolders> entitypage = folderService.findAllFolders(wherepred, preq);
        RespPage respPage = new RespPage().size(entitypage.getSize())
                .totalElements(entitypage.getTotalElements()).totalPages(entitypage.getTotalPages())
                .number(entitypage.getNumber());
        // Now pass back the dto list.
        final List<FolderDto> dtolist = edh.entityToDtoList(entitypage.toList(), FolderDto.class);
        Response.Status rstatus = Response.Status.OK;
        // Create the response object using also the page.
        final CrestBaseResponse setdto = new FolderSetDto().resources(dtolist)
                .page(respPage)
                .size((long) dtolist.size()).datatype("folders");
        if (filters != null) {
            setdto.filter(filters);
        }
        return Response.status(rstatus).entity(setdto).build();
    }
}
