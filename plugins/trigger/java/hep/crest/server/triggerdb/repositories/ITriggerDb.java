package hep.crest.server.triggerdb.repositories;

import java.io.InputStream;

public interface ITriggerDb {

    /**
     * Get trigger DB data.
     * @param components
     * @return InputStream
     */
    InputStream getTriggerDBData(UrlComponents components);

    /**
     * Parse the URL.
     * @param url
     * @return UrlComponents
     */
    UrlComponents parseUrl(String url);
}
