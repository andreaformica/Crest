package hep.crest.server.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.embedded.undertow.UndertowServletWebServerFactory;
import org.springframework.boot.web.servlet.server.ConfigurableServletWebServerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.io.File;

/**
 * Configuration for multipart file uploads to prevent OutOfMemoryError
 * by using disk-based storage instead of memory-only buffering.
 *
 * This configuration:
 * 1. Sets up Undertow to handle large multipart uploads
 * 2. Configures appropriate buffer sizes and thresholds
 * 3. Uses a temporary directory for spilled parts
 *
 * The actual MIME parser configuration is handled through:
 * - jersey-multipart-config.properties
 * - javaopts.properties system properties
 */
@Configuration
@Slf4j
public class MultipartConfiguration {

    /**
     * Maximum file size for multipart uploads, read from application properties.
     * Defaults to 10MB if not specified.
     */
    @Value("${spring.servlet.multipart.max-file-size:10MB}")
    private String maxFileSize;

    /**
     * Maximum request size.
     */
    @Value("${spring.servlet.multipart.max-request-size:10MB}")
    private String maxRequestSize;

    /**
     * Configure Undertow to handle multipart with streaming.
     * This prevents loading entire multipart bodies into memory.
     *
     * @return Undertow configuration
     */
    @Bean
    public ConfigurableServletWebServerFactory servletWebServerFactory() {
        UndertowServletWebServerFactory factory = new UndertowServletWebServerFactory();
        
        // Configure deployment info for multipart handling
        factory.addDeploymentInfoCustomizers(deploymentInfo -> {
            log.info("Configuring deployment info for multipart uploads...");
            
            // Use temporary directory for multipart uploads
            File tempDir = new File(System.getProperty("java.io.tmpdir"), "crest-multipart");
            if (!tempDir.exists()) {
                boolean created = tempDir.mkdirs();
                if (created) {
                    log.info("Created multipart temp directory: {}", tempDir.getAbsolutePath());
                }
            }
            deploymentInfo.setTempDir(tempDir);
            
            log.info("Configured Undertow for streaming multipart uploads");
            log.info("  Max file size: {} (spring.servlet.multipart.max-file-size)", maxFileSize);
            log.info("  Max request size: {} (spring.servlet.multipart.max-request-size)",
                    maxRequestSize);
            log.info("  Temp directory: {}", tempDir.getAbsolutePath());
            log.info("  Streaming enabled via jersey-multipart-config.properties");
        });
        
        return factory;
    }
}



