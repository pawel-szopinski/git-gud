package pl.pawelszopinski.view;

import java.net.http.HttpResponse;

public interface Displayable {
    void showJson(HttpResponse<String> response);

    void showErrorMsg(String msg);

    void showWarning(String msg);
}
