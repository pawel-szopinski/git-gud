package pl.pawelszopinski.result;

import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.handler.HttpRequestHandler;

abstract class ResultCompiler {

    private final HttpRequestHandler httpRequest =
            new HttpRequestHandler(Configuration.getUserName(), Configuration.getUserToken());
    private final String uri;
    private final boolean authenticate;

    ResultCompiler(String uri, boolean authenticate) {
        this.uri = uri;
        this.authenticate = authenticate;
    }

    String getUri() {
        return uri;
    }

    boolean authenticate() {
        return authenticate;
    }

    public HttpRequestHandler getHttpRequest() {
        return httpRequest;
    }
}