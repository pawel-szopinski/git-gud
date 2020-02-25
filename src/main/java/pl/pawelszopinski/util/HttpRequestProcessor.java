package pl.pawelszopinski.util;

import org.apache.commons.lang3.StringUtils;
import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.view.ConsoleDisplay;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

public class HttpRequestProcessor {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    private final String userName;
    private final String token;

    public HttpRequestProcessor(String userName, String token) {
        this.userName = userName;
        this.token = token;
    }

    public <T> HttpResponse<T> sendGet(String uri, HttpResponse.BodyHandler<T> bodyType, boolean authenticate)
            throws IOException, InterruptedException {
        HttpRequest.Builder httpBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(Configuration.getApiAddress() + uri))
                .header("Accept", Configuration.getAcceptHeader());

        if (authenticate && StringUtils.isNotBlank(userName) && StringUtils.isNotBlank(token)) {
            httpBuilder.header("Authorization", basicAuth());
        } else if (authenticate && (StringUtils.isBlank(userName) || StringUtils.isBlank(token))) {
            new ConsoleDisplay().showWarning("Authentication flag was present, " +
                    "but login details are missing in properties file. If the data you are trying " +
                    "to query is not public, you will receive not found error.");
        }

        HttpRequest request = httpBuilder.build();

        return HTTP_CLIENT.send(request, bodyType);
    }

    private String basicAuth() {
        return "Basic " + Base64.getEncoder().encodeToString((userName + ":" + token).getBytes());
    }
}
