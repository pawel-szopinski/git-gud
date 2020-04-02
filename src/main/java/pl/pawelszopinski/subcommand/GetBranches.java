package pl.pawelszopinski.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import pl.pawelszopinski.handler.PrintHandler;
import pl.pawelszopinski.option.*;
import pl.pawelszopinski.parsedentity.Branch;
import pl.pawelszopinski.parsedentity.ParsedResult;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;
import pl.pawelszopinski.result.paged.PagedParsedResultCompiler;
import pl.pawelszopinski.result.paged.PagedVerboseResultCompiler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "branches", description = "Get branches available in a repository.")
public class GetBranches implements Callable<Integer> {

    private String uri = "/repos/{owner}/{repo}/branches?per_page=100&page=";

    @Spec
    private CommandSpec spec;

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
            description = "Sort branches by name (works only with non-verbose output).")
    private boolean sort;

    @Override
    public Integer call() throws Exception {
        uri = uri
                .replace("{owner}",
                        URLEncoder.encode(owner.getOwner(), StandardCharsets.UTF_8))
                .replace("{repo}",
                        URLEncoder.encode(repository.getRepository(), StandardCharsets.UTF_8));

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

        return resultCompiler.compileParsedResult(Branch.class, false);
    }

    private String getVerboseResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        PagedVerboseResultCompiler resultCompiler = new PagedVerboseResultCompiler(basicInfo);

        return resultCompiler.compileJsonResult();
    }
}
