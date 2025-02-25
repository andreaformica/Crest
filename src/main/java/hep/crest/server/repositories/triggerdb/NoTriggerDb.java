package hep.crest.server.repositories.triggerdb;

import java.io.InputStream;

/**
 * No trigger db.
 * Default implementation, which should send an error when used.
 */
public class NoTriggerDb implements ITriggerDb {

    @Override
    public InputStream getTriggerDBData(UrlComponents components) {
        throw new UnsupportedOperationException("No trigger DB is configured.");
    }

    @Override
    public UrlComponents parseUrl(String url) {
        throw new UnsupportedOperationException("No trigger DB is configured.");
    }
}
