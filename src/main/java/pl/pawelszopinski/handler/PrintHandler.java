package pl.pawelszopinski.handler;

import picocli.CommandLine.Model.CommandSpec;

import java.util.List;

public class PrintHandler {

    public static void printString(String text, CommandSpec spec) {
        spec.commandLine().getOut().println(text);
    }

    public static <T> void printParsedResult(List<T> resultList, CommandSpec spec) {
        for (T single : resultList) {
            spec.commandLine().getOut().println(single.toString());
        }
    }

    public static void printException(String msg) {
        System.err.println(msg);
    }
}
