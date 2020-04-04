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
class GetBranchesTest {

    private final ClientAndServer mockServer = startClientAndServer(1080);
    private String serverResponse;

    @BeforeAll
    void init() throws IOException {
        Configuration.readFromFile(Utils.getFullResourcePath("git-gud-withtoken.properties"));

        String rawResponsePath = Utils.getFullResourcePath("branches/branches-server.txt");
        serverResponse = new String(Files.readAllBytes(Paths.get(rawResponsePath)));
    }

    @AfterAll
    void cleanUp() {
        mockServer.stop();
    }

    @Test
    void testParsedResultReturnCorrectData() throws IOException {
        //given
        String parsedResponsePath = Utils.getFullResourcePath("branches/branches-parsed.txt");
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
        assertEquals(parsedResponse, StringUtils.chomp(sw.toString()));
    }

    @Test
    void testVerboseResultReturnCorrectData() throws IOException {
        //given
        String verboseResponsePath = Utils.getFullResourcePath("branches/branches-verbose.txt");
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
        assertEquals(verboseResponse, StringUtils.chomp(sw.toString()));
    }
}