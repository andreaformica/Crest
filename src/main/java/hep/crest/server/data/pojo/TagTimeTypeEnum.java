package hep.crest.server.data.pojo;

import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbBadRequestException;

import java.util.Objects;

/**
 * GlobalTagWorkflowEnum class.
 */
public enum TagTimeTypeEnum {

    /**
     * The bulk workflow.
     */
    TIME("time"),
    /**
     * The express stream workflow.
     */
    RUNLUMI("run-lumi"),
    /**
     * The hlt workflow.
     */
    RUN("run");

    /**
     * The code.
     */
    private final String code;

    /**
     * Default Ctor.
     *
     * @param code the String
     */
    TagTimeTypeEnum(String code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public String getCode() {
        return code;
    }

    /**
     * Create a type from a code.
     * @param code the code.
     * @return the type.
     * @throws AbstractCdbServiceException if the code is unknown.
     */
    public static TagTimeTypeEnum fromCode(String code) throws AbstractCdbServiceException {
        for (TagTimeTypeEnum type : TagTimeTypeEnum.values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        throw new CdbBadRequestException("Unknown type code: " + code);
    }
}
