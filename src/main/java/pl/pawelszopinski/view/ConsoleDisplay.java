package pl.pawelszopinski.view;

import com.cedarsoftware.util.io.JsonWriter;

import java.net.http.HttpResponse;
import java.util.logging.Level;
import java.util.logging.Logger;

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

    @Override
    public void showWarning(String msg) {
        Logger logger = Logger.getLogger(ConsoleDisplay.class.getName());
        logger.setLevel(Level.WARNING);
        logger.warning(msg);
    }
}
