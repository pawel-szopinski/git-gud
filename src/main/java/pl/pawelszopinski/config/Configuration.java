package pl.pawelszopinski.config;

import pl.pawelszopinski.GitGud;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.security.InvalidParameterException;
import java.util.Properties;

public class Configuration {

    private static final String CONFIG_FILE = "/git-gud.properties";
    private static final String ADDRESS_KEY = "api.address";
    private static final String ACCEPT_HDR_KEY = "api.accept-header";
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
        Configuration.apiAddress = apiAddress;
    }

    public static String getAcceptHeader() {
        return acceptHeader;
    }

    private static void setAcceptHeader(String acceptHeader) {
        Configuration.acceptHeader = acceptHeader;
    }

    public static String getUserName() {
        return userName;
    }

    private static void setUserName(String userName) {
        Configuration.userName = userName;
    }

    public static String getUserToken() {
        return userToken;
    }

    private static void setUserToken(String userToken) {
        Configuration.userToken = userToken;
    }

    public static void readFromFile() throws IOException, InvalidParameterException, URISyntaxException {
        File jarPath = new File(GitGud.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        String propertiesPath = jarPath.getParent();

        InputStream input = new FileInputStream(propertiesPath + CONFIG_FILE);

        Properties props = new PropertiesExtended();

        props.load(input);

        assignValues(props);
    }

    private static void assignValues(Properties props) {
        setApiAddress(props.getProperty(ADDRESS_KEY));
        setAcceptHeader(props.getProperty(ACCEPT_HDR_KEY));
        setUserName(props.getProperty(USER_KEY));
        setUserToken(props.getProperty(TOKEN_KEY));
    }
}
