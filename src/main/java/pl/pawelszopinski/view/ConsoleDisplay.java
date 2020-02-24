package pl.pawelszopinski.view;

import com.cedarsoftware.util.io.JsonWriter;

import java.net.http.HttpResponse;

public class ConsoleDisplay implements Displayable {

    @Override
    public void showJson(HttpResponse<String> response) {
        String formattedBody = JsonWriter.formatJson(String.valueOf(response.body()));

        System.out.println(formattedBody);
    }

    @Override
    public void showErrorMsg(String msg) {
        System.err.println(msg);
    }
}
