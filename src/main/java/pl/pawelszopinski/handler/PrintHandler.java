package pl.pawelszopinski.handler;

import java.util.List;

public class PrintHandler {

    public static void printString(String text) {
        System.out.println(text);
    }

    public static <T> void printParsedResult(List<T> resultList) {
        for (T single : resultList) {
            System.out.println(single.toString());
        }
    }

    public static void printException(String msg) {
        System.err.println(msg);
    }
}
