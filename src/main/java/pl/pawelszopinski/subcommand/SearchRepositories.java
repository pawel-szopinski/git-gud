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
import pl.pawelszopinski.parsedentity.Repository;
import pl.pawelszopinski.result.ParsableResult;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;
import pl.pawelszopinski.result.VerboseResult;
import pl.pawelszopinski.result.paged.PagedParsedResultCompiler;
import pl.pawelszopinski.result.paged.PagedVerboseResultCompiler;

import java.security.InvalidParameterException;
import java.util.List;
import java.util.concurrent.Callable;

@Command(name = "search-repos", description = "Search for GitHub repositories with different criteria.")
public class SearchRepositories implements Callable<Integer> {

    private StringBuilder uri = new StringBuilder().append("search/repositories?q=");

    @Option(names = {"-p", "--search-phrase"}, paramLabel = "<phrase>", description = "Repository " +
            "name or README file should contain this phrase.")
    private String searchPhrase;

    @Option(names = {"-u", "--user"}, paramLabel = "<name>", description = "User name " +
            "if owner is a user. Cannot be used with -organization option in the same query.")
    private String userName;

    @Option(names = {"-o", "--organization"}, paramLabel = "<name>", description = "Organization name " +
            "if owner is an organization. Cannot be used with -user option in the same query.")
    private String orgName;

    @Option(names = {"-c", "--created"}, paramLabel = "<date>", description = "Repository creation date. " +
            "Enter exact date or operator with date: >, <, >=, <=, .. (between, e.g. 2015-01-01..2137-01-01)")
    private String created;

    @Option(names = {"-l", "--language"}, paramLabel = "<name>", description = "Repository " +
            "should be written in this/these language(s). " +
            "If specified multiple times, the ''OR'' operator will be used.")
    private String[] languages;

    @Option(names = {"--license"}, paramLabel = "<keyword>", description = "Filter by license " +
            "or license family.")
    private String license;


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

            PrintHandler.printString(result);
        } else {
            List<ParsedResult> result = getParsedResult(basicInfo);

            PrintHandler.printParsedResult(result);
        }

        return 0;
    }

    private List<ParsedResult> getParsedResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        ParsableResult resultCompiler = new PagedParsedResultCompiler(basicInfo);

        return resultCompiler.compileParsedResult(Repository.class);
    }

    private String getVerboseResult(ResultCompilerBasicInfo basicInfo) throws Exception {
        VerboseResult resultCompiler = new PagedVerboseResultCompiler(basicInfo);

        return resultCompiler.compileJsonResult();
    }

    private void validateAndAddSearchCriteriaToUri() {
        if (StringUtils.isBlank(searchPhrase) && StringUtils.isBlank(userName) &&
                StringUtils.isBlank(orgName) && StringUtils.isBlank(created) &&
                StringUtils.isBlank(license) && languages == null) {
            throw new InvalidParameterException("At least one search criteria required!");
        }

        if (StringUtils.isNotBlank(orgName) && StringUtils.isNotBlank(userName)) {
            throw new InvalidParameterException("Searching both user name and organization name " +
                    "does not make sense. Please refine arguments in your next call!");
        }

        if (StringUtils.isNotBlank(searchPhrase)) {
            uri.append(searchPhrase).append("+");
        }

        if (StringUtils.isNotBlank(userName)) {
            uri.append("user:").append(userName).append("+");
        }

        if (StringUtils.isNotBlank(orgName)) {
            uri.append("org:").append(orgName).append("+");
        }

        if (StringUtils.isNotBlank(created)) {
            validateCreatedOptionValue();
            uri.append("created:").append(created).append("+");
        }

        if (languages != null) {
            for (String lang : languages) {
                if (StringUtils.isNotBlank(lang)) {
                    uri.append("language:").append(lang).append("+");
                }
            }
        }

        if (StringUtils.isNotBlank(license)) {
            uri.append("license:").append(license).append("+");
        }

        uri.deleteCharAt(uri.length() - 1);

        uri.append("&per_page=100&page=");
    }

    private void validateCreatedOptionValue() {
        String dateRegex = "[\\d]{4}-[\\d]{1,2}-[\\d]{1,2}";

        if (!created.matches(dateRegex) &&
                !created.matches("(>=|<=|>|<)" + dateRegex) &&
                !created.matches(dateRegex + "\\.\\." + dateRegex)) {
            throw new InvalidParameterException(
                    "Invalid value assigned to --created option!");
        }
    }
}
