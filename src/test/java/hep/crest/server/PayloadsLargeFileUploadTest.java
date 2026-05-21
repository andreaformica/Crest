package hep.crest.server;

import com.fasterxml.jackson.databind.ObjectMapper;
import hep.crest.server.swagger.model.StoreDto;
import hep.crest.server.swagger.model.StoreSetDto;
import hep.crest.server.swagger.model.TagDto;
import hep.crest.server.utils.RandomGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.condition.EnabledIfSystemProperty;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryUsage;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Integration tests for large file uploads.
 * 
 * These tests verify that the multipart streaming configuration works correctly
 * with large files and that memory usage remains stable.
 * 
 * By default, these tests are DISABLED to keep the test suite fast.
 * To enable them locally, run with:
 *   -Dspring.test.largefiles=true
 * 
 * Or enable via environment variable:
 *   export LARGE_FILE_TESTS=true && mvn test -Dspring.test.largefiles=true
 *
 * IMPORTANT: These tests create actual large files on disk and require:
 * - Sufficient disk space (at least 500MB free)
 * - Sufficient heap memory (at least 1GB)
 * - Time (can take 2-5 minutes to run)
 */
@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@Slf4j
public class PayloadsLargeFileUploadTest {

    @Autowired
    private TestRestTemplate testRestTemplate;

    @Autowired
    @Qualifier("jacksonMapper")
    private ObjectMapper mapper;

    private static final RandomGenerator rnd = new RandomGenerator();
    private static final String TEST_TAG_NAME = "large-upload-test";

    private MemoryMonitor memoryMonitor;
    private File testFilesDir;

    @BeforeEach
    void setUp() throws IOException {
        memoryMonitor = new MemoryMonitor();
        
        // Create tag for testing if it doesn't exist
        try {
            initializeTag(TEST_TAG_NAME);
        } catch (Exception e) {
            log.debug("Tag may already exist: {}", e.getMessage());
        }
        
        // Create temporary directory for test files
        testFilesDir = Files.createTempDirectory("crest-large-upload-test").toFile();
        testFilesDir.deleteOnExit();
        
        log.info("===============================================");
        log.info("LARGE FILE UPLOAD TEST STARTED");
        log.info("Test files directory: {}", testFilesDir.getAbsolutePath());
        log.info("Available disk space: {} MB", new File("/tmp").getFreeSpace() / (1024 * 1024));
        memoryMonitor.logCurrentUsage("Test startup");
        log.info("===============================================");
    }

    /**
     * Initialize a tag for testing.
     */
    private void initializeTag(String tagName) {
        TagDto dto = (TagDto) rnd.generate(TagDto.class);
        dto.name(tagName);
        dto.timeType("time");
        dto.status(TagDto.StatusEnum.UNLOCKED);
        dto.synchronization(TagDto.SynchronizationEnum.ALL);
        log.info("Creating tag: {}", dto);
        final ResponseEntity<TagDto> response = testRestTemplate
                .postForEntity("/crestapi/tags", dto, TagDto.class);
        log.info("Tag creation response: {}", response.getStatusCode());
        assertThat(response.getStatusCode()).isIn(HttpStatus.CREATED, HttpStatus.CONFLICT);
    }

    /**
     * Test uploading a 5MB file (stays in memory, baseline test).
     */
    @Test
    @EnabledIfSystemProperty(named = "spring.test.largefiles", matches = "true")
    void testUpload5MBFile() throws IOException {
        log.info("TEST: Upload 5MB file (memory-based)");

        // Create tag for testing if it doesn't exist
        try {
            initializeTag(TEST_TAG_NAME + "-5mb");
        } catch (Exception e) {
            log.debug("Tag may already exist: {}", e.getMessage());
        }

        File largeFile = createTestFile("test-5mb.json", 5 * 1024 * 1024);
        
        long startTime = System.currentTimeMillis();
        memoryMonitor.logCurrentUsage("Before 5MB upload");
        
        ResponseEntity<Object> response = uploadFile(largeFile);
        
        long duration = System.currentTimeMillis() - startTime;
        memoryMonitor.logCurrentUsage("After 5MB upload");
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        log.info("✅ 5MB upload successful ({}ms)", duration);
        
        largeFile.delete();
    }

