package hep.crest.server.controllers;

import lombok.extern.slf4j.Slf4j;

/**
 * Utility class to monitor memory usage in a Java application.
 */
@Slf4j
public class MemoryMonitor {
    /**
     * The start time of the application.
     */
    private final long startTime = System.currentTimeMillis();
    /**
     * The runtime instance to get memory usage.
     */
    private final Runtime runtime = Runtime.getRuntime();
    /**
     * The peak memory usage.
     */
    private long peakMemory = 0;

    /**
     * Memory threshold for processing.
     */
    private static final long MEMORY_THRESHOLD_MB = 512; // Adjust based on your system

    /**
     * Logs the current memory usage and peak memory usage at a specific phase.
     *
     * @param phase The phase of the application (e.g., "Initialization", "Processing").
     */
    void logCurrentUsage(String phase) {
        long used = runtime.totalMemory() - runtime.freeMemory();
        peakMemory = Math.max(peakMemory, used);

        log.info("{} - Memory: {} MB (Peak: {} MB) | Time: {} ms",
                phase,
                bytesToMB(used),
                bytesToMB(peakMemory),
                System.currentTimeMillis() - startTime);
    }

    /**
     * Logs the final memory usage and peak memory usage when the application is shutting down.
     */
    void logFinalUsage() {
        long used = runtime.totalMemory() - runtime.freeMemory();
        log.info("Final - Memory: {} MB (Peak: {} MB) | Total Time: {} ms",
                bytesToMB(used),
                bytesToMB(peakMemory),
                System.currentTimeMillis() - startTime);
    }

    /**
     * Checks the memory usage and logs a warning if it exceeds a certain threshold.
     */
    void checkMemoryUsage() {
        long used = runtime.totalMemory() - runtime.freeMemory();
        if (bytesToMB(used) > 512) { // Example threshold
            log.warn("Memory usage exceeded threshold: {} MB", bytesToMB(used));
        }
    }

    /**
     * Converts bytes to megabytes.
     *
     * @param bytes The number of bytes.
     * @return The equivalent number of megabytes.
     */
    private long bytesToMB(long bytes) {
        return bytes / (1024 * 1024);
    }
}
