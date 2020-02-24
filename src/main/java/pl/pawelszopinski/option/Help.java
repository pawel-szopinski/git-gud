package pl.pawelszopinski.option;

import picocli.CommandLine.Option;

public class Help {
    @Option(names = {"-h", "--help"}, usageHelp = true, description = "Show this help message and exit.")
    private boolean help;
}
