package pl.pawelszopinski.handler;

import java.util.List;

public class PrintHandler {

    private static final String ANSI_CYAN = "\u001B[96m";
    //    private static final String ANSI_YELLOW = "\u001B[93m";
    private static final String ANSI_RED = "\u001B[31m";
    private static final String ANSI_RESET = "\u001B[0m";

    public static void printJson(String body) {
        String formattedBody = body.replace("}{", "}\n{");

        System.out.println(formattedBody);
    }

    public static <T> void printParsedResult(List<T> resultList) {
        for (T single : resultList) {
            System.out.println(single.toString());
        }
    }

    public static void printException(String msg) {
        System.out.print(ANSI_RED + msg);
        System.out.println(ANSI_RESET);
    }

    public static void printInfo(String msg) {
        System.out.print(ANSI_CYAN + msg);
        System.out.println(ANSI_RESET);
        System.out.println();
    }
}