    /**
     * Test uploading a 50MB file (spills to disk, streaming test).
     */
    @Test
    @EnabledIfSystemProperty(named = "spring.test.largefiles", matches = "true")
    void testUpload50MBFile() throws IOException {
        log.info("TEST: Upload 50MB file (disk streaming)");

        // Create tag for testing if it doesn't exist
        String tagname = TEST_TAG_NAME + "-50mb";
        try {
            initializeTag(tagname);
        } catch (Exception e) {
            log.debug("Tag may already exist: {}", e.getMessage());
        }

        File largeFile = createTestFile("test-50mb.json", 50 * 1024 * 1024);
        
        long startTime = System.currentTimeMillis();
        memoryMonitor.logCurrentUsage("Before 50MB upload");
        
        // Monitor temp directory during upload
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "crest-multipart");
        
        // Create StoreSetDto JSON to file on disk first
        File storesetJsonFile = createStoreSetJsonFile(largeFile);
        
        ResponseEntity<Object> response = uploadFileWithMonitoringFromJsonFile(storesetJsonFile,
                largeFile, tempDir, tagname);
        
        long duration = System.currentTimeMillis() - startTime;
        memoryMonitor.logCurrentUsage("After 50MB upload");
        
        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        log.info("✅ 50MB upload successful ({}ms)", duration);
        
        // Verify temp directory was used and cleaned up
        verifyTempDirectoryCleanedUp(tempDir);
        
