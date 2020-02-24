package pl.pawelszopinski.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.option.Authenticate;
import pl.pawelszopinski.option.Help;
import pl.pawelszopinski.option.Owner;
import pl.pawelszopinski.option.Repository;
import pl.pawelszopinski.util.HttpRequestProcessor;
import pl.pawelszopinski.view.ConsoleDisplay;
import pl.pawelszopinski.view.Displayable;

import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

@Command(name = "stargazers", description = "Print users starring a given repository.")
public class GetStargazers implements Callable<Integer> {

    @Mixin
    private Owner owner;

    @Mixin
    private Repository repository;

    @Mixin
    private Authenticate auth;

    @Mixin
    private Help help;

    @Override
    public Integer call() throws Exception {
        Displayable displayMethod = new ConsoleDisplay();

        HttpRequestProcessor requestProc =
                new HttpRequestProcessor(Configuration.getUserName(), Configuration.getUserToken());

        String uri = "repos/" + owner.getOwner() + "/" +
                repository.getRepository() + "/stargazers";

        HttpResponse<String> response =
                requestProc.sendGet(uri, HttpResponse.BodyHandlers.ofString(), auth.isAuth());

        displayMethod.showJson(response);

        return 0;
    }
}

