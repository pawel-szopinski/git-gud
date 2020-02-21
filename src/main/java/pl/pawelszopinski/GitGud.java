package pl.pawelszopinski;

import picocli.CommandLine;
import pl.pawelszopinski.subcommand.GetCommitInfo;

import java.util.concurrent.Callable;

@CommandLine.Command(name = "java -jar /path/to/file.jar", version = "1.0",
        description = "Pulls various information from GitHub REST API in JSON format.",
        mixinStandardHelpOptions = true,
        subcommands = GetCommitInfo.class)
public class GitGud implements Callable<Integer> {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GitGud()).execute(
                "commit-info", "-o", "cschool-cinema", "-r", "kinex", "9fa8b2ad225452fc8ccb18ae76472803f3bf807c");

//        int exitCode = new CommandLine(new GitGud()).execute("commit-info", "-h");
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }
}
