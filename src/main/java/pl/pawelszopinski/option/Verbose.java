package pl.pawelszopinski.option;

import picocli.CommandLine.Option;

public class Verbose {
    @Option(names = {"-v", "--verbose"}, description = "Print full JSON objects as returned from GitHub.")
    private boolean verbose;

    public boolean isVerbose() {
        return verbose;
    }
}
