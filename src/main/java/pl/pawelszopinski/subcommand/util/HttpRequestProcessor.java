package pl.pawelszopinski.subcommand.util;

import com.cedarsoftware.util.io.JsonWriter;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class HttpRequestProcessor {

    private static final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    private HttpRequestProcessor() {
    }

    public static void printGet(String uri) throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(uri))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        String formattedBody = JsonWriter.formatJson(response.body());

        System.out.println(formattedBody);
    }
}
