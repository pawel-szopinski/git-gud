package pl.pawelszopinski.result;

import com.cedarsoftware.util.io.JsonWriter;

import java.net.http.HttpResponse;

public class VerboseArrayResultCompiler extends ArrayResultCompiler {

    public VerboseArrayResultCompiler(String uri, String uriItemReplacement,
                                      String[] items, boolean authenticate) {
        super(uri, uriItemReplacement, items, authenticate);

    }

    public String compileJsonResult() throws Exception {
        StringBuilder result = new StringBuilder();

        for (String item : getItems()) {
            result.append(getSingleItemResult(item));
        }

        return result.toString();
    }

    private String getSingleItemResult(String item) throws Exception {
        String finalUri = getUri().replace(getUriItemReplacement(), item);

        HttpResponse<String> response =
                getHttpRequest().sendGet(finalUri, HttpResponse.BodyHandlers.ofString(), authenticate());

        return JsonWriter.formatJson(response.body());
    }
}
