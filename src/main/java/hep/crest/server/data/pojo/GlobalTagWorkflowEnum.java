package hep.crest.server.data.pojo;

import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbBadRequestException;

import java.util.Objects;

/**
 * GlobalTagWorkflowEnum class.
 */
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
    public static GlobalTagWorkflowEnum fromCode(String code) throws AbstractCdbServiceException {
        for (GlobalTagWorkflowEnum type : GlobalTagWorkflowEnum.values()) {
            if (Objects.equals(type.getCode(), code)) {
                return type;
            }
        }
        throw new CdbBadRequestException("Unknown type code: " + code);
    }
}
