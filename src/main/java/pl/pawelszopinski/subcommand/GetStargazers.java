package pl.pawelszopinski.subcommand;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.entity.User;
import pl.pawelszopinski.option.*;
import pl.pawelszopinski.service.HttpRequestService;
import pl.pawelszopinski.view.ConsoleDisplayService;
import pl.pawelszopinski.view.DisplayService;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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
    private final HttpRequestService httpRequest =
            new HttpRequestService(Configuration.getUserName(), Configuration.getUserToken());
    private final Gson gson = new Gson();

    @Override
    public Integer call() throws Exception {
        HttpResponse<String> response;
        StringBuilder combinedJsons = new StringBuilder();
        List<User> userList = new ArrayList<>();
        String linkHeader;

        int pageNo = 1;

        do {
            String uri = "repos/" + owner.getOwner() + "/" +
                    repository.getRepository() + "/stargazers?per_page=30&page=" + pageNo;

            response = httpRequest.sendGet(uri, HttpResponse.BodyHandlers.ofString(), auth.isAuth());

            if (response.statusCode() != HttpStatus.SC_OK) {
                displaySvc.showError("HTTP error: " + response.statusCode() + ", " +
                        EnglishReasonPhraseCatalog.INSTANCE.getReason(response.statusCode(), null));
                return response.statusCode();
            }

            linkHeader = response.headers().firstValue("link").orElse(null);

            if (verbose.isVerbose()) {
                compileVerboseResult(combinedJsons, response.body());
            } else {
                compileListResult(userList, response.body());
            }

            pageNo++;
        } while (linkHeader != null && getLastPage(linkHeader) >= pageNo);

//        displaySvc.showJson(combinedJsons.toString());

        return HttpStatus.SC_OK;
    }

    private void compileVerboseResult(StringBuilder combinedJsons, String responseBody) {
        combinedJsons.append(responseBody);
    }

    private void compileListResult(List<User> userList, String body) {
        userList.addAll(Arrays.asList(gson.fromJson(body, User[].class)));
    }

    private int getLastPage(String linkHeader) {
        Pattern p = Pattern.compile("page=\\d+>; rel=\"last\"");
        Matcher m = p.matcher(linkHeader);

        if (!m.find()) {
            return 0;
        }

        return Integer.parseInt(m.group().replaceAll("[^\\d]", ""));
    }
}

