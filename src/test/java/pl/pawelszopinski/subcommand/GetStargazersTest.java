package pl.pawelszopinski.subcommand;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockserver.integration.ClientAndServer;
import picocli.CommandLine;
import pl.pawelszopinski.config.Configuration;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Objects;

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
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("git-gud-withtoken.properties");
        String propsPath = Objects.requireNonNull(url).getPath();

        Configuration.readFromFile(propsPath);
    }

    @BeforeAll
    void loadServerResponses() throws IOException {
        URL rawResponseSingleUrl = Thread.currentThread().getContextClassLoader()
                .getResource("stargazers/single-server.json");
        String rawResponseSinglePath = Objects.requireNonNull(rawResponseSingleUrl).getPath();

        serverResponseSingle = new String(Files.readAllBytes(Paths.get(rawResponseSinglePath)));

        URL rawResponseMultiPage1Url = Thread.currentThread().getContextClassLoader()
                .getResource("stargazers/multi1-server.json");
        String rawResponseMultiPage1Path = Objects.requireNonNull(rawResponseMultiPage1Url).getPath();

        serverResponseMultiPage1 = new String(Files.readAllBytes(Paths.get(rawResponseMultiPage1Path)));

        URL rawResponseMultiPage2Url = Thread.currentThread().getContextClassLoader()
                .getResource("stargazers/multi2-server.json");
        String rawResponseMultiPage2Path = Objects.requireNonNull(rawResponseMultiPage2Url).getPath();

        serverResponseMultiPage2 = new String(Files.readAllBytes(Paths.get(rawResponseMultiPage2Path)));
    }

    @AfterAll
    void cleanUp() {
        mockServer.stop();
    }

    @Test
    void testSingleVerboseResultReturnCorrectData() throws IOException {
        //given
        URL verboseResponseUrl = Thread.currentThread().getContextClassLoader()
                .getResource("stargazers/single-verbose.txt");
        String verboseResponsePath = Objects.requireNonNull(verboseResponseUrl).getPath();

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
        assertEquals(verboseResponse, sw.toString());
    }

    @Test
    void testMultiVerboseResultReturnCorrectData() throws IOException {
        //given
        URL verboseResponseUrl = Thread.currentThread().getContextClassLoader()
                .getResource("stargazers/multi-verbose.txt");
        String verboseResponsePath = Objects.requireNonNull(verboseResponseUrl).getPath();

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
        assertEquals(verboseResponse, sw.toString());
    }

    @Test
    void testMultiParsedSortedResultReturnCorrectData() throws IOException {
        //given
        URL responseUrl = Thread.currentThread().getContextClassLoader()
                .getResource("stargazers/multi-parsed.txt");
        String responsePath = Objects.requireNonNull(responseUrl).getPath();

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
        assertEquals(response, sw.toString());
    }
}