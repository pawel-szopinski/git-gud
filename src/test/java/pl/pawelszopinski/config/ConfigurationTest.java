package pl.pawelszopinski.config;

import org.junit.jupiter.api.Test;
import pl.pawelszopinski.exception.ReadPropertiesException;

import java.net.URL;
import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;

public class ConfigurationTest {

    @Test
    void testMissingFileThrowsException() {
        //when
        Exception exception = assertThrows(
                ReadPropertiesException.class,
                () -> Configuration.readFromFile("X"));

        //then
        assertTrue(exception.getMessage().endsWith("(No such file or directory)"));
    }

    @Test
    void testLoadConfigurationSuccess() {
        //given
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("git-gud-withtoken.properties");
        String propsPath = Objects.requireNonNull(url).getPath();

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
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("bad-properties/git-gud-missing-api-address.properties");
        String propsPath = Objects.requireNonNull(url).getPath();

        //then
        Exception exception = assertThrows(ReadPropertiesException.class,
                () -> Configuration.readFromFile(propsPath));
        assertTrue(exception.getMessage().contains("Missing key"));
    }

    @Test
    void testMalformedApiAddressThrowsException() {
        //given
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("bad-properties/git-gud-malformed-api-address.properties");
        String propsPath = Objects.requireNonNull(url).getPath();

        //then
        Exception exception = assertThrows(ReadPropertiesException.class,
                () -> Configuration.readFromFile(propsPath));
        assertTrue(exception.getMessage().contains("Malformed URL in key"));
    }

    @Test
    void testSearchLimitParseErrorThrowsException() {
        //given
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("bad-properties/git-gud-search-limit-not-int.properties");
        String propsPath = Objects.requireNonNull(url).getPath();

        //then
        Exception exception = assertThrows(ReadPropertiesException.class,
                () -> Configuration.readFromFile(propsPath));
        assertTrue(exception.getMessage().contains("is not an integer"));
    }

    @Test
    void testSearchLimitLessThanOneThrowsException() {
        //given
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("bad-properties/git-gud-search-limit-low.properties");
        String propsPath = Objects.requireNonNull(url).getPath();

        //then
        Exception exception = assertThrows(ReadPropertiesException.class,
                () -> Configuration.readFromFile(propsPath));
        assertTrue(exception.getMessage().contains("should be greater than 0"));
    }

    @Test
    void testInvalidTokenThrowsException() {
        //given
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("bad-properties/git-gud-invalid-token.properties");
        String propsPath = Objects.requireNonNull(url).getPath();

        //then
        Exception exception = assertThrows(ReadPropertiesException.class,
                () -> Configuration.readFromFile(propsPath));
        assertTrue(exception.getMessage().contains("have length of exactly 40 characters"));
    }
}
