package plugin.nats.connections;

import io.nats.client.Connection;
import io.nats.client.Nats;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;

/**
 * A factory for NATs connections.
 *
 * @author formica
 */
@Slf4j
public class NatsConnectionFactory {

    /**
     * The NATs streaming cluster identifier.
     */
    private String cluster = "test-cluster";
    /**
     * The client ID in NATs streaming.
     */
    private String clientId = "spring-crest";
    /**
     * The default NATs URL.
     */
    private String natsUrl = "nats://localhost:4222";
    /**
     * The vhf queue.
     */
    private String queue = "test";
    /**
     * The decoded queue. Not used.
     */
    private String vhfDecodedQueue = "test";

    /**
     * NATs connection.
     */
    private Connection conn = null;

    /**
     * Ctor with arguments.
     *
     * @param cluster the cluster name.
     * @param clientId the client ID.
     * @param natsUrl the nats server.
     * @param queue the queue.
     */
    public NatsConnectionFactory(String cluster, String clientId, String natsUrl, String queue) {
        log.info("Initialize factory with properties: {} {} {} {}", cluster, clientId, natsUrl, queue);
        this.cluster = cluster;
        this.clientId = clientId;
        this.natsUrl = natsUrl;
        this.queue = queue;
    }

    /**
     * Initialize the connection factory.
     *
     * @return
     */
    public void initNatsConnectionFactory() {
        log.info(
                "Start NATs connection factory bean...create NATs connection and streaming factory");
        conn = initNatsConnection();
        if (conn == null) {
            log.error("Cannot start the factory, underlying NATs connection is null...");
            return;
        }
    }

    /**
     * Initialize NATs connection.
     *
     * @return Connection
     */
    private Connection initNatsConnection() {
        final io.nats.client.Options options = new io.nats.client.Options.Builder().server(natsUrl)
                .maxReconnects(10).build();
        try {
            log.info("Start NATS connection using url: {}", natsUrl);
            return Nats.connect(options);
        }
        catch (final IOException e) {
            log.error("Failed connecting to NATs: IOException => {}", e);
        }
        catch (final InterruptedException e) {
            log.error("Failed connecting to NATs: connection creation, interrupt detected => {}",
                    e.getMessage());
            Thread.currentThread().interrupt();
        }
        return null;
    }

    /**
     * Test if connection is active.
     *
     * @return boolean
     */
    public boolean testConnection() {
        // This returns immediately. The result of the publish can be handled in the ack
        // handler.
        log.info("Test NATS JETstreaming connection...");
        boolean connok = false;
        final Connection natsconn = conn;
        if (natsconn != null) {
            final String urlconn = natsconn.getConnectedUrl();
            if (urlconn != null) {
                log.debug("Underlying NATS connection is active....");
                connok = true;
            }
        }
        log.warn("NATS streaming connection is {}", connok ? "ACTIVE" : "NOT ACTIVE");
        return connok;
    }

    /**
     * Get NATs connection.
     *
     * @return Connection
     */
    public Connection getNatsConnection() {
        if (this.conn == null) {
            this.conn = initNatsConnection();
        }
        return this.conn;
    }

    /**
     * Close JetStreamingConnection.
     *
     * @return
     */
    public void close() {
        try {
            log.info("Closing NATS JETstreaming connection : {}", conn);
            conn.close();
        }
        catch (final InterruptedException e) {
            Thread.currentThread().interrupt();
            log.error("Error closing NATs connection: interrupt detected => {}", e);
        }
    }

    /**
     * Returns a string representation of the object. In general, the
     * {@code toString} method returns a string that
     * "textually represents" this object. The result should
     * be a concise but informative representation that is easy for a
     * person to read.
     * It is recommended that all subclasses override this method.
     * <p>
     * The {@code toString} method for class {@code Object}
     * returns a string consisting of the name of the class of which the
     * object is an instance, the at-sign character `{@code @}', and
     * the unsigned hexadecimal representation of the hash code of the
     * object. In other words, this method returns a string equal to the
     * value of:
     * <blockquote>
     * <pre>
     * getClass().getName() + '@' + Integer.toHexString(hashCode())
     * </pre></blockquote>
     *
     * @return a string representation of the object.
     */
    @Override
    public String toString() {
        String sf = "connectionFactory: "
                    + this.cluster + ", "
                    + this.clientId + ", "
                    + this.natsUrl + ", "
                    + this.queue;
        return sf;
    }
}
