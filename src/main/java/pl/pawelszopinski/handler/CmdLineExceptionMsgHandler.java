package pl.pawelszopinski.handler;

import picocli.CommandLine;
import picocli.CommandLine.IExecutionExceptionHandler;
import picocli.CommandLine.ParseResult;

public class CmdLineExceptionMsgHandler implements IExecutionExceptionHandler {

    public int handleExecutionException(Exception ex, CommandLine commandLine,
                                        ParseResult parseResult) {

        commandLine.getErr().println(ex.getMessage());

        return commandLine.getExitCodeExceptionMapper() != null
                ? commandLine.getExitCodeExceptionMapper().getExitCode(ex)
                : commandLine.getCommandSpec().exitCodeOnExecutionException();
    }
}
