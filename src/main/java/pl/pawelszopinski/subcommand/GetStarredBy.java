package pl.pawelszopinski.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import pl.pawelszopinski.handler.PrintHandler;
import pl.pawelszopinski.option.Authenticate;
import pl.pawelszopinski.option.Help;
import pl.pawelszopinski.option.Verbose;
import pl.pawelszopinski.parsedentity.ParsedResult;
import pl.pawelszopinski.parsedentity.Repository;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;
import pl.pawelszopinski.result.paged.PagedParsedResultCompiler;
import pl.pawelszopinski.result.paged.PagedVerboseResultCompiler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "starred-by", description = "Get repositories starred by a user.")
public class GetStarredBy implements Callable<Integer> {

    private String uri = "/users/{username}/starred?per_page=100&page=";

    @Spec
    private CommandSpec spec;

    @Option(names = {"-u", "--user"}, required = true, paramLabel = "<name>", description = "Users's " +
            "account name.")
    private String userName;

    @Mixin
    private Authenticate auth;

    @Mixin
    private Verbose verbose;

    @Mixin
    private Help help;

    @Option(names = {"-s", "--sort"},
            description = "Sort repositories by name (works only with non-verbose output).")
    private boolean sort;

    @Override
    public Integer call() throws Exception {
        uri = uri.replace("{username}", URLEncoder.encode(userName, StandardCharsets.UTF_8));

        ResultCompilerBasicInfo basicInfo = new ResultCompilerBasicInfo(uri, auth.isAuth());

        if (verbose.isVerbose()) {
            String result = getVerboseResult(basicInfo);

            PrintHandler.printString(result, spec);
        } else {
            List<ParsedResult> result = getParsedResult(basicInfo);

            if (sort) {
                Collections.sort(result);
            }

            PrintHandler.printParsedResult(result, spec);
        }

        return 0;
    }

    private List<ParsedResult> getParsedResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        PagedParsedResultCompiler resultCompiler = new PagedParsedResultCompiler(basicInfo);

        return resultCompiler.compileParsedResult(Repository.class);
    }

    private String getVerboseResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        PagedVerboseResultCompiler resultCompiler = new PagedVerboseResultCompiler(basicInfo);

        return resultCompiler.compileJsonResult();
    }
}
