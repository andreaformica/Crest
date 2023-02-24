package plugin.nats;

import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.Payload;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.services.PayloadService;
import hep.crest.server.services.TagService;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import plugin.nats.connections.CrestNatsMessagesConverter;
import plugin.nats.connections.NatsController;
import plugin.nats.model.DataNotification;

import java.nio.charset.StandardCharsets;

/**
 * Aspect to be used for NATS messaging triggered on new insertions.
 *
 * @author formica
 * @version %I%, %G%
 */
@Aspect
@Component
@Slf4j
public class NatsAspect {
    /**
     * Controller.
     */
    @Autowired
    private NatsController natsController;

    /**
     * Service.
     */
    @Autowired
    private CrestNatsMessagesConverter crestNatsMessagesConverter;

    /**
     * Service.
     */
    @Autowired
    private TagService tagService;

    /**
     * Service.
     */
    @Autowired
    private PayloadService payloadService;

    /**
     * Check synchronization.
     *
     * @param retVal the Iov
     * @return Object
     */
    @AfterReturning(pointcut = "execution(* hep.crest.server.services.PayloadService.storeIov(*))",
            returning = "retVal")
    public Object natsAlert(Object retVal) throws Throwable {
        Iov entity = (Iov) retVal;
        log.info("Iov insertion for tag {} will trigger NATS notification",
                entity.id().tagName());
        Tag tag = tagService.findOne(entity.id().tagName());
        try {
            Payload meta = payloadService.getPayload(entity.payloadHash());
            String sinfo = "none";
            if (meta != null) {
                byte[] sinfoByarr = payloadService.getPayloadStreamerInfo(entity.payloadHash());
                if (sinfoByarr != null) {
                    sinfo = new String(sinfoByarr, StandardCharsets.UTF_8);
                }
            }
            DataNotification dn = crestNatsMessagesConverter.createDataNotification(tag, entity, sinfo);
            final String msg = crestNatsMessagesConverter.writeSvomObjectAsString(dn);
            log.debug("Created datanotification from iov {}: {}", entity, msg);
            natsController.publishToQueue(null, msg);
        }
        catch (Exception e) {
            log.error("Cannot publish to NATS queue: {}", e.getMessage());
        }
        return retVal;
    }

}
