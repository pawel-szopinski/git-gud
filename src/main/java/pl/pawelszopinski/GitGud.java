package pl.pawelszopinski;

import picocli.CommandLine;
import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.subcommand.GetCommitInfo;
import pl.pawelszopinski.subcommand.GetStargazers;
import pl.pawelszopinski.view.ConsoleDisplay;
import pl.pawelszopinski.view.Displayable;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.concurrent.Callable;

@CommandLine.Command(version = "Version: 1.0",
        description = "Application that allows pulling various information from " +
                "GitHub REST API in JSON format.",
        mixinStandardHelpOptions = true,
        subcommands = {GetCommitInfo.class, GetStargazers.class})
public class GitGud implements Callable<Integer> {

    public static void main(String[] args) {

        loadProperties();

        int exitCode = new CommandLine(new GitGud()).execute(args);

//        int exitCode = new CommandLine(new GitGud()).execute(
//                "stargazers", "-o", "cschool-cinema", "-r", "kinex", "-a");
//
//        int exitCode = new CommandLine(new GitGud()).execute(
//                "stargazers", "-h");

//        int exitCode = new CommandLine(new GitGud()).execute(
//                "commit-info", "-o", "pawel-szopinski", "-r", "implementers-toolbox",
//                "e00b9d96964d110e09fdd8816c8b5ce0efc6b40e",
//                "8fe850f5a5c339e462f682890892846fb02b29b4", "-a");

//        int exitCode = new CommandLine(new GitGud()).execute(
//                "commit-info", "-o", "pawel-szopinski", "-r", "loginapp", "0dd89aa0437da3141f371c96c1234a76cc404c92",
//                "-a");

//        int exitCode = new CommandLine(new GitGud()).execute("commit-info", "-h");
        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        CommandLine.usage(this, System.out);
        return 0;
    }

    private static void loadProperties() {
        Displayable display = new ConsoleDisplay();

        try {
            Configuration.readFromFile();
        } catch (IOException e) {
            display.showErrorMsg("Error reading application properties file! " +
                    "Make sure that it exists in the same directory as JAR file.");
            display.showErrorMsg("Message: " + e.getMessage());
            System.exit(1);
        } catch (InvalidParameterException e) {
            display.showErrorMsg("Error reading application property values. Message: " + e.getMessage());
            System.exit(1);
        }
    }
}
