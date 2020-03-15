package pl.pawelszopinski.config;

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
    private static final String ACCEPT_HDR_START_VAL = "application/vnd.github.";
    private static final String USER_KEY = "user.name";
    private static final String TOKEN_KEY = "user.token";

    private static String apiAddress;
    private static String acceptHeader;
    private static String userName;
    private static String userToken;

    private Configuration() {
    }

    public static String getApiAddress() {
        return apiAddress;
    }

    private static void setApiAddress(String apiAddress) {
        validatePropertyMissing(apiAddress, ADDRESS_KEY);

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
        validatePropertyMissing(acceptHeader, ACCEPT_HDR_KEY);

        if (!acceptHeader.startsWith(ACCEPT_HDR_START_VAL)) {
            throw new InvalidParameterException(
                    MessageFormat.format("Key {0} should start with ''{1}''!",
                            ACCEPT_HDR_KEY, ACCEPT_HDR_START_VAL));
        }

        Configuration.acceptHeader = acceptHeader;
    }

    public static String getUserName() {
        return userName;
    }

    private static void setUserName(String userName) {
        if (userName != null && !userName.matches("^(?!-)(?!.*-$)(?!.*?--)[a-z0-9-]{1,39}$")) {
            throw new InvalidParameterException(MessageFormat.format("Key {0} should:\n" +
                    "- consist only of alphanumeric characters or ''-'' " +
                    "(cannot be first, last or two/more consecutive)\n" +
                    "- be between 1 and 39 characters", USER_KEY));
        }

        Configuration.userName = userName;
    }

    public static String getUserToken() {
        return userToken;
    }

    private static void setUserToken(String userToken) {
        if (userToken != null && !userToken.matches("[0-9a-z]{40}")) {
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
        } catch (InvalidParameterException e) {
            throw new ReadPropertiesException("Error while reading application property values.\n" +
                    "Additional info - " + e.getMessage());
        }
    }

    private static void assignValues(Properties props) {
        setApiAddress(props.getProperty(ADDRESS_KEY));
        setAcceptHeader(props.getProperty(ACCEPT_HDR_KEY));
        setUserName(props.getProperty(USER_KEY));
        setUserToken(props.getProperty(TOKEN_KEY));
    }

    private static void validatePropertyMissing(String value, String key) {
        if (value == null) {
            throw new InvalidParameterException(
                    MessageFormat.format("Missing key {0}!", key));
        }
    }
}
