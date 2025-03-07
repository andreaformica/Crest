package hep.crest.server.config;

import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;

/**
 * AspectJ configuration.
 * @version %I%, %G%
 * @author formica
 *
 */
@Configuration
@ComponentScan("hep.crest.server")
@EnableAspectJAutoProxy
public class AspectJConfig {

}
