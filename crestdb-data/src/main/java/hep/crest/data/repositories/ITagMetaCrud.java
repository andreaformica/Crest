package hep.crest.data.repositories;

import hep.crest.data.utils.DirectoryUtilities;

public interface ITagMetaCrud extends TagMetaDataBaseCustom {

    /**
     * Set directory utilities.
     *
     * @param du
     */
    void setDirtools(DirectoryUtilities du);
}
