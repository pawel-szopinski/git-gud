package pl.pawelszopinski.result;

import com.google.gson.Gson;
import org.apache.http.HttpStatus;
import pl.pawelszopinski.entity.Commit;
import pl.pawelszopinski.entity.Error;
import pl.pawelszopinski.entity.ParsedResult;

import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;

public class ParsedArrayResultCompiler extends ArrayResultCompiler {

    private final Gson gson = new Gson();

    public ParsedArrayResultCompiler(String uri, String uriItemReplacement,
                                     String[] items, boolean authenticate) {
        super(uri, uriItemReplacement, items, authenticate);
    }

    public List<ParsedResult> compileParsedResult() throws Exception {
        List<ParsedResult> resultList = new ArrayList<>();

        for (String item : getItems()) {
            resultList.add(getSingleItemResult(item));
        }

        return resultList;
    }

    private ParsedResult getSingleItemResult(String item) throws Exception {
        String finalUri = getUri().replace(getUriItemReplacement(), item);

        HttpResponse<String> response =
                getHttpRequest().sendGet(finalUri, HttpResponse.BodyHandlers.ofString(), authenticate());

        String body = response.body();
        int statusCode = response.statusCode();

        if (statusCode == HttpStatus.SC_OK) {
            return gson.fromJson(body, Commit.class);
        } else {
            Error error = gson.fromJson(body, Error.class);
            error.setItemId(item);
            error.setNumber(statusCode);

            return error;
        }
    }
}
