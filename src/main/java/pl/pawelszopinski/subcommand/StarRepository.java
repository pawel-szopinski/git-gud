package pl.pawelszopinski.subcommand;

import org.apache.http.impl.EnglishReasonPhraseCatalog;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Spec;
import pl.pawelszopinski.handler.HttpRequestHandler;
import pl.pawelszopinski.handler.PrintHandler;
import pl.pawelszopinski.option.Help;
import pl.pawelszopinski.option.Owner;
import pl.pawelszopinski.option.Repository;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.Callable;

@Command(name = "star-repo", description = "Star a repository. " +
        "(auth token is required in .properties file).")
public class StarRepository implements Callable<Integer> {

    private String uri = "/user/starred/{owner}/{repo}";

    @Spec
    private CommandSpec spec;

    @Mixin
    private Owner owner;

    @Mixin
    private Repository repository;

    @Mixin
    private Help help;

    @Override
    public Integer call() throws Exception {
        uri = uri
                .replace("{owner}",
                        URLEncoder.encode(owner.getOwner(), StandardCharsets.UTF_8))
                .replace("{repo}",
                        URLEncoder.encode(repository.getRepository(), StandardCharsets.UTF_8));

        HttpRequestHandler httpRequest = new HttpRequestHandler();

        int statusCode = httpRequest.sendPut(uri);

        if (statusCode != 204) {
            PrintHandler.printString("Request Failed: " + statusCode + " " +
                    EnglishReasonPhraseCatalog.INSTANCE.getReason(statusCode, null) + ".", spec);
        } else {
            PrintHandler.printString("You are now starring this repository.", spec);
        }

        return 0;
    }
}
