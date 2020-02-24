package pl.pawelszopinski.option;

import picocli.CommandLine.Option;

public class ReusableToken {
    @Option(names = {"-t", "--token"}, paramLabel = "<token>", description =
            "Authentication token.")
    private String token;

    public String getToken() {
        return token;
    }
}
