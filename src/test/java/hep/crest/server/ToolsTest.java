package hep.crest.server;

import hep.crest.server.serializers.ArgTimeUnit;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("default")
@Slf4j
public class ToolsTest {

    @Test
    public void testArgTime() {
        assertThat(ArgTimeUnit.valueOf("NUMBER")).isEqualTo(ArgTimeUnit.NUMBER);
        assertThat(ArgTimeUnit.valueOf("MS")).isEqualTo(ArgTimeUnit.MS);
        assertThat(ArgTimeUnit.valueOf("COOL")).isEqualTo(ArgTimeUnit.COOL);
        assertThat(ArgTimeUnit.valueOf("CUSTOM")).isEqualTo(ArgTimeUnit.CUSTOM);
        assertThat(ArgTimeUnit.valueOf("ISO")).isEqualTo(ArgTimeUnit.ISO);
    }

}
