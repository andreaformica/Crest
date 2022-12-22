package hep.crest.server.controllers;

public abstract class SimpleLobStreamerProvider implements LobStreamerProvider {

    /**
     * The key for accessing the Lob.
     */
    private final String key;

    /**
     * The source of the Lob.
     */
    private final String source;

    /**
     * Initialize the key.
     * @param key
     * @param source
     */
    public SimpleLobStreamerProvider(String key, String source) {
        this.source = source;
        this.key = key;
    }
}
