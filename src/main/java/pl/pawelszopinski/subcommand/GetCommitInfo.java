package pl.pawelszopinski.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import pl.pawelszopinski.option.*;
import pl.pawelszopinski.util.HttpRequestProcessor;
import pl.pawelszopinski.view.ConsoleDisplay;
import pl.pawelszopinski.view.Displayable;

import java.net.http.HttpResponse;
import java.util.concurrent.Callable;

@Command(name = "commit-info", description = "Print info about one or more commits.")
public class GetCommitInfo implements Callable<Integer> {

    @Mixin
    private ReusableOwner owner;

    @Mixin
    private ReusableRepository repository;

    @Parameters(arity = "1..*", description = "One or more commit SHAs.")
    private String[] shaArray;

    @Mixin
    private ReusableUserName userName;

    @Mixin
    private ReusableToken token;

    @Mixin
    private ReusableHelp help;

    @Override
    public Integer call() throws Exception {
        Displayable displayMethod = new ConsoleDisplay();

        HttpRequestProcessor requestProc =
                new HttpRequestProcessor(userName.getUserName(), token.getToken());

        for (String sha : shaArray) {
            String uri = "repos/" + owner.getOwner() + "/" +
                    repository.getRepository() + "/git/commits/" + sha;

            HttpResponse<String> response =
                    requestProc.sendGet(uri, HttpResponse.BodyHandlers.ofString());

            displayMethod.showJson(response);
        }

        return 0;
    }
}