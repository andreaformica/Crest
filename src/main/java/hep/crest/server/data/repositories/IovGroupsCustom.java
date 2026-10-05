/**
 *
 */
package hep.crest.server.data.repositories;

import hep.crest.server.data.pojo.Tag;

import java.math.BigInteger;
import java.util.Date;
import java.util.List;

/**
 * Interface for groups requests.
 *
 * @author aformic
 */
public interface IovGroupsCustom {

    /**
     * @param tag   the Tag
     * @param snap      the Date
     * @param groupsize the Long
     * @return List<BigInteger>
     */
    List<BigInteger> selectSnapshotGroups(Tag tag, Date snap, Long groupsize);

    /**
     * @param tagname the String
     * @return Long
     */
    Long getSize(String tagname);

    /**
     * @param tagname the String
     * @param snap    the Date
     * @return Long
     */
    Long getSizeBySnapshot(String tagname, Date snap);

}
