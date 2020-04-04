package pl.pawelszopinski.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Parameters;
import picocli.CommandLine.Spec;
import pl.pawelszopinski.handler.PrintHandler;
import pl.pawelszopinski.option.*;
import pl.pawelszopinski.parsedentity.Commit;
import pl.pawelszopinski.parsedentity.ParsedResult;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;
import pl.pawelszopinski.result.array.ArrayParsedResultCompiler;
import pl.pawelszopinski.result.array.ArrayVerboseResultCompiler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "commit-info", description = "Get info about one or more commits.")
public class GetCommitInfo implements Callable<Integer> {

    private final static String URI_ITEM_REPLACEMENT = "{commit_sha}";

    private String uri = "/repos/{owner}/{repo}/git/commits/{commit_sha}";

    @Spec
    private CommandSpec spec;

    @Mixin
    private Owner owner;

    @Mixin
    private Repository repository;

    @Parameters(arity = "1..*", description = "One or more commit SHAs.")
    private String[] shaArray;

    @Mixin
    private Authenticate auth;

    @Mixin
    private Verbose verbose;

    @Mixin
    private Help help;

    @Override
    public Integer call() throws Exception {
        uri = uri
                .replace("{owner}",
                        URLEncoder.encode(owner.getOwner(), StandardCharsets.UTF_8))
                .replace("{repo}",
                        URLEncoder.encode(repository.getRepository(), StandardCharsets.UTF_8));

        for (int i = 0; i < shaArray.length; i++) {
            shaArray[i] = URLEncoder.encode(shaArray[i], StandardCharsets.UTF_8);
        }

        ResultCompilerBasicInfo basicInfo = new ResultCompilerBasicInfo(uri, auth.isAuth());

        if (verbose.isVerbose()) {
            String result = getVerboseResult(basicInfo);

            PrintHandler.printString(result, spec);
        } else {
            List<ParsedResult> result = getParsedResult(basicInfo);

            PrintHandler.printParsedResult(result, spec);
        }

        return 0;
    }

    private String getVerboseResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        ArrayVerboseResultCompiler resultCompiler = new ArrayVerboseResultCompiler(
                basicInfo, shaArray, URI_ITEM_REPLACEMENT);

        return resultCompiler.compileJsonResult();
    }

    private List<ParsedResult> getParsedResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        ArrayParsedResultCompiler resultCompiler = new ArrayParsedResultCompiler(
                basicInfo, shaArray, URI_ITEM_REPLACEMENT);

        return resultCompiler.compileParsedResult(Commit.class);
    }
}