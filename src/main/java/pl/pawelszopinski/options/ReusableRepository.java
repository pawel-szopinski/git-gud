package pl.pawelszopinski.options;

import picocli.CommandLine.Option;

public class ReusableRepository {
    @Option(names = {"-r", "--repository"}, required = true, paramLabel = "<name>", description =
            "Repository name.")
    private String repository;

    public String getRepository() {
        return repository;
    }
}
