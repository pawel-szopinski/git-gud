package pl.pawelszopinski.handler;

import org.apache.commons.lang3.StringUtils;
import pl.pawelszopinski.config.Configuration;

import java.io.IOException;
import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;
import java.util.Base64;

public class HttpRequestHandler {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();
    private static final String USER_NAME = Configuration.getUserName();
    private static final String USER_TOKEN = Configuration.getUserToken();

    public HttpResponse<String> sendGet(String uri, boolean authenticate)
            throws IOException, InterruptedException {
        HttpRequest.Builder httpBuilder = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(Configuration.getApiAddress() + uri))
                .header("Accept", Configuration.getAcceptHeader());

        addAuthHeaderIfAuthFlagTrue(httpBuilder, authenticate);

        HttpRequest request = httpBuilder.build();

        try {
            return HTTP_CLIENT.send(request, HttpResponse.BodyHandlers.ofString());
        } catch (ConnectException e) {
            throw new ConnectException("There seems to be no connection to the internet.");
        } catch (HttpTimeoutException e) {
            throw new HttpTimeoutException("Your request timed out. Please try again.");
        }
    }

    private void addAuthHeaderIfAuthFlagTrue(HttpRequest.Builder httpBuilder, boolean authenticate) {
        if (authenticate && StringUtils.isNotBlank(USER_NAME) && StringUtils.isNotBlank(USER_TOKEN)) {
            httpBuilder.header("Authorization", basicAuth());
        } else if (authenticate && (StringUtils.isBlank(USER_NAME) || StringUtils.isBlank(USER_TOKEN))) {
            throw new IllegalArgumentException("Authentication flag was present, " +
                    "but login details are missing/incomplete.");
        }
    }

    private String basicAuth() {
        return "Basic " + Base64.getEncoder().encodeToString((USER_NAME + ":" + USER_TOKEN).getBytes());
    }
}
