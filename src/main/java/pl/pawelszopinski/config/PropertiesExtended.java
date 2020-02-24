package pl.pawelszopinski.config;

import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.Properties;

class PropertiesExtended extends Properties {

    @Override
    public String getProperty(String key) throws InvalidParameterException {
        String property = super.getProperty(key);

        if (property == null && key.startsWith("api.")) {
            throw new InvalidParameterException(
                    MessageFormat.format("Missing value for key {0}!", key));
        }

        return property;
    }
}
