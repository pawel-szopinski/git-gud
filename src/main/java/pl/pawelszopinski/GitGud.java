package pl.pawelszopinski;

import com.google.common.reflect.ClassPath;
import com.indvd00m.ascii.render.Render;
import com.indvd00m.ascii.render.api.ICanvas;
import com.indvd00m.ascii.render.api.IContextBuilder;
import com.indvd00m.ascii.render.api.IRender;
import com.indvd00m.ascii.render.elements.PseudoText;
import picocli.CommandLine;
import picocli.CommandLine.Command;
import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.exception.ReadPropertiesException;
import pl.pawelszopinski.handler.CmdLineExceptionMsgHandler;
import pl.pawelszopinski.handler.PrintHandler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.concurrent.Callable;

@Command(version = "Version: 1.0",
        description = "Application that allows pulling various information from " +
                "GitHub REST API in JSON format.",
        mixinStandardHelpOptions = true)
public class GitGud implements Callable<Integer> {

    public static void main(String[] args) {

        loadProperties();

        int exitCode = createCommandLine().execute(args);

//        int exitCode = createCommandLine().execute("stargazers", "-o", "kfechter", "-r", "LegionY530Ubuntu", "-a");

//        int exitCode = new CommandLine(new GitGud())
//                .setExecutionExceptionHandler(new CmdLineExceptionMsgHandler())
//                .execute("stargazers", "-o", "kfechter", "-r", "LegionY530Ubuntu");

//        int exitCode = new CommandLine(new GitGud()).execute(
//                "stargazers", "-o", "cschool-cinema", "-r", "cinema-api", "-a");

//        int exitCode = new CommandLine(new GitGud()).execute(
//                "stargazers", "-h");

//        int exitCode = new CommandLine(new GitGud())
//                .setExecutionExceptionHandler(new CmdLineExceptionMsgHandler())
//                .execute("commit-info", "-o", "pawel-szopinski", "-r", "implementers-toolbox",
//                        "e00b9d96964d110e09fdd8816c8b5ce0efc6b40e",
//                        "8fe850f5a5c339e462f682890892846fb02b29b4", "x", "-a" , "-v");

//        int exitCode = new CommandLine(new GitGud()).execute(
//                "commit-info", "-o", "pawel-szopinski", "-r", "loginapp", "0dd89aa0437da3141f371c96c1234a76cc404c92",
//                "-a");

//        int exitCode = new CommandLine(new GitGud()).execute("commit-info", "-h");

        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        showAppLogo();
        CommandLine.usage(createCommandLine(), System.out);

        return 0;
    }

    private static void loadProperties() {
        try {
            Configuration.readFromFile();
        } catch (ReadPropertiesException e) {
            PrintHandler.printException(e.getMessage());
            System.exit(1);
        }
    }

    private static CommandLine createCommandLine() {
        CommandLine cl = new CommandLine(new GitGud())
                .setExecutionExceptionHandler(new CmdLineExceptionMsgHandler());

        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try {
            ClassPath classpath = ClassPath.from(loader);

            for (ClassPath.ClassInfo classInfo : classpath.getTopLevelClasses(
                    "pl.pawelszopinski.subcommand")) {
                Class<?> clazz = Class.forName(classInfo.getName());

                cl.addSubcommand(clazz.getDeclaredConstructor().newInstance());
            }
        } catch (IOException | InstantiationException | ClassNotFoundException |
                IllegalAccessException | NoSuchMethodException | InvocationTargetException e) {
            PrintHandler.printException("Could not load commands.");
            System.exit(1);
        }

        return cl;
    }

    private void showAppLogo() {
        IRender render = new Render();
        IContextBuilder builder = render.newBuilder();
        builder.width(90).height(12);
        builder.element(new PseudoText("Git Gud"));
        ICanvas canvas = render.render(builder.build());
        String s = canvas.getText();
        System.out.println(s);
    }
}
