package hep.crest.server.controllers;

import hep.crest.server.converters.HashGenerator;
import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.data.pojo.Payload;
import hep.crest.server.data.pojo.PayloadData;
import hep.crest.server.data.pojo.PayloadInfoData;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.services.PayloadService;
import hep.crest.server.swagger.model.StoreDto;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;

@Component
@Slf4j
public class JsonStreamProcessor {

    /**
     * The entity manager.
     */
    private final EntityManager entityManager;

    /**
     * Thee payload service.
     */
    private PayloadService payloadService;

    @Autowired
    public JsonStreamProcessor(EntityManager entityManager, PayloadService payloadService) {
        this.entityManager = entityManager;
        this.payloadService = payloadService;
    }

    /**
     * Process a JSON stream.
     * @param jsonInputStream
     */
    public void processJsonStream(InputStream jsonInputStream, String objectType,
                                  String version, String tag) {
        ObjectMapper objectMapper = new ObjectMapper();
        JsonFactory jsonFactory = objectMapper.getFactory();
        EntityTransaction transaction = entityManager.getTransaction();

        try (JsonParser parser = jsonFactory.createParser(jsonInputStream)) {
            if (!transaction.isActive()) {
                transaction.begin();
            }

            while (parser.nextToken() != JsonToken.END_ARRAY) {
                if (parser.getCurrentToken() == JsonToken.START_OBJECT) {
                    StoreDto dto = objectMapper.readValue(parser, StoreDto.class);

                    // Here we generate objectType and version. We should probably allow for input
                    // arguments.
                    Payload entity = new Payload().objectType(objectType).hash("none").version(version);
                    entity.compressionType("none");
                    entity.size(0);
                    // Generate the hash from the payload.
                    PayloadData content = new PayloadData();
                    PayloadInfoData sinfodata = new PayloadInfoData();

                    // Initialize the iov entity from the DTO.
                    Iov iov = new Iov();
                    IovId iovId = new IovId();
                    iovId.since(dto.getSince().toBigInteger()).tagName(tag);
                    iov.id(iovId).tag(new Tag().name(tag));
                    byte[] paylodContent = dto.getData().getBytes(StandardCharsets.UTF_8);
                    log.debug("Use the data string, it represents the payload : length is {}",
                            paylodContent.length);
                    entity.size(paylodContent.length);
                    final String phash = HashGenerator.sha256Hash(paylodContent);
                    iov.payloadHash(phash);
                    entity.hash(phash);
                    InputStream is = new ByteArrayInputStream(paylodContent);
                    // Persist the entity using JPA
//                    entityManager.persist(entity);
                    payloadService.insertPayload(entity, is, sinfodata);
                }
            }

            transaction.commit();
        } catch (IOException e) {
            if (transaction.isActive()) {
                transaction.rollback();
            }
            e.printStackTrace();
        }
        catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
