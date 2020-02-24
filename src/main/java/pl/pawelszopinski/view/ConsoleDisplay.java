package pl.pawelszopinski.view;

import com.cedarsoftware.util.io.JsonWriter;

import java.net.http.HttpResponse;

public class ConsoleDisplay implements Displayable {

    //    public static final String ANSI_CYAN = "\u001B[96m";
    public static final String ANSI_YELLOW = "\u001B[93m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    @Override
    public void showJson(HttpResponse<String> response) {
        String formattedBody = JsonWriter.formatJson(String.valueOf(response.body()));

        System.out.println();
        System.out.println(formattedBody);
        System.out.println();
    }

    @Override
    public void showErrorMsg(String msg) {
        System.out.print(ANSI_RED + "ERROR: " + msg);
        System.out.println(ANSI_RESET);
    }

    @Override
    public void showWarning(String msg) {
        System.out.print(ANSI_YELLOW + "WARNING: " + msg);
        System.out.println(ANSI_RESET);
    }
}
