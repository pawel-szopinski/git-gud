package pl.pawelszopinski.result;

import pl.pawelszopinski.handler.HttpRequestHandler;

public class ResultCompilerBasicInfo {

    private final HttpRequestHandler httpRequest = new HttpRequestHandler();
    private final String uri;
    private final boolean authenticate;

    public ResultCompilerBasicInfo(String uri, boolean authenticate) {
        this.uri = uri;
        this.authenticate = authenticate;
    }

    public HttpRequestHandler getHttpRequest() {
        return httpRequest;
    }

    public String getUri() {
        return uri;
    }

    public boolean isAuthenticate() {
        return authenticate;
    }
}