package plugin.nats.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * Container for NATs configuration properties.
 *
 * @author formica
 *
 */
@Component
@ConfigurationProperties("nats")
@Data
public class NatsProperties {

    /**
     * The host.
     */
    private String server = "localhost";
    /**
     * The port.
     */
    private String port = "4222";
    /**
     * The NATs queue for orchestrator, to post alerts and updates on received data.
     */
    private String queue = "data.crest";
    /**
     * The cluster name for NATs streaming.
     */
    private String cluster = "test-cluster";
    /**
     * The client ID for NATs streaming.
     */
    private String clientId = "crest";
    /**
     * The user.
     */
    private String user = "none";
    /**
     * The password.
     */
    private String password = "";
    /**
     * Is NATS notification enabled.
     */
    private Boolean enabled = Boolean.TRUE;
    /**
     * The service client ID.
     */
    private String serviceClientId = "crest";

}
