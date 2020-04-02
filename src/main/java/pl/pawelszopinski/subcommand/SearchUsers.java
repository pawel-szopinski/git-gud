package pl.pawelszopinski.subcommand;

import org.apache.commons.lang3.StringUtils;
import picocli.CommandLine.Command;
import picocli.CommandLine.Mixin;
import picocli.CommandLine.Model.CommandSpec;
import picocli.CommandLine.Option;
import picocli.CommandLine.Spec;
import pl.pawelszopinski.handler.PrintHandler;
import pl.pawelszopinski.option.Authenticate;
import pl.pawelszopinski.option.Help;
import pl.pawelszopinski.option.Verbose;
import pl.pawelszopinski.parsedentity.ParsedSearchResult;
import pl.pawelszopinski.parsedentity.User;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;
import pl.pawelszopinski.result.paged.PagedParsedSearchResultCompiler;
import pl.pawelszopinski.result.paged.PagedVerboseResultCompiler;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "search-users", description = "Search for GitHub users with different criteria.")
public class SearchUsers implements Callable<Integer> {

    private StringBuilder uri = new StringBuilder().append("/search/users?q=");

    @Spec
    private CommandSpec spec;

    @Option(names = {"-u", "--user"}, paramLabel = "<name>", description = "User's " +
            "account name or email should contain this phrase.")
    private String user;

    @Option(names = {"-r", "--no-of-repos"}, paramLabel = "<count>", description = "Number of repos " +
            "a user should have. Enter exact integer or operator with integer: " +
            ">, <, >=, <=, .. (between, e.g. 10..30)")
    private String noOfRepos;

    @Option(names = {"-L", "--location"}, paramLabel = "<name>", description = "User's " +
            "location, e.g. Poland.")
    private String[] locations;

    @Option(names = {"-l", "--language"}, paramLabel = "<name>", description = "User " +
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
        validateAndAddSearchCriteriaToUri();

        ResultCompilerBasicInfo basicInfo = new ResultCompilerBasicInfo(uri.toString(), auth.isAuth());

        if (verbose.isVerbose()) {
            String result = getVerboseResult(basicInfo);

            PrintHandler.printString(result, spec);
        } else {
            List<ParsedSearchResult<User>> result = getParsedResult(basicInfo);

            PrintHandler.printParsedResult(result, spec);
        }

        return 0;
    }

    private List<ParsedSearchResult<User>> getParsedResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        PagedParsedSearchResultCompiler resultCompiler = new PagedParsedSearchResultCompiler(basicInfo);

        return resultCompiler.compileParsedSearchResult(User.class);
    }

    private String getVerboseResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        PagedVerboseResultCompiler resultCompiler = new PagedVerboseResultCompiler(basicInfo);

        return resultCompiler.compileJsonResult();
    }

    private void validateAndAddSearchCriteriaToUri() {
        if (StringUtils.isBlank(user) && StringUtils.isBlank(noOfRepos) &&
                locations == null && languages == null) {
            throw new InvalidParameterException("At least one search criteria required!");
        }

        if (StringUtils.isNotBlank(user)) {
            uri.append(URLEncoder.encode(user, StandardCharsets.UTF_8)).append("+");
        }

        if (StringUtils.isNotBlank(noOfRepos)) {
            validateNoOfRepos();
            uri.append("repos:").append(URLEncoder.encode(noOfRepos, StandardCharsets.UTF_8)).append("+");
        }

        if (locations != null) {
            for (String loc : locations) {
                if (StringUtils.isNotBlank(loc)) {
                    uri.append("location:")
                            .append(URLEncoder.encode(loc, StandardCharsets.UTF_8)).append("+");
                }
            }
        }

        if (languages != null) {
            for (String lang : languages) {
                if (StringUtils.isNotBlank(lang)) {
                    uri.append("language:")
                            .append(URLEncoder.encode(lang, StandardCharsets.UTF_8)).append("+");
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
                    "Invalid value assigned to --no-of-repos option!");
        }
    }
}