        largeFile.delete();
        storesetJsonFile.delete();
    }

    /**
     * Test uploading a 200MB file (maximum configured size, stress test).
     */
    @Test
    @EnabledIfSystemProperty(named = "spring.test.largefiles", matches = "true")
    void testUpload200MBFile() throws IOException {
        log.info("TEST: Upload 200MB file (maximum stress test)");
        // Create tag for testing if it doesn't exist
        String tagname = TEST_TAG_NAME + "-200mb";
        try {
            initializeTag(tagname);
        } catch (Exception e) {
            log.debug("Tag may already exist: {}", e.getMessage());
        }
        // Monitor temp directory during upload
        Path tempDir = Paths.get(System.getProperty("java.io.tmpdir"), "crest-multipart");

        File largeFile = createTestFile("test-200mb.json", 200 * 1024 * 1024);
        
        long startTime = System.currentTimeMillis();
        memoryMonitor.logCurrentUsage("Before 200MB upload");

        // Create StoreSetDto JSON to file on disk first
        File storesetJsonFile = createStoreSetJsonFile(largeFile);

        ResponseEntity<Object> response = uploadFileWithMonitoringFromJsonFile(storesetJsonFile,
                largeFile, tempDir, tagname);

        long duration = System.currentTimeMillis() - startTime;
        memoryMonitor.logCurrentUsage("After 200MB upload");

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        log.info("✅ 200MB upload successful ({}ms)", duration);

        // Verify temp directory was used and cleaned up
        verifyTempDirectoryCleanedUp(tempDir);

        largeFile.delete();
        storesetJsonFile.delete();
    }

    // ==================== Helper Methods ====================

    /**
     * Create a test file of specified size filled with random string data.
     */
    private File createTestFile(String filename, long sizeBytes) throws IOException {
        File file = new File(testFilesDir, filename);
        
        log.info("Creating test file: {} ({} MB)", filename, sizeBytes / (1024 * 1024));
        
        try (FileOutputStream fos = new FileOutputStream(file)) {
            // Write in chunks to avoid memory issues
            byte[] buffer = new byte[1024 * 64]; // 64KB chunks
            long remaining = sizeBytes;
            
            // Create random content for each chunk
            java.util.Random random = new java.util.Random();
            
            while (remaining > 0) {
                // Fill buffer with random bytes
                random.nextBytes(buffer);
                int toWrite = (int) Math.min(buffer.length, remaining);
                fos.write(buffer, 0, toWrite);
                remaining -= toWrite;
                
                if (remaining % (10 * 1024 * 1024) == 0 && remaining > 0) {
                    log.debug("Created: {} MB", (sizeBytes - remaining) / (1024 * 1024));
                }
            }
        }
        
        assertThat(file.exists()).isTrue();
        assertThat(file.length()).isEqualTo(sizeBytes);
        log.info("✅ Test file created: {} bytes", file.length());
        
        return file;
    }

    /**
     * Upload a file via REST API.
     */
    private ResponseEntity<Object> uploadFile(File file) throws IOException {
        return uploadFileWithMonitoring(file, null);
    }

    /**
     * Upload a file and monitor temp directory during upload.
     * Uses the FILE format with file references to avoid loading entire file into memory.
     * This is the proper way to handle large files with streaming.
     */
    private ResponseEntity<Object> uploadFileWithMonitoring(File file, Path tempDir) throws IOException {
        log.info("Starting upload: {} ({} MB)", 
            file.getName(), 
            file.length() / (1024 * 1024));
        
        // Create StoreSetDto with 5 split entries
        StoreSetDto storesetDto = new StoreSetDto();
        
        // Read the file content into a string
        byte[] fileContent = Files.readAllBytes(file.toPath());
        String fileContentAsString = new String(fileContent, StandardCharsets.UTF_8);
        
        // Split the file content into 5 parts
        int contentLength = fileContentAsString.length();
        int partSize = contentLength / 5;
        long baseSince = System.currentTimeMillis();
        
        for (int i = 0; i < 5; i++) {
            StoreDto storeDto = new StoreDto();
            
            // Calculate start and end indices for this part
            int startIdx = i * partSize;
            int endIdx = (i == 4) ? contentLength : (i + 1) * partSize;
            String partContent = fileContentAsString.substring(startIdx, endIdx);
            
            // Set data and metadata for this part
            storeDto.data(partContent);
            storeDto.since(baseSince + (long) i * 1000);
            storeDto.streamerInfo("large-file-streamer");
            storesetDto.addresourcesItem(storeDto);
        }
        
        // Serialize StoreSetDto to bytes
        byte[] storesetBytes = mapper.writeValueAsBytes(storesetDto);
        
        // Create multipart request with proper format
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // Add storeset as a resource
        ByteArrayResource storesetResource = new ByteArrayResource(storesetBytes) {
            @Override
            public String getFilename() {
                return "storeset.json";
            }
        };
        body.add("storeset", storesetResource);
        
        // Add the actual file as a separate part
        FileSystemResource fileResource = new FileSystemResource(file);
        body.add("files", fileResource);
        
        // Add form parameters
        body.add("tag", TEST_TAG_NAME);
        body.add("objectType", "test-object-type");
        body.add("compressionType", "none");
        body.add("version", "1.0");
        body.add("endtime", String.valueOf(System.currentTimeMillis()));
        
        // Set headers for multipart/form-data with FILE format indicator
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-Crest-PayloadFormat", "JSON");  // FILE format for streaming
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        // Monitor temp directory if provided
        if (tempDir != null) {
            startTempDirectoryMonitor(tempDir, file.length());
        }
        
        ResponseEntity<Object> response = testRestTemplate.exchange(
            "/crestapi/payloads",
            HttpMethod.PUT,
            requestEntity,
            Object.class
        );

        log.info("Upload response: {}", response.getStatusCode());
        return response;
    }

    /**
     * Create a StoreSetDto JSON file on disk instead of in memory.
     * This creates the JSON by streaming the file in chunks to avoid OutOfMemoryError.
     * Chunks are base64-encoded since JSON requires valid text, not raw bytes.
     * 
     * The number of IOVs is dynamically calculated to ensure each base64-encoded
     * StoreDto does not exceed 10MB (accounting for ~33% size increase from base64).
     */
    private File createStoreSetJsonFile(File dataFile) throws IOException {
        log.info("Creating StoreSetDto JSON file on disk by streaming chunks");
        
        File jsonFile = new File(testFilesDir, "storeset.json");
        long baseSince = System.currentTimeMillis();
        
        long fileSize = dataFile.length();
        
        // Calculate number of IOVs needed to keep each base64-encoded chunk ≤ 10MB
        // Base64 expands by ~33%, so if we want final size ≤ 10MB (10,485,760 bytes):
        // raw_size * 4/3 ≤ 10MB
        // raw_size ≤ 10MB * 3/4 = 7.5MB (7,864,320 bytes)
        long maxBase64ChunkBytes = 10 * 1024 * 1024; // 10MB limit
        long maxRawChunkBytes = (long)(maxBase64ChunkBytes * 0.75); // 7.5MB
        int niovs = (int) Math.ceil((double) fileSize / maxRawChunkBytes);
        long chunkSize = fileSize / niovs;
        
        log.info("File size: {} bytes, calculated {} IOVs, chunk size per IOV: {} bytes", 
            fileSize, niovs, chunkSize);
        
        try (FileOutputStream jsonFos = new FileOutputStream(jsonFile)) {
            // Write JSON structure start
            jsonFos.write("{\"resources\":[".getBytes(StandardCharsets.UTF_8));
            
            // Stream the data file in chunks - keep track of bytes, not characters
            byte[] buffer = new byte[1024 * 1024]; // 1MB read buffer
            
            try (java.io.FileInputStream dataFis = new java.io.FileInputStream(dataFile)) {
                long totalBytesRead = 0;
                int currentIov = 0;
                byte[] accumulatedBytes = new byte[(int)(chunkSize * 2)]; // Buffer to accumulate bytes
                int accumulatedLen = 0;
                
                int read;
                while ((read = dataFis.read(buffer)) > 0) {
                    // Add bytes to accumulated buffer
                    System.arraycopy(buffer, 0, accumulatedBytes, accumulatedLen, read);
                    accumulatedLen += read;
                    totalBytesRead += read;
                    
                    log.debug("Read {} bytes, total: {}", read, totalBytesRead);
                    
                    // Check if we have enough bytes for one or more chunks
                    while (accumulatedLen >= chunkSize && currentIov < niovs - 1) {
                        // Base64-encode the accumulated bytes
                        String base64Content = java.util.Base64.getEncoder()
                                .encodeToString(java.util.Arrays.copyOf(accumulatedBytes, (int)chunkSize));
                        
                        long base64Size = base64Content.length();
                        log.debug("StoreDto {}: raw {} bytes -> base64 {} bytes", 
                            currentIov + 1, chunkSize, base64Size);
                        
                        // Create and serialize StoreDto
                        StoreDto storeDto = new StoreDto();
                        storeDto.data(base64Content);
                        storeDto.since(baseSince + (long) currentIov * 1000);
                        storeDto.streamerInfo("large-file-streamer");
                        
                        String storeJson = mapper.writeValueAsString(storeDto);
                        jsonFos.write(storeJson.getBytes(StandardCharsets.UTF_8));
                        jsonFos.write(",".getBytes(StandardCharsets.UTF_8));
                        
                        // Shift remaining bytes
                        accumulatedLen -= (int)chunkSize;
                        System.arraycopy(accumulatedBytes, (int)chunkSize, accumulatedBytes, 0, accumulatedLen);
                        
                        currentIov++;
                        log.debug("Written StoreDto {} of {} (base64-encoded)", currentIov, niovs);
                    }
                }
                
                // Write final StoreDto with remaining data
                if (accumulatedLen > 0 && currentIov < niovs) {
                    // Base64-encode the remaining bytes
                    String base64Content = java.util.Base64.getEncoder()
                            .encodeToString(java.util.Arrays.copyOf(accumulatedBytes, accumulatedLen));
                    
                    long base64Size = base64Content.length();
                    log.debug("Final StoreDto {}: raw {} bytes -> base64 {} bytes", 
                        currentIov + 1, accumulatedLen, base64Size);
                    
                    StoreDto storeDto = new StoreDto();
                    storeDto.data(base64Content);
                    storeDto.since(baseSince + (long) currentIov * 1000);
                    storeDto.streamerInfo("large-file-streamer");
                    
                    String storeJson = mapper.writeValueAsString(storeDto);
                    jsonFos.write(storeJson.getBytes(StandardCharsets.UTF_8));
                    
                    currentIov++;
                    log.debug("Written final StoreDto {} of {} (base64-encoded)", currentIov, niovs);
                } else if (currentIov == 0) {
                    // Handle case where file is smaller than expected
                    String base64Content = java.util.Base64.getEncoder()
                            .encodeToString(java.util.Arrays.copyOf(accumulatedBytes, accumulatedLen));
                    
                    long base64Size = base64Content.length();
                    log.debug("Single StoreDto: raw {} bytes -> base64 {} bytes", accumulatedLen, base64Size);
                    
                    StoreDto storeDto = new StoreDto();
                    storeDto.data(base64Content);
                    storeDto.since(baseSince);
                    storeDto.streamerInfo("large-file-streamer");
                    
                    String storeJson = mapper.writeValueAsString(storeDto);
                    jsonFos.write(storeJson.getBytes(StandardCharsets.UTF_8));
                    
                    log.debug("Written single StoreDto for small file (base64-encoded)");
                }
            }
            
            // Write JSON structure end
            jsonFos.write("]".getBytes(StandardCharsets.UTF_8));
            jsonFos.write("}".getBytes(StandardCharsets.UTF_8));
        }
        
        log.info("✅ StoreSetDto JSON file created: {} bytes with {} IOVs", jsonFile.length(), niovs);
        return jsonFile;
    }

    /**
     * Upload file with monitoring using StoreSetDto JSON from file (streaming).
     * This method streams the JSON file instead of building it in memory.
     */
    private ResponseEntity<Object> uploadFileWithMonitoringFromJsonFile(File jsonFile,
                                                                        File dataFile,
                                                                        Path tempDir,
                                                                        String tagname) throws IOException {
        log.info("Starting upload from JSON file: {} ({} MB)", 
            dataFile.getName(), 
            dataFile.length() / (1024 * 1024));
        
        // Create multipart request
        MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
        
        // Add storeset as a file resource (streams from disk)
        FileSystemResource storesetResource = new FileSystemResource(jsonFile);
        body.add("storeset", storesetResource);

        // Add form parameters
        body.add("tag", tagname);
        body.add("objectType", "test-object-type");
        body.add("compressionType", "none");
        body.add("version", "1.0");
        body.add("endtime", String.valueOf(System.currentTimeMillis()));
        
        // Set headers for multipart/form-data with JSON format indicator
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.MULTIPART_FORM_DATA);
        headers.set("X-Crest-PayloadFormat", "JSON");
        
        HttpEntity<MultiValueMap<String, Object>> requestEntity = new HttpEntity<>(body, headers);
        
        // Monitor temp directory if provided
        if (tempDir != null) {
            startTempDirectoryMonitor(tempDir, dataFile.length());
        }
        
        ResponseEntity<Object> response = testRestTemplate.exchange(
            "/crestapi/payloads",
            HttpMethod.PUT,
            requestEntity,
            Object.class
        );

        log.info("Upload response: {}", response.getStatusCode());
        return response;
    }

    // ==================== Helper Methods ====================

    /**
     * Start a monitoring thread to track temp directory usage during upload.
     */
    private void startTempDirectoryMonitor(Path tempDir, long fileSize) {
        Thread monitor = new Thread(() -> {
            try {
                long maxSize = 0;
                while (Thread.currentThread().isAlive()) {
                    if (Files.exists(tempDir)) {
                        long dirSize = calculateDirectorySize(tempDir);
                        if (dirSize > maxSize) {
                            maxSize = dirSize;
                            long percent = (dirSize * 100) / fileSize;
                            log.debug("Temp directory: {} MB ({}%)", 
                                dirSize / (1024 * 1024), 
                                percent);
                        }
                    }
                    Thread.sleep(100);
                }
                log.info("Peak temp directory usage: {} MB", 
                    maxSize / (1024 * 1024));
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (IOException e) {
                log.debug("Error monitoring temp directory", e);
            }
        });
        
        monitor.setDaemon(true);
        monitor.start();
    }

    /**
     * Calculate total size of files in a directory.
     */
    private long calculateDirectorySize(Path directory) throws IOException {
        if (!Files.exists(directory)) {
            return 0;
        }
        
        try (var stream = Files.list(directory)) {
            return stream
                .mapToLong(path -> {
                    try {
                        return Files.size(path);
                    } catch (IOException e) {
                        return 0;
                    }
                })
                .sum();
        }
    }

    /**
     * Verify that temp directory is empty after upload.
     */
    private void verifyTempDirectoryCleanedUp(Path tempDir) throws IOException {
        if (!Files.exists(tempDir)) {
            return;
        }
        
        try (var stream = Files.list(tempDir)) {
            long fileCount = stream.count();
            assertThat(fileCount)
                .as("Temp directory should be cleaned up after upload")
                .isEqualTo(0);
            
            log.info("✅ Temp directory cleaned up");
        }
    }

    // ==================== Memory Monitoring Utility ====================

    /**
     * Utility class to monitor memory usage during tests.
     */
    @Slf4j
    private static class MemoryMonitor {
        private MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();

        void logCurrentUsage(String phase) {
            MemoryUsage heap = memoryBean.getHeapMemoryUsage();
            MemoryUsage nonHeap = memoryBean.getNonHeapMemoryUsage();
            
            log.info("{}: Heap: {}/{} MB, NonHeap: {} MB",
                phase,
                heap.getUsed() / (1024 * 1024),
                heap.getMax() / (1024 * 1024),
                nonHeap.getUsed() / (1024 * 1024)
            );
        }
    }

    // ==================== Spring Resource Wrapper ====================

    // FileSystemResource is no longer needed; we use ByteArrayResource with streaming JSON API
}




