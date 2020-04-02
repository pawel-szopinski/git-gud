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
class GetBranchesTest {

    private final ClientAndServer mockServer = startClientAndServer(1080);
    private String serverResponse;

    @BeforeAll
    void init() throws IOException {
        URL url = Thread.currentThread().getContextClassLoader()
                .getResource("git-gud-withtoken.properties");
        String propsPath = Objects.requireNonNull(url).getPath();

        Configuration.readFromFile(propsPath);

        URL rawResponseUrl = Thread.currentThread().getContextClassLoader()
                .getResource("branches/branches-server.json");
        String rawResponsePath = Objects.requireNonNull(rawResponseUrl).getPath();

        serverResponse = new String(Files.readAllBytes(Paths.get(rawResponsePath)));
    }

    @AfterAll
    void cleanUp() {
        mockServer.stop();
    }

    @Test
    void testParsedResultReturnCorrectData() throws IOException {
        //given
        URL parsedResponseUrl = Thread.currentThread().getContextClassLoader()
                .getResource("branches/branches-parsed.txt");
        String parsedResponsePath = Objects.requireNonNull(parsedResponseUrl).getPath();

        String parsedResponse = new String(Files.readAllBytes(Paths.get(parsedResponsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/pawel-szopinski/git-gud/branches")
                .withQueryStringParameter("per_page", "100")
                .withQueryStringParameter("page", "1")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponse));

        //when
        CommandLine cmd = new CommandLine(new GetBranches());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = "-o pawel-szopinski -r git-gud".split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(parsedResponse, sw.toString());
    }

    @Test
    void testVerboseResultReturnCorrectData() throws IOException {
        //given
        URL verboseResponseUrl = Thread.currentThread().getContextClassLoader()
                .getResource("branches/branches-verbose.json");
        String verboseResponsePath = Objects.requireNonNull(verboseResponseUrl).getPath();

        String verboseResponse = new String(Files.readAllBytes(Paths.get(verboseResponsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/repos/pawel-szopinski/git-gud/branches")
                .withQueryStringParameter("per_page", "100")
                .withQueryStringParameter("page", "1")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponse));

        //when
        CommandLine cmd = new CommandLine(new GetBranches());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = "-o pawel-szopinski -r git-gud -v".split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(verboseResponse, sw.toString());
    }
}