package pl.pawelszopinski.handler;

import org.apache.commons.httpclient.util.URIUtil;
import org.apache.commons.lang3.StringUtils;
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

    private static final String USER_TOKEN = Configuration.getUserToken();
    private static final String API_ADDRESS = Configuration.getApiAddress();

    private HttpRequest.Builder httpBuilder = HttpRequest.newBuilder()
            .header("Accept", Configuration.getAcceptHeader());

    public HttpResponse<String> sendGet(String uri, boolean authenticate) throws Exception {
        httpBuilder.GET().uri(URI.create(URIUtil.encodeQuery(API_ADDRESS + uri)));

        return sendRequest(authenticate);
    }

    public int sendPut(String uri) throws Exception {
        httpBuilder.PUT(HttpRequest.BodyPublishers.noBody())
                .uri(URI.create(URIUtil.encodeQuery(API_ADDRESS + uri)));

        return sendRequest(true).statusCode();
    }

    public int sendDelete(String uri) throws Exception {
        httpBuilder.DELETE().uri(URI.create(URIUtil.encodeQuery(API_ADDRESS + uri)));

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
        if (authenticate && StringUtils.isNotEmpty(USER_TOKEN)) {
            httpBuilder.setHeader("Authorization", "token " + USER_TOKEN);
        } else if (authenticate && StringUtils.isEmpty(USER_TOKEN)) {
            throw new IllegalArgumentException("Authentication required by command or " +
                    "authentication flag explicitly mentioned, but token is missing in .properties file.");
        }
    }
}
