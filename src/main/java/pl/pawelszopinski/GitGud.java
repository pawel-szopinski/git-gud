package pl.pawelszopinski;

import picocli.CommandLine;
import pl.pawelszopinski.subcommand.GetCommitInfo;

import java.util.concurrent.Callable;

@CommandLine.Command(description = "desc",
        mixinStandardHelpOptions = true, version = "1.0",
        subcommands = GetCommitInfo.class)
public class GitGud implements Callable<Integer> {

    public static void main(String[] args) {
        int exitCode = new CommandLine(new GitGud()).execute("ci", "-o", "cschool-cinema", "-r", "kinex");
        System.exit(exitCode);
    }

    @Override
    public Integer call() throws Exception {
        CommandLine.usage(this, System.out);
        return 0;
    }
}
