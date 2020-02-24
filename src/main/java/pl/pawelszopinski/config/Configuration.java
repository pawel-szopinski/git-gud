package pl.pawelszopinski.config;

import pl.pawelszopinski.GitGud;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.Properties;

public class Configuration {

    private static final String CONFIG_FILE = "/git-gud.properties";

    private static String apiAddress;
    private static String headerName;
    private static String headerValue;
    private static String userName;
    private static String userPassword;

    private Configuration() {
    }

    public static String getApiAddress() {
        return apiAddress;
    }

    private static void setApiAddress(String apiAddress) {
        Configuration.apiAddress = apiAddress;
    }

    public static String getHeaderName() {
        return headerName;
    }

    private static void setHeaderName(String headerName) {
        Configuration.headerName = headerName;
    }

    public static String getHeaderValue() {
        return headerValue;
    }

    private static void setHeaderValue(String headerValue) {
        Configuration.headerValue = headerValue;
    }

    public static String getUserName() {
        return userName;
    }

    private static void setUserName(String userName) {
        Configuration.userName = userName;
    }

    public static String getUserPassword() {
        return userPassword;
    }

    private static void setUserPassword(String userPassword) {
        Configuration.userPassword = userPassword;
    }

    public static void readFromFile() throws IOException, InvalidParameterException {
        File jarPath = new File(GitGud.class.getProtectionDomain().getCodeSource().getLocation().getPath());
        String propertiesPath = jarPath.getParent();

        InputStream input = new FileInputStream(propertiesPath + CONFIG_FILE);

        Properties props = new PropertiesExtended();

        props.load(input);

        setApiAddress(props.getProperty("api.address"));
        setHeaderName(props.getProperty("api.header.name"));
        setHeaderValue(props.getProperty("api.header.value"));
        setUserName(props.getProperty("user.name"));
        setUserPassword(props.getProperty("user.password"));
    }
}
