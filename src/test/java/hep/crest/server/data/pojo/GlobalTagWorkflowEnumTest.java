package hep.crest.server.data.pojo;

import hep.crest.server.exceptions.CdbBadRequestException;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * Test class for GlobalTagWorkflowEnum.
 * Tests all enum values, getCode() method, and fromCode() conversion with valid/invalid inputs.
 */
class GlobalTagWorkflowEnumTest {

    // ========== Enum Values Tests ==========

    @Test
    void testEnumValues_shouldHaveFiveValues() {
        // when
        GlobalTagWorkflowEnum[] values = GlobalTagWorkflowEnum.values();

        // then
        assertThat(values).hasSize(5);
    }

    @Test
    void testEnumValue_BULK() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.BULK;

        // then
        assertThat(workflow).isNotNull();
        assertThat(workflow.getCode()).isEqualTo("BULK");
    }

    @Test
    void testEnumValue_ES() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.ES;

        // then
        assertThat(workflow).isNotNull();
        assertThat(workflow.getCode()).isEqualTo("ES");
    }

    @Test
    void testEnumValue_HLT() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.HLT;

        // then
        assertThat(workflow).isNotNull();
        assertThat(workflow.getCode()).isEqualTo("HLT");
    }

    @Test
    void testEnumValue_MC() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.MC;

        // then
        assertThat(workflow).isNotNull();
        assertThat(workflow.getCode()).isEqualTo("MC");
    }

    @Test
    void testEnumValue_TEST() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.TEST;

        // then
        assertThat(workflow).isNotNull();
        assertThat(workflow.getCode()).isEqualTo("TEST");
    }

    @Test
    void testEnumValueOf_BULK() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.valueOf("BULK");

        // then
        assertThat(workflow).isEqualTo(GlobalTagWorkflowEnum.BULK);
    }

    @Test
    void testEnumValueOf_ES() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.valueOf("ES");

        // then
        assertThat(workflow).isEqualTo(GlobalTagWorkflowEnum.ES);
    }

    // ========== fromCode() Tests - Valid Codes ==========

    @Test
    void testFromCode_withBULK_shouldReturnBULK() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.fromCode("BULK");

        // then
        assertThat(workflow).isEqualTo(GlobalTagWorkflowEnum.BULK);
    }

    @Test
    void testFromCode_withES_shouldReturnES() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.fromCode("ES");

        // then
        assertThat(workflow).isEqualTo(GlobalTagWorkflowEnum.ES);
    }

    @Test
    void testFromCode_withHLT_shouldReturnHLT() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.fromCode("HLT");

        // then
        assertThat(workflow).isEqualTo(GlobalTagWorkflowEnum.HLT);
    }

    @Test
    void testFromCode_withMC_shouldReturnMC() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.fromCode("MC");

        // then
        assertThat(workflow).isEqualTo(GlobalTagWorkflowEnum.MC);
    }

    @Test
    void testFromCode_withTEST_shouldReturnTEST() {
        // when
        GlobalTagWorkflowEnum workflow = GlobalTagWorkflowEnum.fromCode("TEST");

        // then
        assertThat(workflow).isEqualTo(GlobalTagWorkflowEnum.TEST);
    }

    // ========== fromCode() Tests - Invalid Codes ==========

    @Test
    void testFromCode_withInvalidCode_shouldThrowException() {
        // when & then
        assertThatThrownBy(() -> GlobalTagWorkflowEnum.fromCode("INVALID"))
                .isInstanceOf(CdbBadRequestException.class)
                .hasMessageContaining("Unknown type code: INVALID");
    }

    @Test
    void testFromCode_withNull_shouldThrowException() {
        // when & then
        assertThatThrownBy(() -> GlobalTagWorkflowEnum.fromCode(null))
                .isInstanceOf(CdbBadRequestException.class)
                .hasMessageContaining("Unknown type code: null");
    }

    @Test
    void testFromCode_withEmptyString_shouldThrowException() {
        // when & then
        assertThatThrownBy(() -> GlobalTagWorkflowEnum.fromCode(""))
                .isInstanceOf(CdbBadRequestException.class)
                .hasMessageContaining("Unknown type code: ");
    }

    @Test
    void testFromCode_withLowerCase_shouldThrowException() {
        // given - fromCode is case-sensitive
        // when & then
        assertThatThrownBy(() -> GlobalTagWorkflowEnum.fromCode("bulk"))
                .isInstanceOf(CdbBadRequestException.class)
                .hasMessageContaining("Unknown type code: bulk");
    }

    @Test
    void testFromCode_withMixedCase_shouldThrowException() {
        // when & then
        assertThatThrownBy(() -> GlobalTagWorkflowEnum.fromCode("Bulk"))
                .isInstanceOf(CdbBadRequestException.class)
                .hasMessageContaining("Unknown type code: Bulk");
    }

    @Test
    void testFromCode_withWhitespace_shouldThrowException() {
        // when & then
        assertThatThrownBy(() -> GlobalTagWorkflowEnum.fromCode(" BULK "))
                .isInstanceOf(CdbBadRequestException.class)
                .hasMessageContaining("Unknown type code:  BULK ");
    }

    @Test
    void testFromCode_withNumericCode_shouldThrowException() {
        // when & then
        assertThatThrownBy(() -> GlobalTagWorkflowEnum.fromCode("123"))
                .isInstanceOf(CdbBadRequestException.class)
                .hasMessageContaining("Unknown type code: 123");
    }

    // ========== Round-trip Tests ==========

    @Test
    void testRoundTrip_allEnumValues() {
        // given - all enum values
        for (GlobalTagWorkflowEnum workflow : GlobalTagWorkflowEnum.values()) {
            // when - convert to code and back
            String code = workflow.getCode();
            GlobalTagWorkflowEnum converted = GlobalTagWorkflowEnum.fromCode(code);

            // then - should get the same enum value
            assertThat(converted).isEqualTo(workflow);
            assertThat(converted.getCode()).isEqualTo(code);
        }
    }

    // ========== Enum Behavior Tests ==========

    @Test
    void testEnumEquality() {
        // given
        GlobalTagWorkflowEnum workflow1 = GlobalTagWorkflowEnum.BULK;
        GlobalTagWorkflowEnum workflow2 = GlobalTagWorkflowEnum.BULK;
        GlobalTagWorkflowEnum workflow3 = GlobalTagWorkflowEnum.ES;

        // then
        assertThat(workflow1).isEqualTo(workflow2);
        assertThat(workflow1).isNotEqualTo(workflow3);
        assertThat(workflow1 == workflow2).isTrue(); // enum singleton
    }

    @Test
    void testEnumToString() {
        // when
        String bulkString = GlobalTagWorkflowEnum.BULK.toString();
        String esString = GlobalTagWorkflowEnum.ES.toString();

        // then - toString returns the enum constant name
        assertThat(bulkString).isEqualTo("BULK");
        assertThat(esString).isEqualTo("ES");
    }

    @Test
    void testEnumName() {
        // when
        String bulkName = GlobalTagWorkflowEnum.BULK.name();
        String hltName = GlobalTagWorkflowEnum.HLT.name();

        // then
        assertThat(bulkName).isEqualTo("BULK");
        assertThat(hltName).isEqualTo("HLT");
    }

    @Test
    void testEnumOrdinal() {
        // then - verify ordinals are assigned in declaration order
        assertThat(GlobalTagWorkflowEnum.BULK.ordinal()).isEqualTo(0);
        assertThat(GlobalTagWorkflowEnum.ES.ordinal()).isEqualTo(1);
        assertThat(GlobalTagWorkflowEnum.HLT.ordinal()).isEqualTo(2);
        assertThat(GlobalTagWorkflowEnum.MC.ordinal()).isEqualTo(3);
        assertThat(GlobalTagWorkflowEnum.TEST.ordinal()).isEqualTo(4);
    }

    @Test
    void testEnumComparison() {
        // when
        int comparison1 = GlobalTagWorkflowEnum.BULK.compareTo(GlobalTagWorkflowEnum.ES);
        int comparison2 = GlobalTagWorkflowEnum.TEST.compareTo(GlobalTagWorkflowEnum.BULK);
        int comparison3 = GlobalTagWorkflowEnum.MC.compareTo(GlobalTagWorkflowEnum.MC);

        // then
        assertThat(comparison1).isNegative(); // BULK < ES
        assertThat(comparison2).isPositive(); // TEST > BULK
        assertThat(comparison3).isZero(); // MC == MC
    }

    // ========== Code Consistency Tests ==========

    @Test
    void testAllCodesAreUpperCase() {
        // verify all codes are uppercase
        for (GlobalTagWorkflowEnum workflow : GlobalTagWorkflowEnum.values()) {
            String code = workflow.getCode();
            assertThat(code).isEqualTo(code.toUpperCase());
        }
    }

    @Test
    void testAllCodesAreNotEmpty() {
        // verify no empty codes
        for (GlobalTagWorkflowEnum workflow : GlobalTagWorkflowEnum.values()) {
            assertThat(workflow.getCode()).isNotEmpty();
        }
    }

    @Test
    void testAllCodesAreUnique() {
        // verify all codes are unique
        GlobalTagWorkflowEnum[] values = GlobalTagWorkflowEnum.values();
        for (int i = 0; i < values.length; i++) {
            for (int j = i + 1; j < values.length; j++) {
                assertThat(values[i].getCode()).isNotEqualTo(values[j].getCode());
            }
        }
    }
}

