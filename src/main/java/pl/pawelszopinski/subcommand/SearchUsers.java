package pl.pawelszopinski.subcommand;

import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Option;
import pl.pawelszopinski.handler.PrintHandler;
import pl.pawelszopinski.option.Authenticate;
import pl.pawelszopinski.option.Help;
import pl.pawelszopinski.option.Verbose;
import pl.pawelszopinski.parsedentity.ParsedResult;
import pl.pawelszopinski.parsedentity.User;
import pl.pawelszopinski.result.ParsableResult;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;
import pl.pawelszopinski.result.VerboseResult;
import pl.pawelszopinski.result.paged.PagedParsedResultCompiler;
import pl.pawelszopinski.result.paged.PagedVerboseResultCompiler;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "search-users", description = "Search for github users with different criteria.")
public class SearchUsers implements Callable<Integer> {

    private StringBuilder uri = new StringBuilder().append("search/users?q=");

    @Option(names = {"-u", "--user"}, paramLabel = "<name>", description = "Users' " +
            "account name or email should contain this phrase.")
    private String user;

    @Option(names = {"-r", "--no-of-repos"}, paramLabel = "<count>", description = "Number of repos " +
            "users should have. Enter exact integer or operator with integer: " +
            ">, <, >=, <=, .. (between, e.g. 10..30)")
    private String noOfRepos;

    @Option(names = {"-L", "--location"}, paramLabel = "<name>", description = "Users' " +
            "location, e.g. Poland.")
    private String[] locations;

    @Option(names = {"-l", "--language"}, paramLabel = "<name>", description = "Users' " +
            "should have repositories written in this/these language(s).")
    private String[] languages;

    @Mixin
    private Authenticate auth;

    @Mixin
    private Verbose verbose;

    @Mixin
    private Help help;

    @Override
    public Integer call() throws Exception {
        addSearchCriteriaToUri();

        ResultCompilerBasicInfo basicInfo = new ResultCompilerBasicInfo(uri.toString(), auth.isAuth());

        if (verbose.isVerbose()) {
            String result = getVerboseResult(basicInfo);

            PrintHandler.printString(result);
        } else {
            List<ParsedResult> result = getParsedResult(basicInfo);

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

    private void addSearchCriteriaToUri() {
        if (StringUtils.isBlank(user) && StringUtils.isBlank(noOfRepos) &&
                locations == null && languages == null) {
            throw new InvalidParameterException("At least one search criteria required!");
        }

        if (StringUtils.isNotBlank(user)) {
            uri.append(user).append("+");
        }

        if (StringUtils.isNotBlank(noOfRepos)) {
            validateNoOfRepos();
            uri.append("repos:").append(noOfRepos).append("+");
        }

        if (locations != null) {
            for (String loc : locations) {
                if (StringUtils.isNotBlank(loc)) {
                    uri.append("location:").append(loc).append("+");
                }
            }
        }

        if (languages != null) {
            for (String lang : languages) {
                if (StringUtils.isNotBlank(lang)) {
                    uri.append("language:").append(lang).append("+");
                }
            }
        }

        uri.deleteCharAt(uri.length() - 1);

        uri.append("&per_page=100&page=");
    }

    private void validateNoOfRepos() {
        if (!StringUtils.isNumeric(noOfRepos) &&
                !noOfRepos.matches("(>=|<=|>|<)[\\d]+") &&
                !noOfRepos.matches("[\\d]+\\.\\.[\\d]+")) {
            throw new InvalidParameterException(
                    "Invalid value assigned to no-of-repos option!");
        }
    }
}
