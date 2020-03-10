package pl.pawelszopinski.config;

import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.Properties;

class PropertiesExtended extends Properties {

    @Override
    public String getProperty(String key) {
        String property = super.getProperty(key);

        if (property != null) property = property.trim();

        if (property == null && key.startsWith("required.")) {
            throw new InvalidParameterException(
                    MessageFormat.format("Missing value for key {0}!", key));
        }

        return property;
    }
}
