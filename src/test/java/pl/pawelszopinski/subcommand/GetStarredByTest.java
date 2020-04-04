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
class GetStarredByTest {

    private final ClientAndServer mockServer = startClientAndServer(1080);
    private String serverResponse;
    private String serverResponseNotFound;

    @BeforeAll
    static void loadProperties() {
        Configuration.readFromFile(Utils.getFullResourcePath("git-gud-withtoken.properties"));
    }

    @BeforeAll
    void loadServerResponses() throws IOException {
        String rawResponsePath = Utils.getFullResourcePath("starred-by/starred-by-server.txt");
        serverResponse = new String(Files.readAllBytes(Paths.get(rawResponsePath)));

        String rawResponseNotFoundPath = Utils.getFullResourcePath("starred-by/not-found-server.txt");
        serverResponseNotFound = new String(Files.readAllBytes(Paths.get(rawResponseNotFoundPath)));
    }

    @AfterAll
    void cleanUp() {
        mockServer.stop();
    }

    @Test
    void testParsedSortedResultReturnCorrectData() throws IOException {
        //given
        String parsedResponsePath = Utils.getFullResourcePath("starred-by/starred-by-parsed.txt");
        String parsedResponse = new String(Files.readAllBytes(Paths.get(parsedResponsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/users/pawel-szopinski/starred")
                .withQueryStringParameter("per_page", "100")
                .withQueryStringParameter("page", "1")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponse));

        //when
        CommandLine cmd = new CommandLine(new GetStarredBy());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-u pawel-szopinski -s")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(parsedResponse, StringUtils.chomp(sw.toString()));
    }

    @Test
    void testVerboseResultReturnCorrectData() throws IOException {
        //given
        String verboseResponsePath = Utils.getFullResourcePath("starred-by/starred-by-verbose.txt");
        String verboseResponse = new String(Files.readAllBytes(Paths.get(verboseResponsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/users/pawel-szopinski/starred")
                .withQueryStringParameter("per_page", "100")
                .withQueryStringParameter("page", "1")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponse));

        //when
        CommandLine cmd = new CommandLine(new GetStarredBy());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-v -u pawel-szopinski")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(verboseResponse, StringUtils.chomp(sw.toString()));
    }

    @Test
    void testVerboseNotFoundResultReturnCorrectData() throws IOException {
        //given
        String verboseResponsePath = Utils.getFullResourcePath("starred-by/not-found-verbose.txt");
        String verboseResponse = new String(Files.readAllBytes(Paths.get(verboseResponsePath)));

        mockServer.when(request()
                .withMethod("GET")
                .withPath("/users/idonotexsistongihub/starred")
                .withQueryStringParameter("per_page", "100")
                .withQueryStringParameter("page", "1")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(200).withBody(serverResponseNotFound));

        //when
        CommandLine cmd = new CommandLine(new GetStarredBy());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-v -u idonotexsistongihub")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(verboseResponse, StringUtils.chomp(sw.toString()));
    }
}