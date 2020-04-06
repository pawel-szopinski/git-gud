package pl.pawelszopinski.config;

import org.junit.jupiter.api.Test;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class PropertiesExtendedTest {

    @Test
    void testNonNullPropertyTrimmedCorrectly() {
        //given
        Properties props = new PropertiesExtended();
        props.put("key", " test ");

        //when
        String value = props.getProperty("key");

        //then
        assertEquals("test", value);
    }

    @Test
    void testNonExistentPropertyReturnNull() {
        //when
        String value = new PropertiesExtended().getProperty("non-existent-key");

        //then
        assertNull(value);
    }
}