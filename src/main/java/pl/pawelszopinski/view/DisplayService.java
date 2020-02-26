package pl.pawelszopinski.view;

public interface DisplayService {
    void showJson(String json);

    void showError(String msg);

    void showWarning(String msg);
}
