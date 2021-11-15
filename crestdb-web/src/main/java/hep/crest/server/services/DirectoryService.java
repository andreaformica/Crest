/**
 *
 */
package hep.crest.server.services;

import hep.crest.data.config.CrestProperties;
import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.Tag;
import hep.crest.data.repositories.IIovCrud;
import hep.crest.data.repositories.ITagCrud;
import hep.crest.data.repositories.ITagMetaCrud;
import hep.crest.data.repositories.PayloadDirectoryImplementation;
import hep.crest.data.utils.DirectoryUtilities;
import hep.crest.server.controllers.EntityDtoHelper;
import hep.crest.swagger.model.IovDto;
import hep.crest.swagger.model.PayloadDto;
import hep.crest.swagger.model.TagMetaDto;
import ma.glasnost.orika.MapperFacade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.AsyncResult;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

/**
 * An implementation for filesystem based storage.
 *
 * @author formica
 *
 */
@Service
public class DirectoryService {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(DirectoryService.class);

    /**
     * Repository.
     */
    @Autowired
    @Qualifier("fstagrepository")
    private ITagCrud fstagrepository;
    /**
     * Repository.
     */
    @Autowired
    @Qualifier("fsmetarepository")
    private ITagMetaCrud fsmetarepository;
    /**
     * Repository.
     */
    @Autowired
    @Qualifier("fsiovrepository")
    private IIovCrud fsiovrepository;
    /**
     * Repository.
     */
    @Autowired
    @Qualifier("fspayloadrepository")
    private PayloadDirectoryImplementation fspayloadrepository;

    /**
     * Helper.
     */
    @Autowired
    private EntityDtoHelper edh;
    /**
     * Service.
     */
    @Autowired
    private IovService iovservice;
    /**
     * Service.
     */
    @Autowired
    private TagService tagservice;
    /**
     * Service.
     */
    @Autowired
    private PayloadService pyldservice;
    /**
     * Service.
     */
    @Autowired
    private TagMetaService tagmetaservice;

    /**
     * Properties.
     */
    @Autowired
    private CrestProperties cprops;

    /**
     * Mapper.
     */
    @Autowired
    @Qualifier("mapper")
    private MapperFacade mapper;

    /**
     * @param tagname
     *            the String
     * @param path
     * @return Tag
     */
    public Tag getTag(String tagname, String path) {
        DirectoryUtilities du = null;
        if (!"none".equals(path)) {
            du = new DirectoryUtilities(path);
        }
        else {
            du = new DirectoryUtilities();
        }
        fstagrepository.setDirtools(du);
        return fstagrepository.findOne(tagname);
    }

    /**
     * @param entity
     *            the Tag
     * @param path
     * @return Tag or null.
     */
    public Tag insertTag(Tag entity, String path) {
        DirectoryUtilities du = null;
        if (!"none".equals(path)) {
            du = new DirectoryUtilities(path);
        }
        else {
            du = new DirectoryUtilities();
        }
        fstagrepository.setDirtools(du);
        return fstagrepository.save(entity);
    }

    /**
     * @param tagname
     *            the String
     * @return List<IovDto>
     */
    public List<IovDto> listIovs(String tagname) {
        try {
            List<Iov> iovlist = fsiovrepository.findByIdTagName(tagname);
            return edh.entityToDtoList(iovlist, IovDto.class);
        }
        catch (final AbstractCdbServiceException e) {
            log.error("Cannot find iov list for tag {}: {}", tagname, e.getMessage());
        }
        return new ArrayList<>();
    }

    /**
     * @param hash
     *            the String
     * @return PayloadDto
     */
    public PayloadDto getPayload(String hash) {
        return fspayloadrepository.find(hash);
    }

    /**
     * @param tagname
     *            the String
     * @param snapshot
     *            the Date
     * @param path
     *            the String
     * @return Future<String>
     */
    @Async
    public Future<String> dumpTag(String tagname, Date snapshot, String path) {
        final String threadname = Thread.currentThread().getName();
        log.debug("Running task in asynchronous mode for name {}", threadname);
        final String outdir = cprops.getDumpdir() + File.separator + path;
        log.debug("Output directory is {}", outdir);

        final DirectoryUtilities du = new DirectoryUtilities(outdir);
        try {
            fstagrepository.setDirtools(du);
            fsmetarepository.setDirtools(du);
            fsiovrepository.setDirtools(du);
            fspayloadrepository.setDirtools(du);

            final Tag seltag = tagservice.findOne(tagname);
            final Iterable<Iov> iovlist = iovservice.selectSnapshotByTag(tagname, snapshot);
            fstagrepository.save(seltag);
            final TagMetaDto mdto = tagmetaservice.findMeta(tagname);
            fsmetarepository.save(mdto);
            fsiovrepository.saveAll(tagname, iovlist);
            int counter = 0;
            for (final Iov iov : iovlist) {
                final PayloadDto pyld = pyldservice.getPayload(iov.payloadHash());
                fspayloadrepository.save(pyld);
                counter++;
            }
            final String tarpath = cprops.getWebstaticdir() + File.separator + path;
            final String outtar = du.createTarFile(outdir, tarpath);
            log.debug("Created output tar file {}", outtar);
            return new AsyncResult<>(
                    "Dump a list of " + counter + " iovs into file system...");
        }
        catch (final AbstractCdbServiceException e) {
            log.error("Server exception, cannot dump tag {} in path {} : {}", tagname, path, e.getMessage());
        }
        return null;
    }
}
