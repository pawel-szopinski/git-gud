package pl.pawelszopinski.option;

import picocli.CommandLine.Option;

public class ReusableOwner {
    @Option(names = {"-o", "--owner"}, required = true, paramLabel = "<name>", description = "Owner's " +
            "account name.")
    private String owner;

    public String getOwner() {
        return owner;
    }
}
