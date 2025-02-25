package hep.crest.server.services;

import java.util.Set;
import java.util.stream.Stream;

/**
 * Interface for a payload buffer.
 */
public interface IPayloadBuffer {

    /**
     * Add a hash to the buffer with optional tag_name metadata.
     *
     * @param hash    Unique hash to store.
     * @param tagName Tag name associated with the hash.
     */
    void addToBuffer(String hash, String tagName);

    /**
     * Retrieve a set of ALL HASHES for a given tag_name from the buffer.
     *
     * @param tagName Tag name to look up.
     * @return Set. A set of hashes associated with the given tag name.
     */
    Set<String> getHashesByTagName(String tagName);

    /**
     * Remove a hash from the buffer by its tag name.
     *
     * @param hash    The hash to remove.
     * @param tagName The tag name to which the hash belongs.
     */
    void removeFromBuffer(String hash, String tagName);

    /**
     * Retrieve a set of ALL HASHES for a given tag_name from the buffer.
     * @param tagName Tag name to look up.
     * @return Stream. A stream of hashes associated with the given tag name.
     */
    Stream<String> streamHashesByTagName(String tagName);
}
