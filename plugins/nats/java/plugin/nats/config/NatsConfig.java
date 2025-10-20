package plugin.nats.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import plugin.nats.connections.NatsConnectionFactory;
import plugin.nats.connections.NatsController;

@Configuration
@ComponentScan("plugin.nats")
@EnableAspectJAutoProxy
@Slf4j
public class NatsConfig {

    /**
     * Create the NATs factory bean.
     *
     * @param natsprops the NatsProperties
     * @return NatsConnectionFactory
     */
    @Bean
    public NatsConnectionFactory natsConnectionFactory(@Autowired NatsProperties natsprops) {
        // Check password
        if (natsprops.getPassword().contains("\n")) {
            log.warn("Remove <newline> from password");
            natsprops.setPassword(natsprops.getPassword().replace("\n", ""));
        }
        // Build NATs URL.
        // Take properties from the configuration files.
        String natsurl = "nats://" + natsprops.getUser() + ":" + natsprops.getPassword() + "@"
                         + natsprops.getServer() + ":" + natsprops.getPort();
        log.info("Creating NatsConnectionFactory Bean using properties {}", natsprops);
        // Build the factory.
        final NatsConnectionFactory natsFactory = new NatsConnectionFactory(natsprops.getCluster(),
                natsprops.getClientId(),
                natsurl,
                natsprops.getQueue());
        natsFactory.initNatsConnectionFactory();
        return natsFactory;
    }

    /**
     * Create controller.
     *
     * @param natsConnectionFactory
     * @return NatsController
     */
    @Bean
    public NatsController natsController(@Autowired NatsConnectionFactory natsConnectionFactory) {
        log.info("Creating NatsController using connection factory {}", natsConnectionFactory);
        return new NatsController(natsConnectionFactory);
    }

}
