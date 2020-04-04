package pl.pawelszopinski;

import java.io.File;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public class Utils {

    public static String getFullResourcePath(String file) {
        URL resource = Thread.currentThread().getContextClassLoader()
                .getResource(file);

        String systemIndependentPath = new File(Objects.requireNonNull(resource).getPath()).toString();

        return URLDecoder.decode(systemIndependentPath, StandardCharsets.UTF_8);
    }
}
