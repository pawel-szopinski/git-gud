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
import pl.pawelszopinski.util.VersionProvider;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.util.concurrent.Callable;

@Command(name = "java -jar /path/to/app.jar",
        description = "An application that allows pulling various information from " +
                "GitHub REST API.",
        mixinStandardHelpOptions = true,
        versionProvider = VersionProvider.class)
public class GitGud implements Callable<Integer> {

    public static void main(String[] args) {
        loadProperties();

        int exitCode = createCommandLine().execute(args);

        System.exit(exitCode);
    }

    @Override
    public Integer call() {
        showAppLogo();
        CommandLine.usage(createCommandLine(), System.out);

        return 0;
    }

    private static void loadProperties() {
        File jarPath = null;
        try {
            jarPath = new File(GitGud.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            PrintHandler.printException(e.getMessage());
            System.exit(1);
        }

        String propertiesPath = jarPath.getParent() + "/" + "git-gud.properties";

        try {
            Configuration.readFromFile(propertiesPath);
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
            PrintHandler.printException("Could not load available commands.");
            System.exit(1);
        }

        return cl;
    }

    private void showAppLogo() {
        IRender render = new Render();
        IContextBuilder builder = render.newBuilder();
        builder.width(120).height(12);
        builder.element(new PseudoText("Git Gud"));
        ICanvas canvas = render.render(builder.build());
        String s = canvas.getText();
        System.out.println(s);
    }
}
