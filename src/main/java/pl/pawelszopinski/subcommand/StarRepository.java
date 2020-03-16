package pl.pawelszopinski.subcommand;

import org.apache.http.HttpException;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import pl.pawelszopinski.handler.HttpRequestHandler;
import pl.pawelszopinski.handler.PrintHandler;
import pl.pawelszopinski.option.Help;
import pl.pawelszopinski.option.Owner;
import pl.pawelszopinski.option.Repository;

import java.text.MessageFormat;
import java.util.concurrent.Callable;

@Command(name = "star-repo", description = "Star a given repository.\n" +
        "IMPORTANT: authentication data is required to be provided in app properties file.")
public class StarRepository implements Callable<Integer> {

    private String uri = "user/starred/{owner}/{repo}";

    @Mixin
    private Owner owner;

    @Mixin
    private Repository repository;

    @Mixin
    private Help help;

    @Override
    public Integer call() throws Exception {
        uri = uri.replace("{owner}", owner.getOwner())
                .replace("{repo}", repository.getRepository());

        HttpRequestHandler httpRequest = new HttpRequestHandler();

        int statusCode = httpRequest.sendPut(uri);

        if (statusCode != 204) {
            throw new HttpException(MessageFormat.format(
                    "Error: {0} {1}", statusCode,
                    EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, null)));
        }

        PrintHandler.printString("Repository has been starred successfully.");

        return 0;
    }
}
