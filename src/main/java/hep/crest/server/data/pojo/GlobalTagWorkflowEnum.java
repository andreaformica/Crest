package hep.crest.server.data.pojo;

import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbBadRequestException;
import lombok.Getter;

import java.util.Objects;

/**
 * GlobalTagWorkflowEnum class.
 */
@Getter
public enum GlobalTagWorkflowEnum {

    /**
     * The bulk workflow.
     */
    BULK("BULK"),
    /**
     * The express stream workflow.
     */
    ES("ES"),
    /**
     * The hlt workflow.
     */
    HLT("HLT"),
    /**
     * The mc workflow.
     */
    MC("MC"),
    /**
     * The test workflow.
     */
    TEST("TEST");

    /**
     * The code.
     * -- GETTER --
     *
     * @return the code

     */
    private final String code;

    /**
     * Default Ctor.
     *
     * @param code the String
     */
    GlobalTagWorkflowEnum(String code) {
        this.code = code;
    }

    /**
     * Create a type from a code.
     * @param code the code.
     * @return the type.
     * @throws AbstractCdbServiceException if the code is unknown.
     */
    public static GlobalTagWorkflowEnum fromCode(String code) throws AbstractCdbServiceException {
        if (code == null) {
            throw new CdbBadRequestException("Unknown type code: null");
        }
        try {
            return valueOf(code);
        } catch (IllegalArgumentException e) {
            throw new CdbBadRequestException("Unknown type code: " + code);
        }
    }
}
