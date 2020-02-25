package pl.pawelszopinski.subcommand;

import com.google.gson.Gson;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.entity.User;
import pl.pawelszopinski.option.Authenticate;
import pl.pawelszopinski.option.Help;
import pl.pawelszopinski.option.Owner;
import pl.pawelszopinski.option.Repository;
import pl.pawelszopinski.util.HttpRequestProcessor;
import pl.pawelszopinski.view.ConsoleDisplay;
import pl.pawelszopinski.view.Displayable;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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
                repository.getRepository() + "/stargazers?per_page=100";

        HttpResponse<String> response =
                requestProc.sendGet(uri, HttpResponse.BodyHandlers.ofString(), auth.isAuth());

        String linkHeader = response.headers().firstValue("link").orElse(null);

        Gson gson = new Gson();

        User[] userArray = gson.fromJson(response.body(), User[].class);

        List<User> userList = new ArrayList<>(Arrays.asList(userArray));

        int i = 2;
        while (linkHeader != null && !linkHeader.endsWith("rel=\"first\"")) {
            uri = "repos/" + owner.getOwner() + "/" +
                    repository.getRepository() + "/stargazers?per_page=100&page=" + i;

            response = requestProc.sendGet(uri, HttpResponse.BodyHandlers.ofString(), auth.isAuth());

            linkHeader = response.headers().firstValue("link").orElse(null);

            userList.addAll(Arrays.asList(gson.fromJson(response.body(), User[].class)));

            i++;
        }

        for (User user : userList) {
            System.out.println(user);
        }

//        displayMethod.showJson(response);

        return 0;
    }
}

