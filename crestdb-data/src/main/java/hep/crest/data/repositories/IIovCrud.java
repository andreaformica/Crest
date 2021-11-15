package hep.crest.data.repositories;

import hep.crest.data.exceptions.AbstractCdbServiceException;
import hep.crest.data.pojo.Iov;
import hep.crest.data.pojo.IovId;
import hep.crest.data.utils.DirectoryUtilities;

public interface IIovCrud extends IIovQuery {

    /**
     * Remove an iov using the Id.
     *
     * @param id
     */
    void deleteById(IovId id) throws AbstractCdbServiceException;

    /**
     * Remove an iov using the entity.
     *
     * @param entity
     */
    void delete(Iov entity) throws AbstractCdbServiceException;

    /**
     * Save an iov.
     *
     * @param entity the Iov to save.
     * @return Iov.
     */
    Iov save(Iov entity) throws AbstractCdbServiceException;

    /**
     * Save an iov.
     *
     * @param tag
     * @param entitylist the Iov list to save.
     * @return int
     */
    int saveAll(String tag, Iterable<Iov> entitylist) throws AbstractCdbServiceException;

    /**
     * Set directory utilities.
     *
     * @param du
     */
    void setDirtools(DirectoryUtilities du);
}
