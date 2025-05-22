package hep.crest.server.tzero;

import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.Tag;

public interface IovUpdateCheck {

    /**
     * Method to evaluate condition based on Tag synchronization type.
     * For the moment we always accept insertions. This shall change.
     *
     * @param entity the tag
     * @param interval the iov
     * @return Boolean : True if the Iov should be accepted for insertion. False otherwise.
     */
     boolean evaluateConditions(Tag entity, Iov interval);
}
