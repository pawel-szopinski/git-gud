package pl.pawelszopinski.config;

import java.util.Properties;

class PropertiesExtended extends Properties {

    @Override
    public String getProperty(String key) {
        String property = super.getProperty(key);

        if (property != null) property = property.trim();

        return property;
    }
}
