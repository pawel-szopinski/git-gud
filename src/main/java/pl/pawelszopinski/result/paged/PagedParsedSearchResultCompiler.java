package pl.pawelszopinski.result.paged;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import pl.pawelszopinski.config.Configuration;
import pl.pawelszopinski.parsedentity.ErrorResult;
import pl.pawelszopinski.parsedentity.ParsedResult;
import pl.pawelszopinski.parsedentity.ParsedSearchResult;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;
import pl.pawelszopinski.util.LastPageExtractor;

import java.net.http.HttpResponse;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;

public class PagedParsedSearchResultCompiler {

    private static final int SEARCH_LIMIT = Configuration.getSearchLimit();

    private final Gson gson = new Gson();
    private final ResultCompilerBasicInfo basicInfo;

    public PagedParsedSearchResultCompiler(ResultCompilerBasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    public <T extends ParsedResult> List<ParsedSearchResult<T>> compileParsedSearchResult(Class<T> type)
            throws Exception {
        List<ParsedSearchResult<T>> resultList = new ArrayList<>();
        HttpResponse<String> response;
        String linkHeader;

        int pageNo = 1;

        do {
            response = basicInfo.getHttpRequest().sendGet(basicInfo.getUri() + pageNo,
                    basicInfo.isAuthenticate());

            String body = response.body();
            int statusCode = response.statusCode();

            if (statusCode != HttpStatus.SC_OK) {
                ErrorResult errorResult = gson.fromJson(body, ErrorResult.class);
                errorResult.setItem("Result page " + pageNo);
                errorResult.setNumber(statusCode);

                throw new HttpException("Error while fetching data from GitHub. " +
                        "Details: " + errorResult.toString());
            }

            ParsedSearchResult<T> pageObj = gson.fromJson(body,
                    TypeToken.getParameterized(ParsedSearchResult.class, type).getType());

            if (pageObj.getTotalCount() > SEARCH_LIMIT) {
                throw new InvalidParameterException("Search result yields more than " + SEARCH_LIMIT +
                        " records, which is current GitHub limit. Please refine your search, so you do " +
                        "not miss any data in the output.");
            }

            resultList.add(pageObj);

            linkHeader = response.headers().firstValue("link").orElse(null);

            pageNo++;
        } while (linkHeader != null && LastPageExtractor.getLastPage(linkHeader) >= pageNo);

        return resultList;
    }
}
