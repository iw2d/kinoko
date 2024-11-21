package kinoko.server.header;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Disabled
public final class HeaderValueTest {
    @Test
    public void testInHeaderValues() {
        final Map<String, Integer> headerMap = Arrays.stream(InHeader.values())
                .collect(Collectors.toMap(Enum::name, InHeader::getValue, (v1, v2) -> v1, LinkedHashMap::new));
        final Map<String, Integer> expected = Arrays.stream(InHeaderTest.values())
                .collect(Collectors.toMap(Enum::name, InHeaderTest::getValue, (v1, v2) -> v1, LinkedHashMap::new));

        for (String key : headerMap.keySet()) {
            if (!expected.containsKey(key)) {
                continue;
            }
            Assertions.assertEquals(headerMap.get(key), expected.get(key));
        }
    }

    @Test
    public void testOutHeaderValues() {
        final Map<String, Integer> headerMap = Arrays.stream(OutHeader.values())
                .collect(Collectors.toMap(Enum::name, OutHeader::getValue, (v1, v2) -> v1, LinkedHashMap::new));
        final Map<String, Integer> expected = Arrays.stream(OutHeaderTest.values())
                .collect(Collectors.toMap(Enum::name, OutHeaderTest::getValue, (v1, v2) -> v1, LinkedHashMap::new));

        for (String key : headerMap.keySet()) {
            if (!expected.containsKey(key)) {
                continue;
            }
            Assertions.assertEquals(headerMap.get(key), expected.get(key));
        }
    }
}
