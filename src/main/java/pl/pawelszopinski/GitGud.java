package pl.pawelszopinski;

import picocli.CommandLine;
import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.subcommand.GetCommitInfo;
import pl.pawelszopinski.view.ConsoleDisplay;
import pl.pawelszopinski.view.Displayable;

import java.io.IOException;
import java.security.InvalidParameterException;
import java.util.concurrent.Callable;

@CommandLine.Command(version = "Version: 1.0",
        description = "Pulls various information from GitHub REST API in JSON format.",
        mixinStandardHelpOptions = true,
        subcommands = GetCommitInfo.class)
public class GitGud implements Callable<Integer> {

    public static void main(String[] args) {

        loadProperties();
//
//        int exitCode = new CommandLine(new GitGud()).execute(
//                "commit-info", "-o", "cschool-cinema", "-r", "kinex", "9fa8b2ad225452fc8ccb18ae76472803f3bf807c",
//                "x");

        int exitCode = new CommandLine(new GitGud()).execute(
                "commit-info", "-o", "pawel-szopinski", "-r", "loginapp", "0dd89aa0437da3141f371c96c1234a76cc404c92");

//        int exitCode = new CommandLine(new GitGud()).execute("-V");
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
