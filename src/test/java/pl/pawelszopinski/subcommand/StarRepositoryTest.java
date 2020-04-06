package pl.pawelszopinski.subcommand;

import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.mockserver.integration.ClientAndServer;
import picocli.CommandLine;
import pl.pawelszopinski.Utils;
import pl.pawelszopinski.config.Configuration;

import java.io.PrintWriter;
import java.io.StringWriter;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.startsWith;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockserver.integration.ClientAndServer.startClientAndServer;
import static org.mockserver.model.HttpRequest.request;
import static org.mockserver.model.HttpResponse.response;

@TestInstance(Lifecycle.PER_CLASS)
class StarRepositoryTest {

    private final ClientAndServer mockServer = startClientAndServer(1080);

    @AfterAll
    void cleanUp() {
        mockServer.stop();
    }

    @Test
    void testStarRepoSuccess() {
        String serverResponse = "You are now starring this repository.";

        //given
        Configuration.readFromFile(Utils.getFullResourcePath("git-gud-withtoken.properties"));

        mockServer.when(request()
                .withMethod("PUT")
                .withPath("/user/starred/pawel-szopinski/git-gud")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(204).withBody(serverResponse));

        //when
        CommandLine cmd = new CommandLine(new StarRepository());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-o pawel-szopinski -r git-gud")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertEquals(serverResponse, StringUtils.chomp(sw.toString()));
    }

    @Test
    void testStarRepoNotFound() {
        //given
        Configuration.readFromFile(Utils.getFullResourcePath("git-gud-withtoken.properties"));

        mockServer.when(request()
                .withMethod("PUT")
                .withPath("/user/starred/pawel-szopinski/repo-that-does-not-exist")
                .withHeader(HttpHeaders.ACCEPT, Configuration.getAcceptHeader()))
                .respond(response().withStatusCode(404));

        //when
        CommandLine cmd = new CommandLine(new StarRepository());
        StringWriter sw = new StringWriter();
        cmd.setOut(new PrintWriter(sw));

        String[] args = ("-o pawel-szopinski -r repo-that-does-not-exist")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(0, exitCode);
        assertThat(sw.toString(), startsWith("Request Failed"));
    }

    @Test
    void testStarRepoMissingToken() {
        //given
        Configuration.readFromFile(Utils.getFullResourcePath("git-gud-notoken.properties"));

        //when
        CommandLine cmd = new CommandLine(new StarRepository());
        StringWriter sw = new StringWriter();
        cmd.setErr(new PrintWriter(sw));

        String[] args = ("-o pawel-szopinski -r git-gud")
                .split(" ");
        int exitCode = cmd.execute(args);

        //then
        assertEquals(1, exitCode);
        assertThat(sw.toString(), containsString("but token is missing"));
    }
}