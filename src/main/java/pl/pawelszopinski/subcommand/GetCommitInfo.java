package pl.pawelszopinski.subcommand;

import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Parameters;
import pl.pawelszopinski.entity.Commit;
import pl.pawelszopinski.entity.ParsedResult;
import pl.pawelszopinski.handler.PrintHandler;
import pl.pawelszopinski.option.*;
import pl.pawelszopinski.result.ParsedArrayResultCompiler;
import pl.pawelszopinski.result.VerboseArrayResultCompiler;

import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "commit-info", description = "Print info about one or more commits.")
public class GetCommitInfo implements Callable<Integer> {

    private final static String URI_ITEM_REPLACEMENT = "{commit_sha}";

    private String uri = "repos/{owner}/{repo}/git/commits/{commit_sha}";

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
        uri = uri.replace("{owner}", owner.getOwner())
                .replace("{repo}", repository.getRepository());

        if (verbose.isVerbose()) {
            String result = getVerboseResult();

            PrintHandler.printJson(result);
        } else {
            List<ParsedResult> result = getParsedResult();

            PrintHandler.printParsedResult(result);
        }

        return 0;
    }

    private String getVerboseResult() throws Exception {
        VerboseArrayResultCompiler resultCompiler = new VerboseArrayResultCompiler(
                uri, URI_ITEM_REPLACEMENT, shaArray, auth.isAuth());

        return resultCompiler.compileJsonResult();
    }

    private List<ParsedResult> getParsedResult() throws Exception {
        ParsedArrayResultCompiler resultCompiler = new ParsedArrayResultCompiler(
                uri, URI_ITEM_REPLACEMENT, shaArray, auth.isAuth());

        return resultCompiler.compileParsedResult(Commit.class);
    }
}