/**
 *
 */
package plugin.nats.connections;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.Tag;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import plugin.nats.model.DataNotification;
import plugin.nats.model.DataNotificationContent;
import plugin.nats.model.DataProvider;
import plugin.nats.model.DataStream;

import java.io.IOException;
import java.time.Instant;
import java.time.ZoneOffset;
import java.util.HashMap;
import java.util.Map;

/**
 * A service to convert messages for NATs.
 *
 * @author formica
 */
@Service
@Slf4j
public class CrestNatsMessagesConverter {

    /**
     * Mapper.
     */
    @Autowired
    private ObjectMapper jacksonMapper;

    /**
     * The product type.
     */
    private static final String TYPE = "tag_name";
    /**
     * The product id.
     */
    private static final String TIME = "since";
    /**
     * The product id.
     */
    private static final String ID = "hash";

    /**
     * Convert a Message into a DataNotification.
     *
     * @param tagupdated the Crest tag
     * @param iovnew     the Iov
     * @param sinfo      the streamer info
     * @return DataNotification
     * @throws IOException If an Exception occurred in the conversion
     */
    public DataNotification createDataNotification(Tag tagupdated, Iov iovnew, String sinfo)
            throws IOException {
        final Instant now = Instant.now();
        // Create datanotification.
        final DataNotification datanotification = new DataNotification();
        final DataNotificationContent dn = new DataNotificationContent();
        datanotification.messageDate(now.atOffset(ZoneOffset.UTC));
        // pkt time in seconds, but with the eopch shift to bring it back to a normal date.
        Long updatetime = tagupdated.modificationTime().getTime();
        Instant pktdate = Instant.ofEpochSecond(updatetime / 1000);
        dn.date(pktdate.atOffset(ZoneOffset.UTC));
        dn.productCard(tagupdated.name());
        dn.dataStream(DataStream.CALIB);
        dn.dataProvider(DataProvider.CRESTDB);
        dn.obsid("none");
        // The burst id is not set for crest notifications
        String burstid = "none";
        // Use the minimum packet time and the obsid from the packet header.
        final Long instime = tagupdated.insertionTime().getTime();
        final String urlcond = "/iovs?tagname=" + tagupdated.name() + "&sort=id.since:DESC";

        dn.setResourceLocator(urlcond);
        // Create the map.
        final Map<String, Object> msgmap = new HashMap<>();
        msgmap.put(TYPE, tagupdated.name());
        msgmap.put(TIME, iovnew.id().since());
        msgmap.put(ID, iovnew.payloadHash());
        msgmap.put("streamer_info", sinfo);
        final String msg = jacksonMapper.writeValueAsString(msgmap);
        dn.setMessage(msg);
        datanotification.content(dn);
        return datanotification;
    }

    /**
     * Template method to read a NATs message and convert it to a SvomObject.
     *
     * @param natsmessage the String
     * @param typename    the String
     * @param ref         the TypeReference<Map<String, T>>
     * @param <T>         the template
     * @return T the Object class used.
     */
    public <T> T getSvomObjectFromMap(String natsmessage, String typename,
                                      TypeReference<Map<String, T>> ref) {
        Map<String, T> amapnot = new HashMap<>();
        try {
            amapnot = jacksonMapper.readValue(natsmessage, ref);
            return amapnot.get(typename);
        }
        catch (final RuntimeException | IOException e) {
            log.error("Error in conversion {}", e.getMessage());
        }
        return null;
    }

    /**
     * Template method to transform a SvomObject into JSON String.
     *
     * @param typename the String
     * @param clazz    the template class T
     * @param <T>      the template
     * @return String
     */
    public <T> String putSvomObjectInMap(String typename, T clazz) {
        final Map<String, T> amapnot = new HashMap<>();
        try {
            amapnot.put(typename, clazz);
            return jacksonMapper.writeValueAsString(amapnot);
        }
        catch (final RuntimeException | IOException e) {
            log.error("Error in putting svom obj in map {}", e.getMessage());
        }
        return null;
    }

    /**
     * Method to transform a Svom Message Object into JSON String.
     *
     * @param msg the template message instance
     * @param <T> the template
     * @return String
     */
    public <T> String writeSvomObjectAsString(T msg) {
        try {
            return jacksonMapper.writeValueAsString(msg);
        }
        catch (final RuntimeException | IOException e) {
            log.error("Error in putting svom obj in JSON string {}", e.getMessage());
        }
        return null;
    }

    /**
     * Template method to transform a JSON String into a SvomObject.
     *
     * @param natsmessage the String
     * @param clazz       the template class T
     * @param <T>         the template
     * @return T
     */
    @SuppressWarnings("unchecked")
    public <T> T getSvomObject(String natsmessage, T clazz) {
        try {
            clazz = (T) jacksonMapper.readValue(natsmessage, clazz.getClass());
            return clazz;
        }
        catch (final RuntimeException | IOException e) {
            log.error("Error in get object {}", e.getMessage());
        }
        return null;
    }

}
