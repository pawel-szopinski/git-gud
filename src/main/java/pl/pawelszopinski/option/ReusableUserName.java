package pl.pawelszopinski.option;

import picocli.CommandLine.Option;

public class ReusableUserName {
    @Option(names = {"-u", "--user"}, paramLabel = "<name>", description =
            "User's account name.")
    private String userName;

    public String getUserName() {
        return userName;
    }
}
