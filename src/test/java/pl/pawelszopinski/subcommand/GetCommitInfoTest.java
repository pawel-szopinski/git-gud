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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@TestInstance(Lifecycle.PER_CLASS)
class GetCommitInfoTest {

    private final ClientAndServer mockServer = startClientAndServer(1080);
    private String serverResponse;
    private String serverResponseNotFound;

    @BeforeAll
    static void loadProperties() {
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("git-gud-withtoken.properties");
        String propsPath = Objects.requireNonNull(url).getPath();

        Configuration.readFromFile(propsPath);
    }

    @BeforeAll
    void loadServerResponses() throws IOException {
        URL rawResponseUrl = Thread.currentThread().getContextClassLoader()
                .getResource("commit-info/commit-server.json");
        String rawResponsePath = Objects.requireNonNull(rawResponseUrl).getPath();

        serverResponse = new String(Files.readAllBytes(Paths.get(rawResponsePath)));

        URL rawResponseNotFoundUrl = Thread.currentThread().getContextClassLoader()
                .getResource("commit-info/notfound-server.json");
        String rawResponseNotFoundPath = Objects.requireNonNull(rawResponseNotFoundUrl).getPath();

        serverResponseNotFound = new String(Files.readAllBytes(Paths.get(rawResponseNotFoundPath)));
    }

    @AfterAll
    void cleanUp() {
        mockServer.stop();
    }

    @Test
    void testMultipleParsedResultReturnCorrectData() throws IOException {
        //given
        URL parsedResponseUrl = Thread.currentThread().getContextClassLoader()
                .getResource("commit-info/commits-parsed.txt");
        String parsedResponsePath = Objects.requireNonNull(parsedResponseUrl).getPath();

        String parsedResponse = new String(Files.readAllBytes(Paths.get(parsedResponsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/cschool-cinema/kinex/git/commits/9fa8b2ad225452fc8ccb18ae76472803f3bf807c")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponse));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/cschool-cinema/kinex/git/commits/xxx")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(404).withBody(serverResponseNotFound));

        //when
        CommandLine cmd = new CommandLine(new GetCommitInfo());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-o cschool-cinema -r kinex xxx 9fa8b2ad225452fc8ccb18ae76472803f3bf807c")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(parsedResponse, sw.toString());
    }

    @Test
    void testSingleVerboseResultReturnCorrectData() throws IOException {
        //given
        URL verboseResponseUrl = Thread.currentThread().getContextClassLoader()
                .getResource("commit-info/commit-single-verbose.txt");
        String verboseResponsePath = Objects.requireNonNull(verboseResponseUrl).getPath();

        String verboseResponse = new String(Files.readAllBytes(Paths.get(verboseResponsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/cschool-cinema/kinex/git/commits/9fa8b2ad225452fc8ccb18ae76472803f3bf807c")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponse));

        //when
        CommandLine cmd = new CommandLine(new GetCommitInfo());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-v -o cschool-cinema -r kinex 9fa8b2ad225452fc8ccb18ae76472803f3bf807c")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(verboseResponse, sw.toString());
    }

    @Test
    void testMultipleVerboseResultReturnCorrectData() throws IOException {
        //given
        URL verboseResponseUrl = Thread.currentThread().getContextClassLoader()
                .getResource("commit-info/commits-verbose.txt");
        String verboseResponsePath = Objects.requireNonNull(verboseResponseUrl).getPath();

        String verboseResponse = new String(Files.readAllBytes(Paths.get(verboseResponsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/cschool-cinema/kinex/git/commits/9fa8b2ad225452fc8ccb18ae76472803f3bf807c")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponse));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/cschool-cinema/kinex/git/commits/xxx")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(404).withBody(serverResponseNotFound));

        //when
        CommandLine cmd = new CommandLine(new GetCommitInfo());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-v -o cschool-cinema -r kinex 9fa8b2ad225452fc8ccb18ae76472803f3bf807c xxx")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(exitCode, 0);
        assertEquals(verboseResponse, sw.toString());
    }

    @Test
    void testMissingCommitParamReturnError() {
        //when
        CommandLine cmd = new CommandLine(new GetCommitInfo());
        StringWriter sw = new StringWriter();
        cmd.setErr(new PrintWriter(sw));

        String[] args = ("-o cschool-cinema -r kinex")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(2, exitCode);
        assertThat(sw.toString(), startsWith("Missing required parameter: <shaArray>"));
    }
}