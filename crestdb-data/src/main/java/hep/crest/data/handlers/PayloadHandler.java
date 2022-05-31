package hep.crest.data.handlers;

import hep.crest.data.exceptions.PayloadEncodingException;
import org.hibernate.engine.jdbc.StreamUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchAlgorithmException;

/**
 * A helper service to handle payload.
 * The handler contains static methods to use inputstreams or byte arrays.
 * @author formica
 */
public final class PayloadHandler {

    /**
     * Logger.
     */
    private static final Logger log = LoggerFactory.getLogger(PayloadHandler.class);

    /**
     * Max length for reading.
     */
    private static final Integer MAX_LENGTH = 1024;

    /**
     * Hidden ctor.
     */
    private PayloadHandler() {
    }

    /**
     * @param is the InputStream
     * @return byte[]
     */
    public static byte[] getBytesFromInputStream(InputStream is) {
        byte[] data = null;
        // Test if stream is null.
        if (is == null) {
            return data;
        }
        // Open the output buffer.
        try (ByteArrayOutputStream buffer = new ByteArrayOutputStream()) {
            int nRead;
            data = new byte[16384];
            // Loop over the stream.
            while ((nRead = is.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
                log.debug("Reading data from stream {} ", nRead);
            }
            // Flush.
            buffer.flush();
            data = buffer.toByteArray();
        }
        catch (final IOException e) {
            log.error("Exception getting bytes from stream : {}", e.getMessage());
            data = new byte[0];
        }
        return data;
    }

    /**
     * Get hash while reading the stream and saving it to a file. The internal
     * method will close the output and input stream but we also do it here just in
     * case.
     *
     * @param uploadedInputStream  the InputStream
     * @param uploadedFileLocation the String
     * @return String
     * @throws PayloadEncodingException If an Exception occurred
     */
    public static String saveToFileGetHash(InputStream uploadedInputStream,
                                           String uploadedFileLocation) throws PayloadEncodingException {
        // Generate hash.
        try (OutputStream out = new FileOutputStream(uploadedFileLocation)) {
            return HashGenerator.hashoutstream(uploadedInputStream, out);
        }
        catch (NoSuchAlgorithmException | IOException e) {
            throw new PayloadEncodingException("Cannot get hash from file " + uploadedFileLocation, e);
        }
        finally {
            // Close the stream outside the try-with-resource block.
            if (uploadedInputStream != null) {
                try {
                    uploadedInputStream.close();
                }
                catch (final IOException e) {
                    log.error("error closing input stream in saveToFileGetHash: {}",
                            e.getMessage());
                }
            }
        }
    }

    /**
     * @param uploadedInputStream the BufferedInputStream
     * @return String
     * @throws PayloadEncodingException If an Exception occurred
     */
    public static String getHashFromStream(BufferedInputStream uploadedInputStream) throws PayloadEncodingException {
        try {
            // Generate hash.
            return HashGenerator.hash(uploadedInputStream);
        }
        catch (NoSuchAlgorithmException | IOException e) {
            throw new PayloadEncodingException("Error in hashing stream : ", e);
        }
    }

    /**
     * @param uploadedInputStream  the InputStream
     * @param uploadedFileLocation the String
     */
    public static void saveStreamToFile(InputStream uploadedInputStream,
                                        String uploadedFileLocation) {
        // Save input stream to file.
        try (OutputStream out = new FileOutputStream(uploadedFileLocation)) {
            StreamUtils.copy(uploadedInputStream, out);
        }
        catch (final IOException e) {
            log.error("Exception in saveStreamToFile: {}", e.getMessage());
        }
    }

}
