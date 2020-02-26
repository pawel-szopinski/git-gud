package pl.pawelszopinski.subcommand;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.entity.User;
import pl.pawelszopinski.http.HttpRequestService;
import pl.pawelszopinski.option.*;
import pl.pawelszopinski.view.ConsoleDisplayService;
import pl.pawelszopinski.view.DisplayService;

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
    private Verbose verbose;

    @Mixin
    private Help help;

    private final DisplayService displaySvc = new ConsoleDisplayService();
    private final HttpRequestService requestProc =
            new HttpRequestService(Configuration.getUserName(), Configuration.getUserToken());
    private final Gson gson = new Gson();
    private String linkHeader = "";
    private HttpResponse<String> response;
    private StringBuilder combinedJsons = new StringBuilder();
    private List<User> userList = new ArrayList<>();

    @Override
    public Integer call() throws Exception {
        int i = 1;

        while (linkHeader != null && !linkHeader.endsWith("rel=\"first\"")) {
            String uri = "repos/" + owner.getOwner() + "/" +
                    repository.getRepository() + "/stargazers?per_page=30&page=" + i;

            response = requestProc.sendGet(uri, HttpResponse.BodyHandlers.ofString(), auth.isAuth());

            if (response.statusCode() != HttpStatus.SC_OK) {
                displaySvc.showError("HTTP error: " + response.statusCode() + ", " +
                        EnglishReasonPhraseCatalog.INSTANCE.getReason(response.statusCode(), null));
                return response.statusCode();
            }

            linkHeader = response.headers().firstValue("link").orElse(null);

            if (verbose.isVerbose()) {
                compileVerboseResult(linkHeader, i);
            } else {
                compileListResult(response.body());
            }

            i++;
        }

//        displaySvc.showJson(combinedJsons.toString());

        return HttpStatus.SC_OK;
    }

    private void compileVerboseResult(String linkHeader, int page) {
        combinedJsons.append(response.body());
    }

    private void compileListResult(String body) {
        userList.addAll(Arrays.asList(gson.fromJson(body, User[].class)));
    }
}

