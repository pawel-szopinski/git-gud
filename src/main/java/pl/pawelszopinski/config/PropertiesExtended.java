package pl.pawelszopinski.config;

import java.util.Optional;
import java.util.Properties;

class PropertiesExtended extends Properties {

    @Override
    public String getProperty(String key) {
        return Optional.ofNullable(super.getProperty(key))
                .map(String::trim)
                .orElse(null);
    }
}
