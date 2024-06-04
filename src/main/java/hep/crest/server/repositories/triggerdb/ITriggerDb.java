package hep.crest.server.repositories.triggerdb;

import java.io.InputStream;

public interface ITriggerDb {

    /**
     * Get L1 prescale set.
     * @param id
     * @return InputStream
     */
    InputStream getL1PrescaleSet(Long id);

    UrlComponents parseUrl(String url);
}
