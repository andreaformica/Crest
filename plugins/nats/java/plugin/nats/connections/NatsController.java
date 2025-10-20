/**
 *
 */
package plugin.nats.connections;

import io.nats.client.Connection;
import io.nats.client.Message;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import plugin.nats.config.NatsProperties;

import javax.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/**
 * A Controller for NATs that can be used to post messages.
 *
 * @author formica
 *
 */
@Slf4j
public class NatsController {

    /**
     * Factory.
     */
    private NatsConnectionFactory natsConnectionFactory;

    /**
     * Nats properties.
     */
    @Autowired
    private NatsProperties natsprops;

    /**
     * FAILED.
     */
    private static final String FAILED = "failed";
    /**
     * OK.
     */
    private static final String OK = "ok";

    /**
     * MAX_RETRIES.
     */
    private static final int MAX_RETRIES = 5;

    /**
     * Ctor.
     * @param natsConnectionFactory
     */
    public NatsController(@Autowired NatsConnectionFactory natsConnectionFactory) {
        log.info("Start NATS controller using connection factory instance: {}", natsConnectionFactory);
        this.natsConnectionFactory = natsConnectionFactory;
    }
    /**
     * Initialize. Not used.
     *
     * @return
     */
    @PostConstruct
    public void init() {
        // Do not use listener for the moment...this is needed if you decode via NATS
        // Send message that service has started...
        log.info("Init NatsController bean");
    }

    /**
     * Validate the connection.
     *
     * @return boolean
     */
    private boolean validateConnection() {
        boolean status = false;
        int counter = 0;
        while (!status && counter < MAX_RETRIES) {
            status = natsConnectionFactory.testConnection();
            if (!status) {
                log.warn("NATs connection problems....retrying...: {}", counter);
            }
            counter++;
        }
        return status;
    }

    /**
     * Publish in async mode to a given queue.
     *
     * @param queue
     *            the String
     * @param msg
     *            the String
     * @return String
     */
    public String publishToQueue(String queue, String msg) {
        if (!natsprops.getEnabled()) {
            return OK;
        }
        boolean status = validateConnection();
        int retries = 3;
        // Try to reinitialize the factory and the connection.
        while (retries > 0) {
            if (!status) {
                log.error("Cannot validate NATs streaming, reinitialize the factory");
                // Re-initialize the factory
                natsConnectionFactory.initNatsConnectionFactory();
                status = validateConnection();
                if (status) {
                    retries = 0;
                }
            }
            retries--;
        }
        // If status is still false, then there is a problem with NATS connection.
        if (!status) {
            return FAILED;
        }
        // Get the queue from the factory if it is null in arguments.
        if (queue == null) {
            queue = natsprops.getQueue();
        }
        // Get the streaming connection.
        final Connection nats = natsConnectionFactory.getNatsConnection();
        if (nats != null) {
            Future<Message> incoming = nats.request(queue, msg.getBytes(StandardCharsets.UTF_8));
            try {
                Message resp = incoming.get(3, TimeUnit.SECONDS);
                String response = new String(resp.getData(), StandardCharsets.UTF_8);
                log.info("Response from NATS is {}", response);
            }
            catch (ExecutionException | TimeoutException e) {
                log.error("NATS Exception: {}", e);
            }
            catch (final InterruptedException e) {
                log.error("Failed posting to NATs: interrupt detected => {}",
                        e.getMessage());
                Thread.currentThread().interrupt();
            }
            log.debug("NATS message published in {}", queue);
            return OK;
        }
        return FAILED;
    }

    /**
     * Gets the service client id.
     *
     * @return the service client id
     */
    public String getServiceClientId() {
        return natsprops.getServiceClientId();
    }
}
