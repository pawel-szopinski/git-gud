package pl.pawelszopinski.subcommand;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockserver.integration.ClientAndServer;
import picocli.CommandLine;
import pl.pawelszopinski.Utils;
import pl.pawelszopinski.config.Configuration;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.nio.file.Files;
import java.nio.file.Paths;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@TestInstance(Lifecycle.PER_CLASS)
class GetStargazersTest {

    private final ClientAndServer mockServer = startClientAndServer(1080);
    private String serverResponseSingle;
    private String serverResponseMultiPage1;
    private String serverResponseMultiPage2;

    @BeforeAll
    static void loadProperties() {
        Configuration.readFromFile(Utils.getFullResourcePath("git-gud-withtoken.properties"));
    }

    @BeforeAll
    void loadServerResponses() throws IOException {
        String rawResponseSinglePath = Utils.getFullResourcePath("stargazers/single-server.txt");
        serverResponseSingle = new String(Files.readAllBytes(Paths.get(rawResponseSinglePath)));

        String rawResponseMultiPage1Path = Utils.getFullResourcePath("stargazers/multi1-server.txt");
        serverResponseMultiPage1 = new String(Files.readAllBytes(Paths.get(rawResponseMultiPage1Path)));

        String rawResponseMultiPage2Path = Utils.getFullResourcePath("stargazers/multi2-server.txt");
        serverResponseMultiPage2 = new String(Files.readAllBytes(Paths.get(rawResponseMultiPage2Path)));
    }

    @AfterAll
    void cleanUp() {
        mockServer.stop();
    }

    @Test
    void testSingleVerboseResultReturnCorrectData() throws IOException {
        //given
        String verboseResponsePath = Utils.getFullResourcePath("stargazers/single-verbose.txt");
        String verboseResponse = new String(Files.readAllBytes(Paths.get(verboseResponsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/cschool-cinema/kinex/stargazers")
                .withQueryStringParameter("per_page", "100")
                .withQueryStringParameter("page", "1")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponseSingle));

        //when
        CommandLine cmd = new CommandLine(new GetStargazers());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-v -o cschool-cinema -r kinex")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(verboseResponse, StringUtils.chomp(sw.toString()));
    }

    @Test
    void testMultiVerboseResultReturnCorrectData() throws IOException {
        //given
        String verboseResponsePath = Utils.getFullResourcePath("stargazers/multi-verbose.txt");
        String verboseResponse = new String(Files.readAllBytes(Paths.get(verboseResponsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/github/clipboard-copy-element/stargazers")
                .withQueryStringParameter("per_page", "100")
                .withQueryStringParameter("page", "1")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponseMultiPage1).withHeader("link",
                        "<https://api.github.com/repositories/123195915/stargazers?per_page=100" +
                                "&page=2>; rel=\"next\", <https://api.github" +
                                ".com/repositories/123195915/stargazers?per_page=100&page=2>; rel=\"last\""));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/github/clipboard-copy-element/stargazers")
                .withQueryStringParameter("per_page", "100")
                .withQueryStringParameter("page", "2")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponseMultiPage2).withHeader("link",
                        "<https://api.github.com/repositories/123195915/stargazers?per_page=100" +
                                "&page=1>; rel=\"prev\", <https://api.github" +
                                ".com/repositories/123195915/stargazers?per_page=100&page=1>; rel=\"first\""));

        //when
        CommandLine cmd = new CommandLine(new GetStargazers());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-v -o github -r clipboard-copy-element")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(verboseResponse, StringUtils.chomp(sw.toString()));
    }

    @Test
    void testMultiParsedSortedResultReturnCorrectData() throws IOException {
        //given
        String responsePath = Utils.getFullResourcePath("stargazers/multi-parsed.txt");
        String response = new String(Files.readAllBytes(Paths.get(responsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/github/clipboard-copy-element/stargazers")
                .withQueryStringParameter("per_page", "100")
                .withQueryStringParameter("page", "1")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponseMultiPage1).withHeader("link",
                        "<https://api.github.com/repositories/123195915/stargazers?per_page=100" +
                                "&page=2>; rel=\"next\", <https://api.github" +
                                ".com/repositories/123195915/stargazers?per_page=100&page=2>; rel=\"last\""));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/github/clipboard-copy-element/stargazers")
                .withQueryStringParameter("per_page", "100")
                .withQueryStringParameter("page", "2")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponseMultiPage2).withHeader("link",
                        "<https://api.github.com/repositories/123195915/stargazers?per_page=100" +
                                "&page=1>; rel=\"prev\", <https://api.github" +
                                ".com/repositories/123195915/stargazers?per_page=100&page=1>; rel=\"first\""));

        //when
        CommandLine cmd = new CommandLine(new GetStargazers());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-s -o github -r clipboard-copy-element")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(response, StringUtils.chomp(sw.toString()));
    }
}