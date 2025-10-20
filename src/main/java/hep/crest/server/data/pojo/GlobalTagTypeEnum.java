package hep.crest.server.data.pojo;

import hep.crest.server.exceptions.AbstractCdbServiceException;
import hep.crest.server.exceptions.CdbBadRequestException;

/**
 * The enum for the global tag type.
 */
public enum GlobalTagTypeEnum {
    /**
     * Locked.
     */
    LOCKED('L'),
    /**
     * TEST.
     */
    TEST('T'),
    /**
     * NONE.
     */
    NONE('N');

    /**
     * The type.
     */
    private final char code;

    /**
     * Default Ctor.
     *
     * @param code the char
     */
    GlobalTagTypeEnum(char code) {
        this.code = code;
    }

    /**
     * @return the code
     */
    public char getCode() {
        return code;
    }

    /**
     * Create a type from a code.
     * @param code the code.
     * @return the type.
     * @throws AbstractCdbServiceException if the code is unknown.
     */
    public static GlobalTagTypeEnum fromCode(char code) throws AbstractCdbServiceException {
        for (GlobalTagTypeEnum type : GlobalTagTypeEnum.values()) {
            if (type.getCode() == code) {
                return type;
            }
        }
        throw new CdbBadRequestException("Unknown type code: " + code);
    }
}
