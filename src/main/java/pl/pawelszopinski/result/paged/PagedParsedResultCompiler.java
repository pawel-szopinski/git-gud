package pl.pawelszopinski.result.paged;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.http.HttpException;
import org.apache.http.HttpStatus;
import pl.pawelszopinski.parsedentity.ErrorResult;
import pl.pawelszopinski.parsedentity.ParsedResult;
import pl.pawelszopinski.result.ParsableResult;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;
import pl.pawelszopinski.util.LastPageExtractor;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class PagedParsedResultCompiler implements ParsableResult {

    private final Gson gson = new Gson();
    private final ResultCompilerBasicInfo basicInfo;

    public PagedParsedResultCompiler(ResultCompilerBasicInfo basicInfo) {
        this.basicInfo = basicInfo;
    }

    @Override
    public <T extends ParsedResult> List<ParsedResult> compileParsedResult(Class<T> type)
            throws Exception {
        List<ParsedResult> resultList = new ArrayList<>();
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

            linkHeader = response.headers().firstValue("link").orElse(null);

            body = trimJsonStringIfActualObjectsNested(response.body());

            resultList.addAll(gson.fromJson(body, TypeToken.getParameterized(List.class, type).getType()));

            pageNo++;
        } while (linkHeader != null && LastPageExtractor.getLastPage(linkHeader) >= pageNo);

        return resultList;
    }

    private String trimJsonStringIfActualObjectsNested(String body) {
        if (body.startsWith("[")) {
            return body;
        }

        body = body.substring(body.indexOf("["));

        return body.substring(0, body.lastIndexOf("]") + 1);
    }
}
