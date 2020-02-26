package pl.pawelszopinski.view;

import com.cedarsoftware.util.io.JsonWriter;

public class ConsoleDisplayService implements DisplayService {

    //    public static final String ANSI_CYAN = "\u001B[96m";
    public static final String ANSI_YELLOW = "\u001B[93m";
    public static final String ANSI_RED = "\u001B[31m";
    public static final String ANSI_RESET = "\u001B[0m";

    @Override
    public void showJson(String body) {
        String formattedBody = JsonWriter.formatJson(body);

        System.out.println(formattedBody);
    }

    @Override
    public void showError(String msg) {
        System.out.print(ANSI_RED + "ERROR: " + msg);
        System.out.println(ANSI_RESET);
    }

    @Override
    public void showWarning(String msg) {
        System.out.print(ANSI_YELLOW + "WARNING: " + msg);
        System.out.println(ANSI_RESET);
    }
}
