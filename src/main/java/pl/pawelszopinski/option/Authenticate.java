package pl.pawelszopinski.option;

import picocli.CommandLine.Option;

public class Authenticate {
    @Option(names = {"-a", "--authenticate"}, description = "Use authentication data from config file.")
    private boolean auth;

    public boolean isAuth() {
        return auth;
    }
}
