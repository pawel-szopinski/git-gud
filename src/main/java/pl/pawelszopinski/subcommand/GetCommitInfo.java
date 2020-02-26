package pl.pawelszopinski.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.http.HttpRequestService;
import pl.pawelszopinski.option.*;
import pl.pawelszopinski.view.ConsoleDisplayService;
import pl.pawelszopinski.view.DisplayService;

import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

@Command(name = "commit-info", description = "Print info about one or more commits.")
public class GetCommitInfo implements Callable<Integer> {

    @Mixin
    private Owner owner;

    @Mixin
    private Repository repository;

    @Parameters(arity = "1..*", description = "One or more commit SHAs.")
    private String[] shaArray;

    @Mixin
    private Authenticate auth;

    @Mixin
    private Verbose verbose;

    @Mixin
    private Help help;

    @Override
    public Integer call() throws Exception {
        DisplayService displayMethod = new ConsoleDisplayService();

        HttpRequestService requestProc =
                new HttpRequestService(Configuration.getUserName(), Configuration.getUserToken());

        for (String sha : shaArray) {
            String uri = "repos/" + owner.getOwner() + "/" +
                    repository.getRepository() + "/git/commits/" + sha;

            HttpResponse<String> response =
                    requestProc.sendGet(uri, HttpResponse.BodyHandlers.ofString(), auth.isAuth());

            displayMethod.showJson(response.body());
        }

        return 0;
    }
}