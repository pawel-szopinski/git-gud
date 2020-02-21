package pl.pawelszopinski.subcommand;

import picocli.CommandLine;
import pl.pawelszopinski.subcommand.util.HttpRequestProcessor;

import java.util.List;
import java.util.concurrent.Callable;

@CommandLine.Command(name = "commit-info", aliases = "ci", mixinStandardHelpOptions = true)
public class GetCommitInfo implements Callable<Integer> {

    @CommandLine.Option(names = {"-o", "--owner"}, required = true, paramLabel = "ACCOUNT", description = "owner's " +
            "account name")
    private String owner;

    @CommandLine.Option(names = {"-r", "--repository"}, required = true, paramLabel = "REPO", description =
            "repository name")
    private String repository;

    @CommandLine.Option(names = {"-cs", "--commit-sha"}, description = "One or more commit SHAs.")
    private List<String> shas;

    @Override
    public Integer call() throws Exception {
        HttpRequestProcessor.printGet("https://api.github.com/repos/" + owner + "/" +
                repository + "/git/commits/9fa8b2ad225452fc8ccb18ae76472803f3bf807c");

        return 0;
    }
}