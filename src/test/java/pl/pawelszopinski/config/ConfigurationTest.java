package pl.pawelszopinski.config;

import org.junit.jupiter.api.Test;
import pl.pawelszopinski.Utils;
import pl.pawelszopinski.exception.ReadPropertiesException;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationTest {

    @Test
    void testMissingFileThrowsException() {
        //when
        Exception exception = assertThrows(
                ReadPropertiesException.class,
                () -> Configuration.readFromFile("X"));

        //then
        assertTrue(exception.getMessage().startsWith("Could not read application properties file"));
    }

    @Test
    void testLoadConfigurationSuccess() {
        //given
        String propsPath = Utils.getFullResourcePath("git-gud-withtoken.properties");

        //when
        Configuration.readFromFile(propsPath);

        //then
        assertEquals("http://localhost:1080", Configuration.getApiAddress());
        assertEquals("application/vnd.github.v3+json", Configuration.getAcceptHeader());
        assertEquals(1000, Configuration.getSearchLimit());
        assertEquals("xxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxxx", Configuration.getUserToken());
    }

    @Test
    void testMissingKeyThrowsException() {
        //given
        String propsPath = Utils.getFullResourcePath("bad-properties/git-gud-missing-api-address.properties");

        //then
        Exception exception = assertThrows(ReadPropertiesException.class,
                () -> Configuration.readFromFile(propsPath));
        assertTrue(exception.getMessage().contains("Missing key"));
    }

    @Test
    void testMalformedApiAddressThrowsException() {
        //given
        String propsPath = Utils.getFullResourcePath("bad-properties/git-gud-malformed-api-address.properties");

        //then
        Exception exception = assertThrows(ReadPropertiesException.class,
                () -> Configuration.readFromFile(propsPath));
        assertTrue(exception.getMessage().contains("Malformed URL in key"));
    }

    @Test
    void testSearchLimitParseErrorThrowsException() {
        //given
        String propsPath = Utils.getFullResourcePath("bad-properties/git-gud-search-limit-not-int.properties");

        //then
        Exception exception = assertThrows(ReadPropertiesException.class,
                () -> Configuration.readFromFile(propsPath));
        assertTrue(exception.getMessage().contains("is not an integer"));
    }

    @Test
    void testSearchLimitLessThanOneThrowsException() {
        //given
        String propsPath = Utils.getFullResourcePath("bad-properties/git-gud-search-limit-low.properties");

        //then
        Exception exception = assertThrows(ReadPropertiesException.class,
                () -> Configuration.readFromFile(propsPath));
        assertTrue(exception.getMessage().contains("should be greater than 0"));
    }

    @Test
    void testInvalidTokenThrowsException() {
        //given
        String propsPath = Utils.getFullResourcePath("bad-properties/git-gud-invalid-token.properties");

        //then
        Exception exception = assertThrows(ReadPropertiesException.class,
                () -> Configuration.readFromFile(propsPath));
        assertTrue(exception.getMessage().contains("have length of exactly 40 characters"));
    }
}
