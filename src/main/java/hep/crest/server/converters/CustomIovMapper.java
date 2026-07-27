package hep.crest.server.converters;

import hep.crest.server.data.pojo.Iov;
import hep.crest.server.data.pojo.IovId;
import hep.crest.server.data.pojo.Tag;
import hep.crest.server.data.repositories.TagRepository;
import hep.crest.server.swagger.model.IovDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigInteger;
import java.util.Date;
import java.util.Optional;

/**
 * Custom mapper for Iov with repository-based logic.
 */
@Component
public class CustomIovMapper {

    /**
     * Tag repository.
     */
    private final TagRepository tagRepository;

    /**
     * Constructor with TagRepository injection.
     *
     * @param tagRepository the TagRepository
     */
    @Autowired
    public CustomIovMapper(TagRepository tagRepository) {
        this.tagRepository = tagRepository;
    }

    /**
     * Creates a fully populated Iov entity from an IovDto, including Tag lookup and IovId
     * construction.
     *
     * @param dto the IovDto
     * @return the constructed Iov entity, or null if Tag not found
     */
    public Iov createIovFromDto(IovDto dto) {
        if (dto == null || dto.getTagName() == null) {
            return null;
        }
        Optional<Tag> tagOpt = tagRepository.findByName(dto.getTagName());
        if (tagOpt.isEmpty()) {
            return null;
        }
        Tag tag = tagOpt.get();
        BigInteger since = null;
        if (dto.getSince() != null) {
            Long sinceObj = dto.getSince();
            since = BigInteger.valueOf(sinceObj);
        }
        Date insertionTime = null;
        if (dto.getInsertionTime() != null) {
            insertionTime = Date.from(dto.getInsertionTime().toInstant());
        }
        IovId iovId = new IovId()
                .setTagId(tag.getTagId())
                .setSince(since)
                .setInsertionTime(insertionTime);
        return new Iov()
                .setId(iovId)
                .setTag(tag)
                .setPayloadHash(dto.getPayloadHash());
    }
}
