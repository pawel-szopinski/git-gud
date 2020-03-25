package pl.pawelszopinski.config;

import org.apache.commons.lang3.StringUtils;
import pl.pawelszopinski.GitGud;
import pl.pawelszopinski.exception.ReadPropertiesException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.text.MessageFormat;
import java.util.Properties;

public class Configuration {

    private static final String CONFIG_FILE = "git-gud.properties";
    private static final String ADDRESS_KEY = "api.address";
    private static final String ACCEPT_HDR_KEY = "api.accept-header";
    private static final String SEARCH_LIMIT_KEY = "api.search-limit";
    private static final String TOKEN_KEY = "user.token";

    private static String apiAddress;
    private static String acceptHeader;
    private static int searchLimit;
    private static String userToken;

    private Configuration() {
    }

    public static String getApiAddress() {
        return apiAddress;
    }

    private static void setApiAddress(String apiAddress) {
        validatePropertyNotNullOrEmpty(apiAddress, ADDRESS_KEY);

        try {
            new URL(apiAddress);
        } catch (MalformedURLException e) {
            throw new InvalidParameterException(
                    MessageFormat.format("Malformed URL in key {0}!", ADDRESS_KEY));
        }

        Configuration.apiAddress = apiAddress;
    }

    public static String getAcceptHeader() {
        return acceptHeader;
    }

    private static void setAcceptHeader(String acceptHeader) {
        validatePropertyNotNullOrEmpty(acceptHeader, ACCEPT_HDR_KEY);

        Configuration.acceptHeader = acceptHeader;
    }

    public static int getSearchLimit() {
        return searchLimit;
    }

    private static void setSearchLimit(String searchLimit) {
        validatePropertyNotNullOrEmpty(searchLimit, SEARCH_LIMIT_KEY);

        int searchLimitInt;
        try {
            searchLimitInt = Integer.parseInt(searchLimit);
        } catch (NumberFormatException e) {
            throw new NumberFormatException(MessageFormat.format(
                    "Key {0} is not an integer.", SEARCH_LIMIT_KEY));
        }

        if (searchLimitInt < 1) {
            throw new InvalidParameterException(MessageFormat.format(
                    "Key {0} should be greater than 0.", SEARCH_LIMIT_KEY));
        }

        Configuration.searchLimit = searchLimitInt;
    }

    public static String getUserToken() {
        return userToken;
    }

    private static void setUserToken(String userToken) {
        if (StringUtils.isNotEmpty(userToken) && !userToken.matches("[0-9a-z]{40}")) {
            throw new InvalidParameterException(MessageFormat.format("Key {0} should:\n" +
                    "- consist only of lowercase alphanumeric characters\n" +
                    "- have length of exactly 40 characters", TOKEN_KEY));
        }

        Configuration.userToken = userToken;
    }

    public static void readFromFile() {
        File jarPath;
        try {
            jarPath = new File(GitGud.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            throw new ReadPropertiesException(e.getMessage());
        }

        String propertiesPath = jarPath.getParent() + "/";

        Properties props;
        try {
            InputStream input = new FileInputStream(propertiesPath + CONFIG_FILE);

            props = new PropertiesExtended();
            props.load(input);
        } catch (IOException e) {
            throw new ReadPropertiesException("Could not read application properties file!\n" +
                    "Additional info - " + e.getMessage());
        }

        try {
            assignValues(props);
        } catch (InvalidParameterException | NumberFormatException e) {
            throw new ReadPropertiesException("Error while reading application property values.\n" +
                    "Additional info - " + e.getMessage());
        }
    }

    private static void assignValues(Properties props) {
        setApiAddress(props.getProperty(ADDRESS_KEY));
        setAcceptHeader(props.getProperty(ACCEPT_HDR_KEY));
        setSearchLimit(props.getProperty(SEARCH_LIMIT_KEY));
        setUserToken(props.getProperty(TOKEN_KEY));
    }

    private static void validatePropertyNotNullOrEmpty(String value, String key) {
        if (StringUtils.isBlank(value)) {
            throw new InvalidParameterException(
                    MessageFormat.format("Missing key {0}!", key));
        }
    }
}
