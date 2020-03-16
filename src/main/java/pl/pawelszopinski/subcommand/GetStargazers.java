package pl.pawelszopinski.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import pl.pawelszopinski.entity.ParsedResult;
import pl.pawelszopinski.entity.User;
import pl.pawelszopinski.handler.PrintHandler;
import pl.pawelszopinski.option.*;
import pl.pawelszopinski.result.ParsableResult;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;
import pl.pawelszopinski.result.VerboseResult;
import pl.pawelszopinski.result.paged.PagedParsedResultCompiler;
import pl.pawelszopinski.result.paged.PagedVerboseResultCompiler;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "stargazers", description = "Print users starring a given repository.")
public class GetStargazers implements Callable<Integer> {

    private String uri = "repos/{owner}/{repo}/stargazers?per_page=100&page=";

    @Mixin
    private Owner owner;

    @Mixin
    private Repository repository;

    @Mixin
    private Authenticate auth;

    @Mixin
    private Verbose verbose;

    @Mixin
    private Help help;

    @Option(names = {"-s", "--sort"},
            description = "Sort users by login (works only with non-verbose output).")
    private boolean sort;

    @Override
    public Integer call() throws Exception {
        uri = uri.replace("{owner}", owner.getOwner())
                .replace("{repo}", repository.getRepository());

        ResultCompilerBasicInfo basicInfo = new ResultCompilerBasicInfo(uri, auth.isAuth());

        if (verbose.isVerbose()) {
            String result = getVerboseResult(basicInfo);

            PrintHandler.printJson(result);
        } else {
            List<ParsedResult> result = getParsedResult(basicInfo);

            if (sort) {
                Collections.sort(result);
            }

            PrintHandler.printParsedResult(result);
        }

        return 0;
    }

    private List<ParsedResult> getParsedResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        ParsableResult resultCompiler = new PagedParsedResultCompiler(basicInfo);

        return resultCompiler.compileParsedResult(User.class);
    }

    private String getVerboseResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        VerboseResult resultCompiler = new PagedVerboseResultCompiler(basicInfo);

        return resultCompiler.compileJsonResult();
    }
}

