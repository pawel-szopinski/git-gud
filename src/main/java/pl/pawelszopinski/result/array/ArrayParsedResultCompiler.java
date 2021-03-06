package pl.pawelszopinski.result.array;

import com.google.gson.Gson;
import pl.pawelszopinski.parsedentity.ErrorResult;
import pl.pawelszopinski.parsedentity.ParsedResult;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ArrayParsedResultCompiler {

    private final Gson gson = new Gson();
    private final ResultCompilerBasicInfo basicInfo;
    private final String[] items;
    private final String uriItemReplacement;

    public ArrayParsedResultCompiler(ResultCompilerBasicInfo resultCompilerBasicInfo, String[] items,
                                     String uriItemReplacement) {
        this.basicInfo = resultCompilerBasicInfo;
        this.items = items;
        this.uriItemReplacement = uriItemReplacement;
    }

    public <T extends ParsedResult> List<ParsedResult> compileParsedResult(Class<T> type)
            throws Exception {
        List<ParsedResult> resultList = new ArrayList<>();

        for (String item : items) {
            resultList.add(getSingleItemResult(item, type));
        }

        return resultList;
    }

    private <T extends ParsedResult> ParsedResult getSingleItemResult(String item, Class<T> type)
            throws Exception {
        String finalUri = basicInfo.getUri().replace(uriItemReplacement, item);

        HttpResponse<String> response = basicInfo.getHttpRequest().sendGet(
                finalUri, basicInfo.isAuthenticate());

        String body = response.body();
        int statusCode = response.statusCode();

        if (statusCode / 100 == 2) {
            return gson.fromJson(body, type);
        } else {
            ErrorResult errorResult = gson.fromJson(body, ErrorResult.class);
            errorResult.setItem(item);
            errorResult.setNumber(statusCode);

            return errorResult;
        }
    }
}
