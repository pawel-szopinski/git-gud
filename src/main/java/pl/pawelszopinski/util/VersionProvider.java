package pl.pawelszopinski.util;

import picocli.CommandLine.IVersionProvider;
import pl.pawelszopinski.exception.ReadPropertiesException;

import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import java.util.Properties;

public class VersionProvider implements IVersionProvider {

    @Override
    public String[] getVersion() {
        Properties props = new Properties();

        try (InputStream input = this.getClass().getClassLoader()
                .getResourceAsStream("application.properties")) {
            props.load(Objects.requireNonNull(input));
        } catch (IOException | NullPointerException e) {
            throw new ReadPropertiesException("Could not retrieve application version!\n" +
                    "Additional info - " + e.getMessage());
        }

        return new String[]{"Version: " + props.getProperty("app.version")};
    }
}
