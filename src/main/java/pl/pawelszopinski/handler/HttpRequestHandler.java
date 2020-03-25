package pl.pawelszopinski.handler;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHeaders;
import pl.pawelszopinski.config.Configuration;

import java.net.ConnectException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.net.http.HttpTimeoutException;

public class HttpRequestHandler {

    private static final HttpClient HTTP_CLIENT = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_1_1)
            .build();

    private HttpRequest.Builder httpBuilder = HttpRequest.newBuilder()
            .header(HttpHeaders.ACCEPT, Configuration.getAcceptHeader());

    public HttpResponse<String> sendGet(String uri, boolean authenticate) throws Exception {
        httpBuilder.GET().uri(URI.create(URIUtil.encodeQuery(
                Configuration.getApiAddress() + uri)));

        return sendRequest(authenticate);
    }

    public int sendPut(String uri) throws Exception {
        httpBuilder.PUT(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(URIUtil.encodeQuery(Configuration.getApiAddress() + uri)));

        return sendRequest(true).statusCode();
    }

    public int sendDelete(String uri) throws Exception {
        httpBuilder.DELETE().uri(URI.create(URIUtil.encodeQuery(Configuration.getApiAddress() + uri)));

        return sendRequest(true).statusCode();
    }

    private HttpResponse<String> sendRequest(boolean authenticate) throws Exception {
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
        if (authenticate && StringUtils.isNotEmpty(Configuration.getUserToken())) {
            httpBuilder.setHeader(HttpHeaders.AUTHORIZATION, "token " + Configuration.getUserToken());
        } else if (authenticate && StringUtils.isEmpty(Configuration.getUserToken())) {
            throw new IllegalArgumentException("Authentication required by command or " +
                    "authentication flag explicitly mentioned, but token is missing in .properties file.");
        }
    }
}
