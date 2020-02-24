package pl.pawelszopinski.util;

import pl.pawelszopinski.config.Configuration;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class HttpRequestProcessor {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .build();

    private final String userName;
    private final String token;

    public HttpRequestProcessor(String userName, String token) {
        this.userName = userName;
        this.token = token;
    }

    public <T> HttpResponse<T> sendGet(String uri, HttpResponse.BodyHandler<T> bodyType)
            throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(Configuration.getApiAddress() + uri))
                .header(Configuration.getHeaderName(), Configuration.getHeaderValue())
                .header("Authorization", basicAuth())
                .build();

        return HTTP_CLIENT.send(request, bodyType);
    }

    private String basicAuth() {
        if (userName == null || token == null) return "";

        return "Basic " + Base64.getEncoder().encodeToString((userName + ":" + token).getBytes());
    }
}
