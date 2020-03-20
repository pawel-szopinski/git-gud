package pl.pawelszopinski.result.array;

import com.cedarsoftware.util.io.JsonWriter;
import pl.pawelszopinski.result.ResultCompilerBasicInfo;

import java.net.http.HttpResponse;

public class ArrayVerboseResultCompiler {

    private final ResultCompilerBasicInfo basicInfo;
    private final String[] items;
    private final String uriItemReplacement;

    public ArrayVerboseResultCompiler(ResultCompilerBasicInfo basicInfo, String[] items,
                                      String uriItemReplacement) {
        this.basicInfo = basicInfo;
        this.items = items;
        this.uriItemReplacement = uriItemReplacement;
    }

    public String compileJsonResult(boolean searchResult) throws Exception {
        StringBuilder result = new StringBuilder();

        for (String item : items) {
            result.append("\n\n").append("item: ").append(item).append("\n")
                    .append(getSingleItemResult(item));
        }

        return result.toString().substring(2);
    }

    private String getSingleItemResult(String item) throws Exception {
        String finalUri = basicInfo.getUri().replace(uriItemReplacement, item);

        HttpResponse<String> response =
                basicInfo.getHttpRequest().sendGet(finalUri, basicInfo.isAuthenticate());

        return JsonWriter.formatJson(response.body());
    }
}
