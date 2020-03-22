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

@Command(name = "unstar-repo", description = "Unstar a repository " +
        "(auth token is required in .properties file).")
public class UnstarRepository implements Callable<Integer> {

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

        int statusCode = httpRequest.sendDelete(uri);

        if (statusCode != 204) {
            throw new HttpException(MessageFormat.format(
                    "Error: {0} {1}", statusCode,
                    EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, null)));
        }

        PrintHandler.printString("You are no longer starring this repository.");

        return 0;
    }
}
